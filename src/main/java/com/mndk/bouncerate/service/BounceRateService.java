package com.mndk.bouncerate.service;

import com.mndk.bouncerate.db.BounceRateDAO;
import com.mndk.bouncerate.db.ProductCategoryDAO;
import com.mndk.bouncerate.db.SetTopBoxesDAO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BounceRateService {


    ProductCategoryDAO categoryDAO;
    BounceRateDAO bounceRateDAO;
    SetTopBoxesDAO setTopBoxesDAO;


    // ===== GETTERS =====

    public double getBounceRate(int categoryId, int setTopBoxId) {
        return bounceRateDAO.getBounceRate(categoryId, setTopBoxId);
    }


    // ===== SETTERS =====

    public void setBounceRate(int categoryId, int setTopBoxId, double bounceRate) {
        bounceRateDAO.setBounceRate(categoryId, setTopBoxId, (float) bounceRate);
    }


    // ===== RANDOMIZERS =====

    public void randomizeBounceRatesOfSetTopBox(int setTopBoxId, double min, double max) {
        bounceRateDAO.randomizeBounceRatesOfSetTopBox(setTopBoxId, min, max - min);
    }


    public void randomizeAll(double min, double max) {
        List<ProductCategoryDAO.ProductCategory> categories = categoryDAO.getAll();
        List<Integer> categoryIdList = categories.stream().map(ProductCategoryDAO.ProductCategory::id).toList();
        for(int categoryId : categoryIdList) {
            bounceRateDAO.randomizeBounceRatesOfCategory(categoryId, min, max - min);
        }
    }
}
