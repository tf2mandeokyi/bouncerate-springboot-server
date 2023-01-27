package com.mndk.bouncerate.controller;

import com.mndk.bouncerate.db.ProductCategoryDAO;
import com.mndk.bouncerate.db.BounceRateDAO;
import com.mndk.bouncerate.db.SetTopBoxesDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@SuppressWarnings("unused")
public class AdministrativeController {


    @Autowired ProductCategoryDAO categoryDAO;
    @Autowired BounceRateDAO bounceRateDAO;
    @Autowired SetTopBoxesDAO setTopBoxesDAO;


    @PostMapping("/resetEverything")
    public void resetEverything() {
        categoryDAO.deleteTable();
        categoryDAO.initializeTable();
        bounceRateDAO.deleteTable();
        bounceRateDAO.initializeTable();
        setTopBoxesDAO.deleteTable();
        setTopBoxesDAO.initializeTable();
    }

}
