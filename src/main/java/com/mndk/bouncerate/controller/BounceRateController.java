package com.mndk.bouncerate.controller;

import com.mndk.bouncerate.db.ProductCategory;
import com.mndk.bouncerate.db.ProductCategoryDAO;
import com.mndk.bouncerate.db.BounceRateDAO;
import com.mndk.bouncerate.db.SetTopBoxesDAO;
import com.mndk.bouncerate.util.MinMax;
import com.mndk.bouncerate.util.ValueWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/bounceRates")
@SuppressWarnings("unused")
public class BounceRateController {


    @Autowired ProductCategoryDAO categoryDAO;
    @Autowired BounceRateDAO bounceRateDAO;
    @Autowired SetTopBoxesDAO setTopBoxesDAO;


    @GetMapping("/category/{categoryId}")
    @ResponseBody
    public Map<String, Float> getBounceRatesOfCategory(@PathVariable("categoryId") int categoryId) {
        var bounceRateMap = bounceRateDAO.getBounceRatesOfCategory(categoryId);
        Map<String, Float> result = new HashMap<>();

        for(var bounceRateEntry : bounceRateMap.entrySet()) {
            result.put(bounceRateEntry.getKey() + "", bounceRateEntry.getValue());
        }
        return result;
    }


    @GetMapping("/setTopBox/{setTopBoxId}")
    @ResponseBody
    public Map<String, Float> getBounceRatesOfSetTopBox(
            @PathVariable("setTopBoxId")   int setTopBoxId
    ) {
        var bounceRateEntryList = bounceRateDAO.getBounceRatesOfSetTopBox(setTopBoxId);
        Map<String, Float> result = new HashMap<>();

        for(var bounceRateEntry : bounceRateEntryList.entrySet()) {
            result.put(bounceRateEntry.getKey() + "", bounceRateEntry.getValue());
        }
        return result;
    }


    @PostMapping("/setTopBox/{setTopBoxId}/randomize")
    public void randomizeBounceRatesOfSetTopBox(
            @PathVariable("setTopBoxId")   int setTopBoxId,
            @RequestBody MinMax<Integer> bounceRateMinMax
    ) {
        List<ProductCategory> categories = categoryDAO.getAll();
        List<Integer> categoryIdList = categories.stream().map(ProductCategory::id).toList();
        
        insertRandomizedBounceRates(
                categoryIdList, Collections.singletonList(setTopBoxId),
                bounceRateMinMax.min(), bounceRateMinMax.max()
        );
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
        List<ProductCategory> categories = categoryDAO.getAll();
        List<Integer> categoryIdList = categories.stream().map(ProductCategory::id).toList();
        List<Integer> setTopBoxIdList = setTopBoxesDAO.getAllIds();
        insertRandomizedBounceRates(
                categoryIdList, setTopBoxIdList,
                bounceRateMinMax.min(), bounceRateMinMax.max()
        );
    }


    private void insertRandomizedBounceRates(
            List<Integer> categoryIdList, List<Integer> setTopBoxIdList, float min, float max
    ) {
        Random random = new Random();
        for(int categoryId : categoryIdList) for(int setTopBoxId : setTopBoxIdList) {
            this.setBounceRate(categoryId, setTopBoxId, random.nextFloat(min, max));
        }
    }

}
