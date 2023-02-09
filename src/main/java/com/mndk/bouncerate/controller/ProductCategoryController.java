package com.mndk.bouncerate.controller;

import com.mndk.bouncerate.db.BounceRateDAO;
import com.mndk.bouncerate.db.ProductCategoryDAO;
import com.mndk.bouncerate.util.Validator;
import com.mndk.bouncerate.util.ValueWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@SuppressWarnings({ "unused", "UnusedReturnValue" })
public class ProductCategoryController {


    @Autowired ProductCategoryDAO categoryDAO;
    @Autowired BounceRateDAO bounceRateDAO;


    @GetMapping("")
    @ResponseBody
    public List<ProductCategoryDAO.ProductCategory> getMany(
            @RequestParam(value = "count", defaultValue = "-1") int count,
            @RequestParam(value = "page", defaultValue = "1")   int pageNum
    ) {
        if(count == -1) {
            return categoryDAO.getAll();
        }
        else if(count >= 1 && pageNum >= 1) {
            return categoryDAO.getBulk(count, (pageNum - 1) * count);
        }
        else {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
    }


    record AddProductCategoryBody(String name) {}
    @PostMapping("")
    public void addOneOrMany(@RequestBody(required = false) AddProductCategoryBody requestBody) {
        if(requestBody != null) {
            categoryDAO.addOne(requestBody.name);
        } else {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/{id}")
    @ResponseBody
    public ProductCategoryDAO.ProductCategory getOne(@PathVariable("id") int id) {
        return Validator.checkNull(
                categoryDAO.getOne(id),
                () -> new HttpClientErrorException(HttpStatus.NOT_FOUND)
        );
    }


    record UpdateProductCategoryBody(String name) {}
    @PostMapping("/{id}")
    public void updateOne(
            @PathVariable("id") int id,
            @RequestBody UpdateProductCategoryBody requestBody
    ) {
        if(requestBody.name != null) categoryDAO.updateName(id, requestBody.name);
    }


    @DeleteMapping("/{id}")
    public void deleteOne(@PathVariable("id") int id) {
        categoryDAO.deleteOne(id);
        bounceRateDAO.deleteBounceRatesOfCategory(id);
    }


    @GetMapping("/{id}/score")
    @ResponseBody
    public ValueWrapper<Double> getBounceRateScore(
            @PathVariable("id")         int id,
            @RequestParam("br_min")     double minBounceRate,
            @RequestParam("br_max")     double maxBounceRate
    ) {
        return new ValueWrapper<>(
                bounceRateDAO.getBounceRateCountOfCategory(
                        id, minBounceRate, maxBounceRate
                ).getScore()
        );
    }


    @GetMapping("/count")
    @ResponseBody
    public ValueWrapper<Integer> getCount() {
        return new ValueWrapper<>(categoryDAO.getCount());
    }
}
