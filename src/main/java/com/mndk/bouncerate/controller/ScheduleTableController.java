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
            @RequestParam(value = "slotId")         int timeSlotId,
            @RequestParam(value = "categoryId")     int categoryId
    ) {
        scheduleTableService.setOne(timeSlotId, 0, categoryId);
    }


    @DeleteMapping("/default")
    public void deleteDefaultStreamSchedule(@RequestParam(value = "slotId") int timeSlotId) {
        scheduleTableService.removeOne(timeSlotId, 0);
    }


    @PostMapping("/calculate")
    public void calculateBounceRateOfTimeSlot(@RequestParam(value = "slotId") int timeSlotId) {
        scheduleTableService.calculateBounceRateOfTimeSlot(timeSlotId);
    }

}
