package com.mndk.bouncerate.service;

import com.mndk.bouncerate.db.BounceRateDAO;
import com.mndk.bouncerate.db.BounceRateDAO.*;
import com.mndk.bouncerate.db.ProductCategoryDAO;
import com.mndk.bouncerate.db.ProductCategoryDAO.*;
import com.mndk.bouncerate.db.SetTopBoxesDAO;
import com.mndk.bouncerate.db.SetTopBoxesDAO.*;
import com.mndk.bouncerate.util.DoubleMinMax;
import com.mndk.bouncerate.util.distribution.SkewNormalDistribution;
import lombok.AllArgsConstructor;
import org.apache.commons.math3.distribution.RealDistribution;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class BounceRateService {


    private static final int MAX_BOUNCERATE_UPDATE_PER_QUERY = 50000;


    /**
     * Distribution object for random bounce rate values
     */
    private static final RealDistribution DISTRIBUTION = new SkewNormalDistribution(92, 32, -10);
    private static final DoubleMinMax RANGE = new DoubleMinMax(0, 100);


    ProductCategoryDAO categoryDAO;
    BounceRateDAO bounceRateDAO;
    SetTopBoxesDAO setTopBoxesDAO;


    // ===== GETTERS =====

    public Float getBounceRate(int categoryId, int setTopBoxId) {
        return bounceRateDAO.getBounceRate(categoryId, setTopBoxId);
    }


    // ===== SETTERS =====

    public void setBounceRate(int categoryId, int setTopBoxId, float bounceRate) {
        bounceRateDAO.setBounceRate(new BounceRateNode(categoryId, setTopBoxId, bounceRate));
    }


    // ===== RANDOMIZERS =====

    public void randomizeBounceRatesOfSetTopBox(int setTopBoxId) {
        var categoryIdList = categoryDAO.getAll().stream().map(ProductCategory::id).toList();
        this.randomize(categoryIdList, Collections.singletonList(setTopBoxId));
    }


    public void randomizeBounceRatesOfCategory(int categoryId) {
        var setTopBoxIdList = setTopBoxesDAO.getAll().stream().map(SetTopBox::id).toList();
        this.randomize(Collections.singletonList(categoryId), setTopBoxIdList);
    }


    public void randomizeAll() {
        var categoryIdList = categoryDAO.getAll().stream().map(ProductCategory::id).toList();
        var setTopBoxIdList = setTopBoxesDAO.getAll().stream().map(SetTopBox::id).toList();
        this.randomize(categoryIdList, setTopBoxIdList);
    }


    public void randomize(List<Integer> categoryIdList, List<Integer> setTopBoxIdList) {
        var nodes = new ArrayList<BounceRateNode>();
        int i = 0;
        for(int categoryId : categoryIdList) {
            for(int setTopBoxId : setTopBoxIdList) {
                double bounceRate = DISTRIBUTION.sample();
                bounceRate = RANGE.fitToRange(bounceRate);
                nodes.add(new BounceRateNode(categoryId, setTopBoxId, bounceRate));
                i++;

                if(i >= MAX_BOUNCERATE_UPDATE_PER_QUERY) {
                    bounceRateDAO.setBounceRate(nodes.toArray(BounceRateNode[]::new));
                    nodes.clear();
                    i = 0;
                }
            }
        }

        bounceRateDAO.setBounceRate(nodes.toArray(BounceRateNode[]::new));
    }
}
