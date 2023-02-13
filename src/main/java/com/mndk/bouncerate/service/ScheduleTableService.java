package com.mndk.bouncerate.service;

import com.mndk.bouncerate.db.AltStreamCalculationDAO;
import com.mndk.bouncerate.db.ScheduleTableDAO;
import com.mndk.bouncerate.db.SetTopBoxesDAO;
import com.mndk.bouncerate.util.MinMax;
import com.mndk.bouncerate.util.Validator;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Arrays;

@Service
@AllArgsConstructor
public class ScheduleTableService {


    private static final int TIME_SLOT_COUNT = 6;
    private static final int ALT_STREAM_COUNT = 3;


    final AltStreamCalculationDAO tableCalculationDAO;
    final ScheduleTableDAO scheduleTableDAO;
    final SetTopBoxesDAO setTopBoxesDAO;


    // ===== GETTERS =====

    public Integer[][] getEntireTable() {
        var scheduleNodes = scheduleTableDAO.getAll();

        int timeSlotCount = TIME_SLOT_COUNT;
        int altStreamCount = ALT_STREAM_COUNT;

        var table = new Integer[6][];
        for(int i = 0; i < timeSlotCount; i++) {
            table[i] = new Integer[altStreamCount + 1];
            Arrays.fill(table[i], null);
        }
        for(var scheduleNode : scheduleNodes) {
            int timeSlotId = scheduleNode.getTimeSlotId(), streamNumber = scheduleNode.getStreamNumber();

            if(timeSlotId < 0 || timeSlotId > timeSlotCount - 1) continue;
            if(streamNumber < 0 || streamNumber > altStreamCount) continue;

            table[timeSlotId][streamNumber] = scheduleNode.getCategoryId();
        }

        return table;
    }


    @Nullable
    public ScheduleTableDAO.TimeSlotBounceRate getTimeSlotBounceRate(
            int timeSlotId,
            boolean updateIfOutdated,
            MinMax<Double> bounceRateRange
    ) {
        validateTimeSlot(timeSlotId);
        var bounceRate = scheduleTableDAO.getTimeSlotBounceRate(timeSlotId);
        if(updateIfOutdated && (bounceRate == null || bounceRate.isNeedsUpdate())) {
            Validator.checkNull(
                    bounceRateRange,
                    () -> new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bounce rate range cannot be null")
            );

            var newBounceRate = this.calculateTimeSlotBounceRate(timeSlotId, bounceRateRange);
            this.updateTimeSlotBounceRate(timeSlotId, newBounceRate);
            return newBounceRate;
        }
        return bounceRate;
    }


    public ScheduleTableDAO.TimeSlotBounceRate[] getAllTimeSlotBounceRates() {
        var resultArray = new ScheduleTableDAO.TimeSlotBounceRate[TIME_SLOT_COUNT];
        for(int i = 0; i < TIME_SLOT_COUNT; i++) {
            resultArray[i] = this.getTimeSlotBounceRate(
                    i, false, null
            );
        }
        return resultArray;
    }


    // ===== CALCULATORS =====

    public ScheduleTableDAO.TimeSlotBounceRate calculateTimeSlotBounceRate(
            int timeSlotId, MinMax<Double> bounceRateRange
    ) {
        return this.calculateAltStreamsOfTimeSlot(
                timeSlotId,
                (streamNumber) -> scheduleTableDAO.getCategoryId(timeSlotId, streamNumber),
                bounceRateRange.min(), bounceRateRange.max()
        ).bounceRate();
    }


    public AltStreamCalculationResult calculateAltStreamsOfTimeSlot(
            int timeSlotId, double minBounceRate, double maxBounceRate
    ) {
        return this.calculateAltStreamsOfTimeSlot(
                timeSlotId,
                (streamNumber) -> tableCalculationDAO.getCurrentBestCategoryId(timeSlotId, minBounceRate, maxBounceRate),
                minBounceRate, maxBounceRate
        );
    }


    public record AltStreamCalculationResult(
            Integer[] altStreams,
            ScheduleTableDAO.TimeSlotBounceRate bounceRate
    ) {}
    interface ProductCategoryIdFunction {
        Integer get(int streamNumber);
    }
    /**
     * @param timeSlotId The ID of the Time slot to calculate the tables
     * @param minBounceRate Lowest accepted value of the set-top boxes' bounce rate
     * @param maxBounceRate Highest accepted value of the set-top boxes' bounce rate
     * @return The list of category IDs for the alternative stream; null if the calculation fails
     */
    public AltStreamCalculationResult calculateAltStreamsOfTimeSlot(
            int timeSlotId, ProductCategoryIdFunction categoryIdFunction,
            double minBounceRate, double maxBounceRate
    ) {
        validateTimeSlot(timeSlotId);

        synchronized (tableCalculationDAO) {
            int totalSetTopBoxesCount = setTopBoxesDAO.getCount();
            tableCalculationDAO.resetCalculationTable();

            Integer categoryId = scheduleTableDAO.getCategoryId(timeSlotId, 0);
            if(categoryId == null) return null;

            tableCalculationDAO.initializeData(categoryId, minBounceRate, maxBounceRate);
            tableCalculationDAO.excludeCategory(categoryId);
            int defaultCapturedSetTopBoxesCount = tableCalculationDAO.getCurrentCapturedCount();

            Integer[] altStreamArray = new Integer[ALT_STREAM_COUNT];
            Arrays.fill(altStreamArray, null);

            for (int i = 0; i < ALT_STREAM_COUNT; i++) {
                Integer altCategoryId = categoryIdFunction.get(i + 1);
                if(altCategoryId == null) continue;

                altStreamArray[i] = altCategoryId;
                tableCalculationDAO.setSetTopBoxesCaptured(altCategoryId, minBounceRate, maxBounceRate);
                tableCalculationDAO.excludeCategory(altCategoryId);
            }
            int altCapturedSetTopBoxesCount = tableCalculationDAO.getCurrentCapturedCount();

            var bounceRate = new ScheduleTableDAO.TimeSlotBounceRate(
                    getBounceRatePercentage(defaultCapturedSetTopBoxesCount, totalSetTopBoxesCount),
                    getBounceRatePercentage(altCapturedSetTopBoxesCount, totalSetTopBoxesCount),
                    false
            );

            return new AltStreamCalculationResult(altStreamArray, bounceRate);
        }
    }


    // ===== SETTERS =====

    public void setScheduleStream(int timeSlotId, int streamNumber, int categoryId) {
        try {
            validateTimeSlot(timeSlotId);
            validateStreamNumber(streamNumber);
            scheduleTableDAO.insertNode(new ScheduleTableDAO.ScheduleNode(timeSlotId, streamNumber, categoryId));
            scheduleTableDAO.markTimeSlotBounceRateOutdated(timeSlotId);
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Cannot find a category with ID #" + categoryId);
        }
    }


    public void setAlternativeStreams(int timeSlotId, Integer[] categoryIdList) {
        validateTimeSlot(timeSlotId);
        for(int i = 0; i < categoryIdList.length; i++) {
            Integer category = categoryIdList[i];
            if(category == null) continue;

            setScheduleStream(timeSlotId, i + 1, category);
        }
    }


    public void updateTimeSlotBounceRate(int timeSlotId, ScheduleTableDAO.TimeSlotBounceRate bounceRate) {
        validateTimeSlot(timeSlotId);
        scheduleTableDAO.updateTimeSlotBounceRate(timeSlotId, bounceRate);
    }


    // ===== REMOVERS =====

    public void removeScheduleStream(int timeSlotId, int streamNumber) {
        validateTimeSlot(timeSlotId);
        validateStreamNumber(streamNumber);
        scheduleTableDAO.deleteNode(timeSlotId, streamNumber);
    }


    // ===== MISC =====

    private void validateTimeSlot(int timeSlotId) {
        if(timeSlotId < 0 || timeSlotId > TIME_SLOT_COUNT - 1) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Invalid time slot ID!");
        }
    }


    private void validateStreamNumber(int streamNumber) {
        if(streamNumber < 0 || streamNumber > ALT_STREAM_COUNT) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Invalid stream number!");
        }
    }


    private double getBounceRatePercentage(int captured, int total) {
        return (1 - (captured + 1) / (double) (total + 2)) * 100;
    }

}
