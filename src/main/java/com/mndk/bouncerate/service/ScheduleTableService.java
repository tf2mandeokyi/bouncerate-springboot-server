package com.mndk.bouncerate.service;

import com.mndk.bouncerate.db.BounceRateDAO;
import com.mndk.bouncerate.db.ProductCategoryDAO;
import com.mndk.bouncerate.db.ScheduleTableDAO;
import com.mndk.bouncerate.db.ScheduleTableDAO.ScheduleTableBounceRateNode;
import com.mndk.bouncerate.db.ScheduleTableDAO.ScheduleTableBounceRateNodeValue;
import com.mndk.bouncerate.db.ScheduleTableDAO.ScheduleTableStreamNode;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.*;

@Service
@AllArgsConstructor
public class ScheduleTableService {


    private static final int TIME_SLOT_COUNT = 6;
    private static final int ALT_STREAM_COUNT = 3;


    BounceRateDAO bounceRateDAO;
    ProductCategoryDAO categoryDAO;
    ScheduleTableDAO scheduleTableDAO;


    // ===== FUNCTIONS =====

    interface ProductCategoryIdFunction {
        Integer get(int streamNumber,
                    Set<Integer> capturedSetTopBoxes,
                    Map<Integer, Float> lowestSetTopBoxBounceRateMap);
    }


    // ===== RECORDS =====

    public record AltStreamCalculationResult(
            Integer[] altStreams,
            ScheduleTableBounceRateNodeValue[] bounceRateArray
    ) {}
    
    public record ScheduleTable(
            Map<Integer, Integer[]> table
    ) {}
    
    public record BounceRateTable(
            Map<Integer, ScheduleTableBounceRateNodeValue[]> table
    ) {}


    // ===== GETTERS =====

    public ScheduleTable getEntireTable() {
        var scheduleNodes = scheduleTableDAO.getAll();

        var tableMap = new HashMap<Integer, Integer[]>();

        // TODO: Replace this tableMap setup to something else
        for(int i = 0; i < TIME_SLOT_COUNT; i++) {
            tableMap.put(i, new Integer[ALT_STREAM_COUNT + 1]);
        }

        for(var scheduleNode : scheduleNodes) {
            int timeSlotId = scheduleNode.getTimeSlotId(), streamNumber = scheduleNode.getStreamNumber();

            // TODO: Replace this timeSlotId check to something else
            if(timeSlotId < 0 || timeSlotId > TIME_SLOT_COUNT - 1) continue;
            if(streamNumber < 0 || streamNumber > ALT_STREAM_COUNT) continue;

            tableMap.get(timeSlotId)[streamNumber] = scheduleNode.getCategoryId();
        }

        return new ScheduleTable(tableMap);
    }


    public ScheduleTableBounceRateNodeValue[] calculateAndGetTimeSlotBounceRate(int timeSlotId,
                                                                                boolean updateIfOutdated,
                                                                                double maxBounceRate)
    {
        validateTimeSlotId(timeSlotId);
        var bounceRateNodes = scheduleTableDAO.getTimeSlotBounceRate(timeSlotId);

        int nonNullCount = 0;
        var updateNeededStreamNumberList = new ArrayList<Integer>();
        var bounceRateStreamArray = new ScheduleTableBounceRateNodeValue[ALT_STREAM_COUNT + 1];
        for(var bounceRateNode : bounceRateNodes) {
            int streamNumber = bounceRateNode.getStreamNumber();
            if(streamNumber >= ALT_STREAM_COUNT + 1) continue;

            bounceRateStreamArray[streamNumber] = bounceRateNode.toValueObject();
            nonNullCount++;

            if(bounceRateNode.isNeedsUpdate()) updateNeededStreamNumberList.add(streamNumber);
        }

        if(updateIfOutdated && (updateNeededStreamNumberList.size() != 0 || nonNullCount != ALT_STREAM_COUNT + 1)) {
            bounceRateStreamArray = this.calculateBounceRateArrayFromExistingStreams(timeSlotId, maxBounceRate);
            var newBounceRateNodes = ScheduleTableBounceRateNodeValue.toNodeArray(timeSlotId, bounceRateStreamArray);
            this.updateTimeSlotBounceRate(newBounceRateNodes);
        }
        return bounceRateStreamArray;
    }


    public BounceRateTable getBounceRateTable() {
        var tableMap = new HashMap<Integer, ScheduleTableBounceRateNodeValue[]>();
        for(int i = 0; i < TIME_SLOT_COUNT; i++) {
            var bounceRateArray = this.calculateAndGetTimeSlotBounceRate(i, false, 0);
            tableMap.put(i, bounceRateArray);
        }
        return new BounceRateTable(tableMap);
    }

    
    public AltStreamCalculationResult storeAndGetAltStreamCalculationResult(int timeSlotId,
                                                                            double maxBounceRate)
    {
        var calculationResult = calculateAltStreamsOfTimeSlot(timeSlotId, maxBounceRate);
        setAlternativeStreams(timeSlotId, calculationResult.altStreams());
        
        var calculationResultNodes = ScheduleTableBounceRateNodeValue.toNodeArray(timeSlotId, calculationResult.bounceRateArray);
        updateTimeSlotBounceRate(calculationResultNodes);
        
        return calculationResult;
    }
    

    // ===== CALCULATORS =====

    @Nullable
    public ScheduleTableBounceRateNodeValue[] calculateBounceRateArrayFromExistingStreams(int timeSlotId,
                                                                                          double maxBounceRate)
    {
        var calculationResult = this.calculateAltStreamsOfTimeSlot(
                timeSlotId,
                (streamNumber, _set, _map) -> scheduleTableDAO.getCategoryId(timeSlotId, streamNumber),
                maxBounceRate
        );
        return calculationResult != null ? calculationResult.bounceRateArray() : null;
    }


    @Nullable
    public AltStreamCalculationResult calculateAltStreamsOfTimeSlot(int timeSlotId,
                                                                    double maxBounceRate)
    {
        Integer defaultStreamCategoryId = scheduleTableDAO.getCategoryId(timeSlotId, 0);
        if(defaultStreamCategoryId == null) return null;

        var categoryList = categoryDAO.getAll();
        Set<Integer> excludedCategories = new HashSet<>(List.of(defaultStreamCategoryId));

        return this.calculateAltStreamsOfTimeSlot(
                timeSlotId, (streamNumber, capturedSetTopBoxes, lowestSetTopBoxBounceRateMap) -> {
                    double leastCategoryBounceRate = 100;
                    int bestCategoryId = -1;
                    for (var category : categoryList) {
                        if (excludedCategories.contains(category.id())) continue;

                        var bounceRateMap = bounceRateDAO.getBounceRateMapOfCategory(category.id());

                        double total = 0;
                        int count = 0;
                        for (var bounceRateEntry : bounceRateMap.entrySet()) {
                            int setTopBoxId = bounceRateEntry.getKey();
                            if(capturedSetTopBoxes.contains(setTopBoxId)) continue;

                            Float newBounceRate = bounceRateEntry.getValue();
                            Float oldBounceRate = lowestSetTopBoxBounceRateMap.get(setTopBoxId);

                            Float lowerBounceRate = getLowerBounceRate(oldBounceRate, newBounceRate);
                            if (lowerBounceRate != null) { total += newBounceRate; count++; }
                        }

                        double categoryBounceRate = total / (double) count;
                        if (categoryBounceRate < leastCategoryBounceRate) {
                            leastCategoryBounceRate = categoryBounceRate;
                            bestCategoryId = category.id();
                        }
                    }
                    excludedCategories.add(bestCategoryId);
                    return bestCategoryId;
                }, maxBounceRate
        );
    }


    /**
     * @param timeSlotId The ID of the Time slot to calculate the tables
     * @param maxBounceRate Highest accepted value of the set-top boxes' bounce rate
     * @return The list of category IDs for the alternative stream; null if the calculation fails
     */
    @Nullable
    private AltStreamCalculationResult calculateAltStreamsOfTimeSlot(int timeSlotId,
                                                                     ProductCategoryIdFunction categoryIdFunction,
                                                                     double maxBounceRate)
    {
        Integer defaultStreamCategoryId = scheduleTableDAO.getCategoryId(timeSlotId, 0);
        if(defaultStreamCategoryId == null) return null;

        var lowestSetTopBoxBounceRateMap = new HashMap<Integer, Float>();
        var capturedSetTopBoxes = new HashSet<Integer>();

        var altStreamArray = new Integer[ALT_STREAM_COUNT];
        var bounceRateArray = new ScheduleTableBounceRateNodeValue[ALT_STREAM_COUNT + 1];

        double bounceRateSum = 0, streamBounceRate;
        var bounceRateMap = bounceRateDAO.getBounceRateMapOfCategory(defaultStreamCategoryId);
        for(var bounceRateEntry : bounceRateMap.entrySet()) {
            int setTopBoxId = bounceRateEntry.getKey();
            Float bounceRate = bounceRateEntry.getValue();

            bounceRateSum += bounceRate;
            lowestSetTopBoxBounceRateMap.put(setTopBoxId, bounceRate);
            if(bounceRate <= maxBounceRate) capturedSetTopBoxes.add(setTopBoxId);
        }
        streamBounceRate = bounceRateSum / lowestSetTopBoxBounceRateMap.size();
        bounceRateArray[0] = new ScheduleTableBounceRateNodeValue(streamBounceRate, false);

        Arrays.fill(altStreamArray, null);
        for(int i = 0; i < ALT_STREAM_COUNT; i++) {
            int categoryId = categoryIdFunction.get(i + 1, capturedSetTopBoxes, lowestSetTopBoxBounceRateMap);
            altStreamArray[i] = categoryId;
            bounceRateMap = bounceRateDAO.getBounceRateMapOfCategory(categoryId);

            for(var bounceRateEntry : bounceRateMap.entrySet()) {
                int setTopBoxId = bounceRateEntry.getKey();
                Float newBounceRate = bounceRateEntry.getValue();
                Float oldBounceRate = lowestSetTopBoxBounceRateMap.get(setTopBoxId);

                if(isNewBounceRateLower(oldBounceRate, newBounceRate)) {
                    bounceRateSum += oldBounceRate == null ? newBounceRate : (newBounceRate - oldBounceRate);
                    lowestSetTopBoxBounceRateMap.put(setTopBoxId, newBounceRate);
                }
                if(newBounceRate <= maxBounceRate) capturedSetTopBoxes.add(setTopBoxId);
            }
            streamBounceRate = bounceRateSum / lowestSetTopBoxBounceRateMap.size();
            bounceRateArray[i + 1] = new ScheduleTableBounceRateNodeValue(streamBounceRate, false);
        }

        return new AltStreamCalculationResult(altStreamArray, bounceRateArray);
    }


    // ===== SETTERS =====

    public void setScheduleStream(int timeSlotId, int streamNumber, Integer categoryId) {
        validateTimeSlotId(timeSlotId);
        validateStreamNumber(streamNumber);
        scheduleTableDAO.insertNode(new ScheduleTableStreamNode(timeSlotId, streamNumber, categoryId));
        scheduleTableDAO.markTimeSlotBounceRateOutdated(timeSlotId);
    }


    public void setAlternativeStreams(int timeSlotId, Integer[] altStreamArray) {
        validateTimeSlotId(timeSlotId);
        for(int i = 0; i < altStreamArray.length; i++) {
            Integer category = altStreamArray[i];
            if(category == null) continue;

            setScheduleStream(timeSlotId, i + 1, category);
        }
    }


    public void updateTimeSlotBounceRate(ScheduleTableBounceRateNode... nodes) {
        validateNodes(nodes);
        scheduleTableDAO.updateTimeSlotBounceRate(nodes);
    }


    // ===== REMOVERS =====

    public void removeScheduleStream(int timeSlotId, int streamNumber) {
        validateTimeSlotId(timeSlotId);
        validateStreamNumber(streamNumber);
        scheduleTableDAO.deleteNode(timeSlotId, streamNumber);
    }


    // ===== MISC =====

    private void validateTimeSlotId(int timeSlotId) {
        // TODO: Replace this validation into something else
        if(timeSlotId < 0 || timeSlotId > TIME_SLOT_COUNT - 1) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Invalid time slot ID!");
        }
    }


    private void validateStreamNumber(int streamNumber) {
        if(streamNumber < 0 || streamNumber > ALT_STREAM_COUNT) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Invalid stream number!");
        }
    }


    private void validateNodes(ScheduleTableBounceRateNode... nodes) {
        for(var node : nodes) {
            validateTimeSlotId(node.getTimeSlotId());
            validateStreamNumber(node.getStreamNumber());
        }
    }


    @Nullable
    private Float getLowerBounceRate(@Nullable Float oldBounceRate, @Nullable Float newBounceRate) {
        return isNewBounceRateLower(oldBounceRate, newBounceRate) ? newBounceRate : oldBounceRate;
    }


    @Nullable
    private boolean isNewBounceRateLower(@Nullable Float oldBounceRate, @Nullable Float newBounceRate) {
        return newBounceRate != null && (oldBounceRate == null || oldBounceRate > newBounceRate);
    }

}
