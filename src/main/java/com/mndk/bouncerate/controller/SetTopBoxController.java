package com.mndk.bouncerate.controller;

import com.mndk.bouncerate.db.SetTopBoxesDAO;
import com.mndk.bouncerate.service.SetTopBoxService;
import com.mndk.bouncerate.util.Validator;
import com.mndk.bouncerate.util.ValueWrapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/setTopBoxes")
@SuppressWarnings("unused")
public class SetTopBoxController {


    SetTopBoxService setTopBoxService;


    @GetMapping("")
    @ResponseBody
    public List<SetTopBoxesDAO.SetTopBox> getMany(
            @RequestParam(value = "count", defaultValue = "-1") int count,
            @RequestParam(value = "page",  defaultValue = "1")  int pageNum
    ) {
        return Validator.checkNull(
                setTopBoxService.getPage(count, pageNum),
                () -> new HttpClientErrorException(HttpStatus.BAD_REQUEST)
        );
    }


    record AddSetTopBoxBody(String location) {}
    @PostMapping("")
    public void addOneOrMany(
            @RequestParam(value = "random", defaultValue = "false") boolean random,
            @RequestParam(value = "count",  defaultValue = "1")     int count,
            @RequestBody (required = false)                         AddSetTopBoxBody requestBody
    ) {
        if(random) {
            setTopBoxService.addManyRandom(count);
        } else if(requestBody != null) {
            setTopBoxService.addOne(new SetTopBoxesDAO.SetTopBox(-1, null, requestBody.location));
        } else {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/{id}")
    @ResponseBody
    public SetTopBoxesDAO.SetTopBox getOne(@PathVariable("id") int id) {
        return Validator.checkNull(
                setTopBoxService.getOne(id),
                () -> new HttpClientErrorException(HttpStatus.NOT_FOUND)
        );
    }


    @DeleteMapping("/{id}")
    public void deleteOne(@PathVariable("id") int setTopBoxId) {
        setTopBoxService.deleteOne(setTopBoxId);
    }


    @GetMapping("/count")
    @ResponseBody
    public ValueWrapper<Integer> getCount() {
        return new ValueWrapper<>(setTopBoxService.getCount());
    }

}
