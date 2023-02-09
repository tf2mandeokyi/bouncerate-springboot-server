package com.mndk.bouncerate.controller;

import com.mndk.bouncerate.db.ScheduleTableDAO;
import com.mndk.bouncerate.db.TemporaryBounceRateCalculationDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/scheduleTable")
@SuppressWarnings("unused")
public class ScheduleTableController {


    @Autowired ScheduleTableDAO scheduleTableDAO;
    @Autowired TemporaryBounceRateCalculationDAO temporaryBounceRateCalculationDAO;


    record ScheduleTable(Integer[][] table) {}
    @GetMapping("")
    @ResponseBody
    public ScheduleTable getAll() {
        List<ScheduleTableDAO.ScheduleNode> nodes = scheduleTableDAO.getAll();

        int timeSlotCount = ScheduleTableDAO.TIME_SLOT_COUNT;
        int altStreamCount = ScheduleTableDAO.ALT_STREAM_COUNT + 1;

        Integer[][] table = new Integer[6][];
        for(int i = 0; i < timeSlotCount; i++) {
            table[i] = new Integer[altStreamCount];
            Arrays.fill(table[i], null);
        }
        for(ScheduleTableDAO.ScheduleNode node : nodes) {
            int timeSlotId = node.getTimeSlotId(), streamNumber = node.getStreamNumber();

            if(timeSlotId < 0 || timeSlotId > timeSlotCount - 1) continue;
            if(streamNumber < 0 || streamNumber > altStreamCount) continue;

            table[timeSlotId][streamNumber] = node.getCategoryId();
        }

        return new ScheduleTable(table);
    }


    @PostMapping("/default")
    public void setDefaultStreamSchedule(
            @RequestParam(value = "slotId")         int slotId,
            @RequestParam(value = "categoryId")     int categoryId
    ) {
        try {
            validateTimeSlot(slotId);
            scheduleTableDAO.insertNode(new ScheduleTableDAO.ScheduleNode(slotId, 0, categoryId));
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
    }


    @DeleteMapping("/default")
    public void deleteDefaultStreamSchedule(@RequestParam(value = "slotId") int slotId) {
        validateTimeSlot(slotId);
        scheduleTableDAO.deleteNode(slotId, 0);
    }


    @PostMapping("/calculate")
    public void calculateBounceRate(@RequestParam(value = "slotId") int slotId) {
        synchronized (temporaryBounceRateCalculationDAO) {
            System.out.println("Bouncerate calculation start");

            temporaryBounceRateCalculationDAO.initialize(slotId, 0, 30);
            System.out.println("Initialization done");

            for (int i = 0; i < ScheduleTableDAO.ALT_STREAM_COUNT; i++) {
                temporaryBounceRateCalculationDAO.loop(slotId, i + 1, 0, 30);
                System.out.println("Loop #" + (i + 1) + " done");
            }
        }
    }


    private void validateTimeSlot(int timeSlotId) {
        if(timeSlotId < 0 || timeSlotId > ScheduleTableDAO.TIME_SLOT_COUNT - 1) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
    }

}
