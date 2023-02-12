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
        scheduleTableService.setOne(timeSlotId, streamNumber, categoryId);
    }


    @DeleteMapping("/default")
    public void deleteStreamSchedule(
            @RequestParam("slotId")         int timeSlotId,
            @RequestParam("streamNumber")   int streamNumber
    ) {
        scheduleTableService.removeOne(timeSlotId, streamNumber);
    }


    record AlternativeStreamsBody(Integer[] altStreams) {}
    @PostMapping("/alternatives")
    @ResponseBody
    public AlternativeStreamsBody calculateAlternativeStreams(
            @RequestParam("slotId")     int timeSlotId,
            @RequestBody                MinMax<Double> bounceRateMinMax
    ) {
        Integer[] alternativeCategories = scheduleTableService.getAltStreamsOfTimeSlot(
                timeSlotId, bounceRateMinMax.min(), bounceRateMinMax.max()
        );
        scheduleTableService.setAlternatives(timeSlotId, alternativeCategories);
        return new AlternativeStreamsBody(alternativeCategories);
    }


    @GetMapping("/bounceRate")
    @ResponseBody
    public ScheduleTableService.TimeSlotBounceRate getTimeSlotBounceRate(
            @RequestParam("slotId")     int timeSlotId,
            @RequestBody                MinMax<Double> bounceRateMinMax
    ) {
        return scheduleTableService.getTimeSlotBounceRate(timeSlotId, bounceRateMinMax.min(), bounceRateMinMax.max());
    }

}
