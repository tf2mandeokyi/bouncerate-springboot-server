package com.mndk.bouncerate.controller;

import com.mndk.bouncerate.service.BounceRateService;
import com.mndk.bouncerate.util.MinMax;
import com.mndk.bouncerate.util.ValueWrapper;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/bounceRates")
@SuppressWarnings("unused")
public class BounceRateController {


    BounceRateService bounceRateService;


    @PostMapping("/setTopBox/{setTopBoxId}/randomize")
    public void randomizeBounceRatesOfSetTopBox(
            @PathVariable("setTopBoxId")    int setTopBoxId,
            @RequestBody                    MinMax<Double> bounceRateRange
    ) {
        bounceRateService.randomizeBounceRatesOfSetTopBox(setTopBoxId, bounceRateRange.min(), bounceRateRange.max());
    }


    @GetMapping({ "/category/{categoryId}/{setTopBoxId}", "/setTopBox/{setTopBoxId}/{categoryId}" })
    @ResponseBody
    public ValueWrapper<Double> getBounceRate(
            @PathVariable("categoryId")    int categoryId,
            @PathVariable("setTopBoxId")   int setTopBoxId
    ) {
        return new ValueWrapper<>(bounceRateService.getBounceRate(categoryId, setTopBoxId));
    }


    @PostMapping({ "/category/{categoryId}/{setTopBoxId}", "/setTopBox/{setTopBoxId}/{categoryId}" })
    public void setBounceRate(
            @PathVariable("categoryId")    int categoryId,
            @PathVariable("setTopBoxId")   int setTopBoxId,
            @RequestBody float requestBody
    ) {
        bounceRateService.setBounceRate(categoryId, setTopBoxId, requestBody);
    }


    @PostMapping("/randomize")
    public void setBounceRatesRandom(
            @RequestBody MinMax<Integer> bounceRateRange
    ) {
        bounceRateService.randomizeAll(bounceRateRange.min(), bounceRateRange.max());
    }
}
