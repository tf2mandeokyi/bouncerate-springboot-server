package com.mndk.bouncerate.controller;

import com.mndk.bouncerate.db.AdvertisementProduct;
import com.mndk.bouncerate.db.AdvertisementProductDAO;
import com.mndk.bouncerate.db.BounceRateDAO;
import com.mndk.bouncerate.db.SetTopBoxesDAO;
import com.mndk.bouncerate.util.NullValidator;
import com.mndk.bouncerate.util.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bounceRates")
@SuppressWarnings("unused")
public class BounceRateController {


    @Autowired AdvertisementProductDAO productDAO;
    @Autowired BounceRateDAO bounceRateDAO;
    @Autowired SetTopBoxesDAO setTopBoxesDAO;


    @GetMapping("/{productId}/{setTopBoxId}")
    @ResponseBody
    public float getBounceRate(
            @PathVariable("productId")     int productId,
            @PathVariable("setTopBoxId")   int setTopBoxId
    ) {
        return NullValidator.check(bounceRateDAO.getBounceRate(productId, setTopBoxId), ResourceNotFoundException::new);
    }


    @PostMapping("/{productId}/{setTopBoxId}")
    public void setBounceRate(
            @PathVariable("productId")     int productId,
            @PathVariable("setTopBoxId")   int setTopBoxId,
            @RequestBody float requestBody
    ) {
        bounceRateDAO.setBounceRate(productId, setTopBoxId, requestBody);
    }


    @PostMapping("/randomize")
    public void setBounceRatesRandom() {
        List<AdvertisementProduct> products = productDAO.getAll();
        List<Integer> productIdList = products.stream().map(AdvertisementProduct::id).toList();
        List<Integer> setTopBoxIdList = setTopBoxesDAO.getAllIds();
        bounceRateDAO.insertRandomizedBounceRates(productIdList, setTopBoxIdList, 0, 70);
    }

}
