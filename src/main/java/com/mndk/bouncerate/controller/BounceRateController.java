package com.mndk.bouncerate.controller;

import com.mndk.bouncerate.db.BounceRateDAO;
import com.mndk.bouncerate.db.ProductCategoryDAO;
import com.mndk.bouncerate.db.SetTopBoxesDAO;
import com.mndk.bouncerate.util.MinMax;
import com.mndk.bouncerate.util.ValueWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bounceRates")
@SuppressWarnings("unused")
public class BounceRateController {


    @Autowired ProductCategoryDAO categoryDAO;
    @Autowired BounceRateDAO bounceRateDAO;
    @Autowired SetTopBoxesDAO setTopBoxesDAO;


    @PostMapping("/setTopBox/{setTopBoxId}/randomize")
    public void randomizeBounceRatesOfSetTopBox(
            @PathVariable("setTopBoxId")    int setTopBoxId,
            @RequestBody                    MinMax<Double> bounceRateMinMax
    ) {
        double randomStart = bounceRateMinMax.min();
        double randomSize = bounceRateMinMax.max() - bounceRateMinMax.min();
        bounceRateDAO.randomizeBounceRatesOfSetTopBox(setTopBoxId, randomStart, randomSize);
    }


    @GetMapping({ "/category/{categoryId}/{setTopBoxId}", "/setTopBox/{setTopBoxId}/{categoryId}" })
    @ResponseBody
    public ValueWrapper<Float> getBounceRate(
            @PathVariable("categoryId")    int categoryId,
            @PathVariable("setTopBoxId")   int setTopBoxId
    ) {
        return new ValueWrapper<>(bounceRateDAO.getBounceRate(categoryId, setTopBoxId));
    }


    @PostMapping({ "/category/{categoryId}/{setTopBoxId}", "/setTopBox/{setTopBoxId}/{categoryId}" })
    public void setBounceRate(
            @PathVariable("categoryId")    int categoryId,
            @PathVariable("setTopBoxId")   int setTopBoxId,
            @RequestBody float requestBody
    ) {
        bounceRateDAO.setBounceRate(categoryId, setTopBoxId, requestBody);
    }


    @PostMapping("/randomize")
    public void setBounceRatesRandom(
            @RequestBody MinMax<Integer> bounceRateMinMax
    ) {
        double randomStart = bounceRateMinMax.min();
        double randomSize = bounceRateMinMax.max() - bounceRateMinMax.min();

        List<ProductCategoryDAO.ProductCategory> categories = categoryDAO.getAll();
        List<Integer> categoryIdList = categories.stream().map(ProductCategoryDAO.ProductCategory::id).toList();
        for(int categoryId : categoryIdList) {
            bounceRateDAO.randomizeBounceRatesOfCategory(categoryId, randomStart, randomSize);
        }
    }
}
