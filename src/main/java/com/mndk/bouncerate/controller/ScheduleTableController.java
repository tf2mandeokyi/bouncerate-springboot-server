package com.mndk.bouncerate.controller;

import com.mndk.bouncerate.db.ScheduleTableDAO.*;
import com.mndk.bouncerate.service.ScheduleTableService;
import com.mndk.bouncerate.service.ScheduleTableService.*;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/scheduleTable")
@SuppressWarnings("unused")
public class ScheduleTableController {


    ScheduleTableService scheduleTableService;


    @GetMapping("")
    @ResponseBody
    public ScheduleTable getAll() {
        return scheduleTableService.getEntireTable();
    }


    @PostMapping("")
    public void setStreamSchedule(
            @RequestParam("slotId")         int timeSlotId,
            @RequestParam("streamNumber")   int streamNumber,
            @RequestParam("categoryId")     int categoryId
    ) {
        scheduleTableService.setScheduleStream(timeSlotId, streamNumber, categoryId);
    }


    @DeleteMapping("")
    public void deleteStreamSchedule(
            @RequestParam("slotId")         int timeSlotId,
            @RequestParam("streamNumber")   int streamNumber
    ) {
        scheduleTableService.removeScheduleStream(timeSlotId, streamNumber);
    }


    @PostMapping("/alternatives")
    @ResponseBody
    public AltStreamCalculationResult calculateAndGetAlternativeStreams(
            @RequestParam("slotId")         int timeSlotId,
            @RequestParam("maxBounceRate")  double maxBounceRate
    ) {
        return scheduleTableService.storeAndGetAltStreamCalculationResult(timeSlotId, maxBounceRate);
    }


    @GetMapping("/bounceRate")
    @ResponseBody
    public BounceRateTable getBounceRateTable() {
        return scheduleTableService.getBounceRateTable();
    }


    @PostMapping("/bounceRate")
    @ResponseBody
    public ScheduleTableBounceRateNodeValue[] calculateAndGetTimeSlotBounceRate(
            @RequestParam("slotId")         int timeSlotId,
            @RequestParam("maxBounceRate")  double maxBounceRate
    ) {
        return scheduleTableService.calculateAndGetTimeSlotBounceRate(timeSlotId, true, maxBounceRate);
    }

}
