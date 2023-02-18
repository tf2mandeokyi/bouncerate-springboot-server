package com.mndk.bouncerate.controller;

import com.mndk.bouncerate.service.BounceRateService;
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
    public void randomizeBounceRatesOfSetTopBox(@PathVariable("setTopBoxId") int setTopBoxId) {
        bounceRateService.randomizeBounceRatesOfSetTopBox(setTopBoxId);
    }


    @GetMapping({ "/category/{categoryId}/{setTopBoxId}", "/setTopBox/{setTopBoxId}/{categoryId}" })
    @ResponseBody
    public ValueWrapper<Float> getBounceRate(
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
    public void setBounceRatesRandom() {
        bounceRateService.randomizeAll();
    }
}
