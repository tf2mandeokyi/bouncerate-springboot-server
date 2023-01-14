package com.mndk.bouncerate.controller;

import com.mndk.bouncerate.db.SetTopBox;
import com.mndk.bouncerate.db.SetTopBoxesDAO;
import com.mndk.bouncerate.util.NullValidator;
import com.mndk.bouncerate.util.ResourceNotFoundException;
import com.mndk.bouncerate.util.StringRandomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/setTopBoxes")
@SuppressWarnings("unused")
public class SetTopBoxController {


    @Autowired SetTopBoxesDAO setTopBoxesDAO;


    @GetMapping("")
    @ResponseBody
    public List<SetTopBox> getSetTopBoxes() {
        return setTopBoxesDAO.getAll();
    }


    @GetMapping("/{id}")
    @ResponseBody
    public SetTopBox getSetTopBox(@PathVariable("id") int id) {
        return NullValidator.check(setTopBoxesDAO.getSetTopBox(id), ResourceNotFoundException::new);
    }


    @PostMapping("/addRandom/{count}")
    public void addRandomSetTopBoxes(@PathVariable("count") int count) {
        for(int i = 0; i < count; i++) {
            setTopBoxesDAO.addSetTopBox(StringRandomizer.nextAZaz09String(5));
        }
    }

}
