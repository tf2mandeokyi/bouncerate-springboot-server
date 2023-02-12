package com.mndk.bouncerate.service;

import com.mndk.bouncerate.db.AltStreamCalculationDAO;
import com.mndk.bouncerate.db.ScheduleTableDAO;
import com.mndk.bouncerate.db.SetTopBoxesDAO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Arrays;
import java.util.List;

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
        List<ScheduleTableDAO.ScheduleNode> nodes = scheduleTableDAO.getAll();

        int timeSlotCount = TIME_SLOT_COUNT;
        int altStreamCount = ALT_STREAM_COUNT;

        Integer[][] table = new Integer[6][];
        for(int i = 0; i < timeSlotCount; i++) {
            table[i] = new Integer[altStreamCount + 1];
            Arrays.fill(table[i], null);
        }
        for(ScheduleTableDAO.ScheduleNode node : nodes) {
            int timeSlotId = node.getTimeSlotId(), streamNumber = node.getStreamNumber();

            if(timeSlotId < 0 || timeSlotId > timeSlotCount - 1) continue;
            if(streamNumber < 0 || streamNumber > altStreamCount) continue;

            table[timeSlotId][streamNumber] = node.getCategoryId();
        }

        return table;
    }


    public record TimeSlotBounceRate(Double onlyDefault, Double withAlt) {}
    public TimeSlotBounceRate getTimeSlotBounceRate(int timeSlotId, double minBounceRate, double maxBounceRate) {
        validateTimeSlot(timeSlotId);

        synchronized (tableCalculationDAO) {
            int totalSetTopBoxesCount = setTopBoxesDAO.getCount();

            tableCalculationDAO.resetCalculationTable();

            Integer categoryId = scheduleTableDAO.getCategoryId(timeSlotId, 0);
            if(categoryId == null) return null;

            tableCalculationDAO.initializeData(categoryId, minBounceRate, maxBounceRate);
            int defaultCapturedSetTopBoxesCount = tableCalculationDAO.getCurrentCapturedCount();

            for(int i = 0; i < ALT_STREAM_COUNT; i++) {
                categoryId = scheduleTableDAO.getCategoryId(timeSlotId, i + 1);
                if(categoryId == null) return null;

                tableCalculationDAO.setSetTopBoxesCaptured(categoryId, minBounceRate, maxBounceRate);
            }

            int altCapturedSetTopBoxesCount = tableCalculationDAO.getCurrentCapturedCount();

            return new TimeSlotBounceRate(
                    getBounceRatePercentage(defaultCapturedSetTopBoxesCount, totalSetTopBoxesCount),
                    getBounceRatePercentage(altCapturedSetTopBoxesCount, totalSetTopBoxesCount)
            );
        }
    }


    /**
     * @param timeSlotId The ID of the Time slot to calculate the tables
     * @param minBounceRate Lowest accepted value of the set-top boxes' bounce rate
     * @param maxBounceRate Highest accepted value of the set-top boxes' bounce rate
     * @return The list of category IDs for the alternative stream; null if the calculation fails
     */
    public Integer[] getAltStreamsOfTimeSlot(int timeSlotId, double minBounceRate, double maxBounceRate) {
        validateTimeSlot(timeSlotId);

        synchronized (tableCalculationDAO) {

            System.out.println("Bouncerate calculation start");
            tableCalculationDAO.resetCalculationTable();

            Integer categoryId = scheduleTableDAO.getCategoryId(timeSlotId, 0);
            if(categoryId == null) return null;

            tableCalculationDAO.initializeData(categoryId, minBounceRate, maxBounceRate);
            tableCalculationDAO.excludeCategory(categoryId);

            System.out.println("Initialization done");

            Integer[] result = new Integer[ALT_STREAM_COUNT];
            Arrays.fill(result, null);

            for (int i = 0; i < ALT_STREAM_COUNT; i++) {
                Integer altCategoryId = tableCalculationDAO.getCurrentBestCategoryId(timeSlotId, minBounceRate, maxBounceRate);
                if(altCategoryId == null) return result;

                result[i] = altCategoryId;
                tableCalculationDAO.setSetTopBoxesCaptured(altCategoryId, minBounceRate, maxBounceRate);
                tableCalculationDAO.excludeCategory(altCategoryId);
                System.out.println("Loop #" + (i + 1) + " done");
            }
            return result;
        }
    }


    // ===== SETTERS =====

    public void setOne(int timeSlotId, int streamNumber, int categoryId) {
        try {
            validateTimeSlot(timeSlotId);
            validateStreamNumber(streamNumber);
            scheduleTableDAO.insertNode(new ScheduleTableDAO.ScheduleNode(timeSlotId, streamNumber, categoryId));
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public void setAlternatives(int timeSlotId, Integer[] categoryIdList) {
        validateTimeSlot(timeSlotId);
        for(int i = 0; i < categoryIdList.length; i++) {
            Integer category = categoryIdList[i];
            if(category == null) continue;

            setOne(timeSlotId, i + 1, category);
        }
    }


    // ===== REMOVERS =====

    public void removeOne(int timeSlotId, int streamNumber) {
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
