package com.mndk.bouncerate.controller;

import com.mndk.bouncerate.db.SetTopBoxesDAO;
import com.mndk.bouncerate.util.StringRandomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/setTopBoxes")
@SuppressWarnings("unused")
public class SetTopBoxController {


    @Autowired SetTopBoxesDAO setTopBoxesDAO;


    @PostMapping("/addRandom/{count}")
    public void addRandomSetTopBoxes(@PathVariable("count") int count) {
        for(int i = 0; i < count; i++) {
            setTopBoxesDAO.addSetTopBox(StringRandomizer.nextAZaz09String(5));
        }
    }

}
