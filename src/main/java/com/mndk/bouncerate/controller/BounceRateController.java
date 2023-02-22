package com.mndk.bouncerate.controller;

import com.mndk.bouncerate.service.BounceRateService;
import com.mndk.bouncerate.util.ValueWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Api(tags = "Bounce rate 컨트롤러")
@RequestMapping("/api/v1/bounceRates")
@SuppressWarnings("unused")
public class BounceRateController {


    BounceRateService bounceRateService;


    @ApiOperation("셋톱박스에 해당하는 카테고리 별 Bounce rate 무작위화")
    @ApiResponse(code = 200, message = "ok")
    @PostMapping("/setTopBox/{setTopBoxId}/randomize")
    public void randomizeBounceRatesOfSetTopBox(@PathVariable("setTopBoxId") int setTopBoxId) {
        bounceRateService.randomizeBounceRatesOfSetTopBox(setTopBoxId);
    }


    @PostMapping("/category/{categoryId}/randomize")
    public void randomizeBounceRatesOfCategory(@PathVariable("categoryId") int categoryId) {
        bounceRateService.randomizeBounceRatesOfCategory(categoryId);
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
