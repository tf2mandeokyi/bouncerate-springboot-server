package com.mndk.bouncerate.controller;

import com.mndk.bouncerate.db.BounceRateDAO;
import com.mndk.bouncerate.db.ProductCategory;
import com.mndk.bouncerate.db.ProductCategoryDAO;
import com.mndk.bouncerate.util.NullValidator;
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
    public List<ProductCategory> getMany(
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
    public ProductCategory getOne(@PathVariable("id") int id) {
        return NullValidator.check(
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


    @GetMapping("/count")
    @ResponseBody
    public ValueWrapper<Integer> getCount() {
        return new ValueWrapper<>(categoryDAO.getCount());
    }


    @GetMapping("/getPriority")
    @ResponseBody
    public List<ProductCategory> getPriority(
            @RequestParam(value = "count", defaultValue = "3") int count
    ) {
        return categoryDAO.getBulk_orderByScore(count, 0);
    }
}
