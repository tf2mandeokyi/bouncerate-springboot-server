package com.mndk.bouncerate.controller;

import com.mndk.bouncerate.db.ProductCategoryDAO.*;
import com.mndk.bouncerate.service.ProductCategoryService;
import com.mndk.bouncerate.util.Validator;
import com.mndk.bouncerate.util.ValueWrapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/categories")
@SuppressWarnings({ "unused", "UnusedReturnValue" })
public class ProductCategoryController {


    ProductCategoryService productCategoryService;


    @GetMapping("")
    @ResponseBody
    public List<ProductCategory> getMany(
            @RequestParam(value = "count", defaultValue = "-1") int count,
            @RequestParam(value = "page", defaultValue = "1")   int pageNum
    ) {
        return Validator.checkNull(
                productCategoryService.getPage(count, pageNum),
                () -> new HttpClientErrorException(HttpStatus.BAD_REQUEST)
        );
    }


    record AddProductCategoryBody(String name) {}
    @PostMapping("")
    public void addOneOrMany(@RequestBody AddProductCategoryBody requestBody) {
        String categoryName = Validator.checkNull(
                requestBody.name,
                () -> new HttpClientErrorException(HttpStatus.BAD_REQUEST)
        );
        productCategoryService.addOne(categoryName);
    }


    @GetMapping("/{id}")
    @ResponseBody
    public ProductCategory getOne(@PathVariable("id") int id) {
        return productCategoryService.getOne(id);
    }


    record UpdateProductCategoryBody(String name) {}
    @PostMapping("/{id}")
    public void updateOne(
            @PathVariable("id") int id,
            @RequestBody UpdateProductCategoryBody requestBody
    ) {
        productCategoryService.updateOne(id, requestBody.name);
    }


    @DeleteMapping("/{id}")
    public void deleteOne(@PathVariable("id") int id) {
        productCategoryService.deleteOne(id);
    }


    @GetMapping("/count")
    @ResponseBody
    public ValueWrapper<Integer> getCount() {
        return new ValueWrapper<>(productCategoryService.getCount());
    }
}
