package com.mndk.bouncerate.controller;

import com.mndk.bouncerate.db.AdvertisementProduct;
import com.mndk.bouncerate.db.AdvertisementProductDAO;
import com.mndk.bouncerate.db.BounceRateDAO;
import com.mndk.bouncerate.db.SetTopBoxesDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bounceRates")
@SuppressWarnings("unused")
public class BounceRateController {


    @Autowired AdvertisementProductDAO productDAO;
    @Autowired BounceRateDAO bounceRateDAO;
    @Autowired SetTopBoxesDAO setTopBoxesDAO;


    @GetMapping("/{product_id}/{settopbox_id}")
    @ResponseBody
    public float getBounceRate(
            @PathVariable("product_id")     int productId,
            @PathVariable("settopbox_id")   int setTopBoxId
    ) {
        return bounceRateDAO.getBounceRate(productId, setTopBoxId);
    }


    @PostMapping("/{product_id}/{settopbox_id}")
    public ResponseEntity<?> setBounceRate(
            @PathVariable("product_id")     int productId,
            @PathVariable("settopbox_id")   int setTopBoxId,
            @RequestBody float requestBody
    ) {
        bounceRateDAO.setBounceRate(productId, setTopBoxId, requestBody);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PostMapping("/setRandom")
    public void setBounceRatesRandom() {
        List<AdvertisementProduct> products = productDAO.getAll();
        List<Integer> productIdList = products.stream().map(AdvertisementProduct::id).toList();
        List<Integer> setTopBoxIdList = setTopBoxesDAO.getAllIds();
        bounceRateDAO.insertRandomizedBounceRates(productIdList, setTopBoxIdList, 0, 70);
    }

}