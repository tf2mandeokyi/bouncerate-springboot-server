package com.mndk.bouncerate.controller;

import com.mndk.bouncerate.db.BounceRateDAO;
import com.mndk.bouncerate.db.ProductCategoryDAO;
import com.mndk.bouncerate.db.SetTopBoxesDAO;
import com.mndk.bouncerate.util.NullValidator;
import com.mndk.bouncerate.util.ValueWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/setTopBoxes")
@SuppressWarnings("unused")
public class SetTopBoxController {


    @Autowired ProductCategoryDAO categoryDAO;
    @Autowired BounceRateDAO bounceRateDAO;
    @Autowired SetTopBoxesDAO setTopBoxesDAO;


    @GetMapping("")
    @ResponseBody
    public List<SetTopBoxesDAO.SetTopBox> getMany(
            @RequestParam(value = "count", defaultValue = "-1") int count,
            @RequestParam(value = "page",  defaultValue = "1")  int pageNum
    ) {
        if(count == -1) {
            return setTopBoxesDAO.getAll();
        }
        else if(count >= 1 && pageNum >= 1) {
            return setTopBoxesDAO.getBulk(count, (pageNum - 1) * count);
        }
        else {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
    }


    record AddSetTopBoxBody(String location) {}
    @PostMapping("")
    public void addOneOrMany(
            @RequestParam(value = "random", defaultValue = "false") boolean random,
            @RequestParam(value = "count",  defaultValue = "1")     int count,
            @RequestBody (required = false)                         AddSetTopBoxBody requestBody
    ) {
        if(random) {
            for (int i = 0; i < count; i++) {
                setTopBoxesDAO.addSetTopBox(null);
            }
        } else if(requestBody != null) {
            setTopBoxesDAO.addSetTopBox(requestBody.location);
        } else {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/{id}")
    @ResponseBody
    public SetTopBoxesDAO.SetTopBox getOne(@PathVariable("id") int id) {
        return NullValidator.check(
                setTopBoxesDAO.getSetTopBox(id),
                () -> new HttpClientErrorException(HttpStatus.NOT_FOUND)
        );
    }


    @DeleteMapping("/{id}")
    public void deleteOne(@PathVariable("id") int setTopBoxId) {
        setTopBoxesDAO.deleteOne(setTopBoxId);
        bounceRateDAO.deleteBounceRatesOfSetTopBox(setTopBoxId);
    }


    @GetMapping("/count")
    @ResponseBody
    public ValueWrapper<Integer> getCount() {
        return new ValueWrapper<>(setTopBoxesDAO.getCount());
    }

}
