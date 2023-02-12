package com.mndk.bouncerate.controller;

import com.mndk.bouncerate.service.ScheduleTableService;
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


    @PostMapping("/default")
    public void setDefaultStreamSchedule(
            @RequestParam("slotId")         int timeSlotId,
            @RequestParam("categoryId")     int categoryId
    ) {
        scheduleTableService.setOne(timeSlotId, 0, categoryId);
    }


    @DeleteMapping("/default")
    public void deleteDefaultStreamSchedule(@RequestParam("slotId") int timeSlotId) {
        scheduleTableService.removeOne(timeSlotId, 0);
    }


    @PostMapping("/calculate")
    public void calculateBounceRateOfTimeSlot(@RequestParam("slotId") int timeSlotId) {
        // TODO: Include bounce rate range as a RequestBody
        Integer[] alternativeCategories = scheduleTableService.getAltStreamsOfTimeSlot(timeSlotId, 0, 30);
        scheduleTableService.setAlternatives(timeSlotId, alternativeCategories);
    }

}
