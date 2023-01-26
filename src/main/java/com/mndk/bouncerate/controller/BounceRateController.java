package com.mndk.bouncerate.controller;

import com.mndk.bouncerate.db.AdvertisementProduct;
import com.mndk.bouncerate.db.AdvertisementProductDAO;
import com.mndk.bouncerate.db.BounceRateDAO;
import com.mndk.bouncerate.db.SetTopBoxesDAO;
import com.mndk.bouncerate.util.MinMax;
import com.mndk.bouncerate.util.NullValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.*;

@RestController
@RequestMapping("/api/v1/bounceRates")
@SuppressWarnings("unused")
public class BounceRateController {


    @Autowired AdvertisementProductDAO productDAO;
    @Autowired BounceRateDAO bounceRateDAO;
    @Autowired SetTopBoxesDAO setTopBoxesDAO;


    @GetMapping("/product/{productId}" )
    @ResponseBody
    public Map<String, Float> getBounceRatesOfProduct(
            @PathVariable("productId")     int productId
    ) {
        var bounceRateMap = bounceRateDAO.getBounceRatesOfProduct(productId);
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
        List<AdvertisementProduct> products = productDAO.getAll();
        List<Integer> productIdList = products.stream().map(AdvertisementProduct::id).toList();

        insertRandomizedBounceRates(
                productIdList, Collections.singletonList(setTopBoxId),
                bounceRateMinMax.min(), bounceRateMinMax.max()
        );
    }


    @GetMapping({ "/product/{productId}/{setTopBoxId}", "/setTopBox/{setTopBoxId}/{productId}" })
    @ResponseBody
    public float getBounceRate(
            @PathVariable("productId")     int productId,
            @PathVariable("setTopBoxId")   int setTopBoxId
    ) {
        return NullValidator.check(
                bounceRateDAO.getBounceRate(productId, setTopBoxId),
                () -> new HttpClientErrorException(HttpStatus.NOT_FOUND)
        );
    }


    @PostMapping({ "/product/{productId}/{setTopBoxId}", "/setTopBox/{setTopBoxId}/{productId}" })
    public void setBounceRate(
            @PathVariable("productId")     int productId,
            @PathVariable("setTopBoxId")   int setTopBoxId,
            @RequestBody float requestBody
    ) {
        bounceRateDAO.setBounceRate(productId, setTopBoxId, requestBody);
    }


    @PostMapping("/randomize")
    public void setBounceRatesRandom(
            @RequestBody MinMax<Integer> bounceRateMinMax
    ) {
        List<AdvertisementProduct> products = productDAO.getAll();
        List<Integer> productIdList = products.stream().map(AdvertisementProduct::id).toList();
        List<Integer> setTopBoxIdList = setTopBoxesDAO.getAllIds();
        insertRandomizedBounceRates(
                productIdList, setTopBoxIdList,
                bounceRateMinMax.min(), bounceRateMinMax.max()
        );
    }


    private void insertRandomizedBounceRates(
            List<Integer> productIdList, List<Integer> setTopBoxIdList, float min, float max
    ) {
        Random random = new Random();
        for(int productId : productIdList) for(int setTopBoxId : setTopBoxIdList) {
            this.setBounceRate(productId, setTopBoxId, random.nextFloat(min, max));
        }
    }

}
