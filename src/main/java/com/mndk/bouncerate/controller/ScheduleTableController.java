package com.mndk.bouncerate.controller;

import com.mndk.bouncerate.service.ScheduleTableService;
import com.mndk.bouncerate.util.MinMax;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/scheduleTable")
@SuppressWarnings("unused")
public class ScheduleTableController {


    ScheduleTableService scheduleTableService;


    record ScheduleTable(Integer[][] table) {}
    @GetMapping("")
    @ResponseBody
    public ScheduleTable getAll() {
        return new ScheduleTable(scheduleTableService.getEntireTable());
    }


    @PostMapping("")
    public void setStreamSchedule(
            @RequestParam("slotId")         int timeSlotId,
            @RequestParam("streamNumber")   int streamNumber,
            @RequestParam("categoryId")     int categoryId
    ) {
        scheduleTableService.setScheduleStream(timeSlotId, streamNumber, categoryId);
    }


    @DeleteMapping("/default")
    public void deleteStreamSchedule(
            @RequestParam("slotId")         int timeSlotId,
            @RequestParam("streamNumber")   int streamNumber
    ) {
        scheduleTableService.removeScheduleStream(timeSlotId, streamNumber);
    }


    @PostMapping("/alternatives")
    @ResponseBody
    public ScheduleTableService.AltStreamCalculationResult calculateAlternativeStreams(
            @RequestParam("slotId")     int timeSlotId,
            @RequestBody                MinMax<Double> bounceRateRange
    ) {
        var calculationResult = scheduleTableService.calculateAltStreamsOfTimeSlot(
                timeSlotId, bounceRateRange.min(), bounceRateRange.max()
        );
        scheduleTableService.setAlternativeStreams(timeSlotId, calculationResult.altStreams());
        scheduleTableService.updateTimeSlotBounceRate(timeSlotId, calculationResult.bounceRate());
        return calculationResult;
    }


    @GetMapping("/bounceRate")
    @ResponseBody
    public Object getAllTimeSlotBounceRates() {
        return scheduleTableService.getAllTimeSlotBounceRates();
    }


    @PostMapping("/bounceRate")
    @ResponseBody
    public Object calculateTimeSlotBounceRate(
            @RequestParam(value = "slotId")     int timeSlotId,
            @RequestBody                        MinMax<Double> bounceRateRange
    ) {
        return scheduleTableService.getTimeSlotBounceRate(timeSlotId, true, bounceRateRange);
    }

}
