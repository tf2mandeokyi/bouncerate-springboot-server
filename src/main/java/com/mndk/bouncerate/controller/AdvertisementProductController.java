package com.mndk.bouncerate.controller;

import com.mndk.bouncerate.db.AdvertisementProduct;
import com.mndk.bouncerate.db.AdvertisementProductDAO;
import com.mndk.bouncerate.db.BounceRateDAO;
import com.mndk.bouncerate.db.SetTopBoxesDAO;
import com.mndk.bouncerate.util.NullValidator;
import com.mndk.bouncerate.util.StringRandomizer;
import com.mndk.bouncerate.util.ValueWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@SuppressWarnings({ "unused", "UnusedReturnValue" })
public class AdvertisementProductController {


    @Autowired AdvertisementProductDAO productDAO;
    @Autowired BounceRateDAO bounceRateDAO;
    @Autowired SetTopBoxesDAO setTopBoxesDAO;


    @GetMapping("")
    @ResponseBody
    public List<AdvertisementProduct> getMany(
            @RequestParam(value = "count", defaultValue = "-1") int count,
            @RequestParam(value = "page", defaultValue = "1") int pageNum
    ) {
        if(count == -1) {
            return productDAO.getAll();
        }
        else if(count >= 1 && pageNum >= 1) {
            return productDAO.getBulk(count, (pageNum - 1) * count);
        }
        else {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
    }


    record AddProductBody(String name, boolean availability) {}
    @PostMapping("")
    public void addOneOrMany(
            @RequestParam(value = "random", defaultValue = "false") boolean random,
            @RequestParam(value = "count", defaultValue = "1") int count,
            @RequestBody(required = false) AddProductBody requestBody
    ) {
        if(random) {
            for (int i = 0; i < count; i++) {
                productDAO.addOne(StringRandomizer.nextAZaz09String(5), true);
            }
        } else if(requestBody != null) {
            productDAO.addOne(requestBody.name, requestBody.availability);
        } else {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/{id}")
    @ResponseBody
    public AdvertisementProduct getOne(@PathVariable("id") int id) {
        return NullValidator.check(
                productDAO.getOne(id),
                () -> new HttpClientErrorException(HttpStatus.NOT_FOUND)
        );
    }


    record UpdateProductBody(String name, Boolean availability) {}
    @PostMapping("/{id}")
    public void updateOne(
            @PathVariable("id") int id,
            @RequestBody UpdateProductBody requestBody
    ) {
        if(requestBody.name != null) productDAO.updateName(id, requestBody.name);
        if(requestBody.availability != null) productDAO.updateAvailability(id, requestBody.availability);
    }


    @DeleteMapping("/{id}")
    public void deleteOne(@PathVariable("id") int id) {
        productDAO.deleteOne(id);
        bounceRateDAO.deleteBounceRatesOfProduct(id);
    }


    @GetMapping("/count")
    @ResponseBody
    public ValueWrapper<Integer> getCount() {
        return new ValueWrapper<>(productDAO.getCount());
    }


    @GetMapping("/getPriority")
    @ResponseBody
    public List<AdvertisementProduct> getPriority(
            @RequestParam(value = "count", defaultValue = "3") int count
    ) {
        return productDAO.getBulk_orderByScore(count, 0);
    }
}
