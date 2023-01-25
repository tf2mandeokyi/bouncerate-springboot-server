package com.mndk.bouncerate.controller;

import com.mndk.bouncerate.db.SetTopBox;
import com.mndk.bouncerate.db.SetTopBoxesDAO;
import com.mndk.bouncerate.util.NullValidator;
import com.mndk.bouncerate.util.StringRandomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/setTopBoxes")
@SuppressWarnings("unused")
public class SetTopBoxController {


    @Autowired SetTopBoxesDAO setTopBoxesDAO;


    @GetMapping("")
    @ResponseBody
    public List<SetTopBox> getMany(
            @RequestParam(value = "count", defaultValue = "-1") int count,
            @RequestParam(value = "page", defaultValue = "1") int pageNum
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


    record AddSetTopBoxBody(String name) {}
    @PostMapping("")
    public void addOneOrMany(
            @RequestParam(value = "random", defaultValue = "false") boolean random,
            @RequestParam(value = "count", defaultValue = "1") int count,
            @RequestBody(required = false) AddSetTopBoxBody requestBody
    ) {
        if(random) {
            for (int i = 0; i < count; i++) {
                setTopBoxesDAO.addSetTopBox(StringRandomizer.nextAZaz09String(5));
            }
        } else if(requestBody != null) {
            setTopBoxesDAO.addSetTopBox(requestBody.name);
        } else {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/{id}")
    @ResponseBody
    public SetTopBox getOne(@PathVariable("id") int id) {
        return NullValidator.check(
                setTopBoxesDAO.getSetTopBox(id),
                () -> new HttpClientErrorException(HttpStatus.NOT_FOUND)
        );
    }


    record UpdateSetTopBoxBody(String name) {}
    @PostMapping("/{id}")
    public void updateOne(
            @PathVariable("id") int id,
            @RequestBody UpdateSetTopBoxBody requestBody
    ) {
        if(requestBody.name != null) setTopBoxesDAO.updateName(id, requestBody.name);
    }


    @DeleteMapping("/{id}")
    public void deleteOne(@PathVariable("id") int id) {
        setTopBoxesDAO.deleteOne(id);
    }


    @GetMapping("/count")
    @ResponseBody
    public int getCount() {
        return setTopBoxesDAO.getCount();
    }

}
