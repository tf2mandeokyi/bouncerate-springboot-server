package com.mndk.bouncerate.controller;

import com.mndk.bouncerate.db.AdvertisementProduct;
import com.mndk.bouncerate.db.AdvertisementProductDAO;
import com.mndk.bouncerate.db.BounceRateDAO;
import com.mndk.bouncerate.util.MapUtils;
import com.mndk.bouncerate.util.NullValidator;
import com.mndk.bouncerate.util.ResourceNotFoundException;
import com.mndk.bouncerate.util.StringRandomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/products")
@SuppressWarnings("unused")
public class AdvertisementProductController {


    @Autowired AdvertisementProductDAO productDAO;
    @Autowired BounceRateDAO bounceRateDAO;


    @GetMapping("")
    @ResponseBody
    public List<AdvertisementProduct> getProducts() {
        return productDAO.getAll();
    }


    @GetMapping("/{id}")
    @ResponseBody
    public AdvertisementProduct getProduct(@PathVariable("id") int id) {
        return NullValidator.check(productDAO.getProduct(id), ResourceNotFoundException::new);
    }


    @GetMapping("/{id}/bounceRateScore")
    @ResponseBody
    public float getBounceRateScore(
            @PathVariable(value = "id") int id,
            @RequestParam(value = "forceUpdate", defaultValue = "false") boolean forceUpdate
    ) {
        AdvertisementProduct product = productDAO.getProduct(id);

        boolean update = forceUpdate;
        if(!forceUpdate) {
            Date now = new Date(), scoreUpdatedDate = product.scoreUpdatedDate();

            if(scoreUpdatedDate == null) update = true;
            else {
                long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - scoreUpdatedDate.getTime());
                if (days >= 1) update = true;
            }
        }

        if(update) {
            float newScore = bounceRateDAO.getScore(id, 30);
            productDAO.updateBounceRateScore(id, newScore);
            return newScore;
        } else {
            return product.bounceRateScore();
        }
    }


    @PostMapping("/addRandom/{count}")
    public void addRandomProducts(@PathVariable("count") int count) {
        for(int i = 0; i < count; i++) {
            productDAO.addProduct(StringRandomizer.nextAZaz09String(5), true);
        }
    }


    @GetMapping("/getPriority")
    @ResponseBody
    public List<Integer> getPriority(
            @RequestParam(value = "forceUpdate", defaultValue = "false") boolean forceUpdate,
            @RequestParam(value = "count", defaultValue = "3") int count
    ) {
        Map<Integer, Float> bounceRateScoreMap = new HashMap<>();
        List<Integer> productIds = productDAO.getAllAvailabileIds();
        for(int productId : productIds) {
            bounceRateScoreMap.put(productId, this.getBounceRateScore(productId, forceUpdate));
        }

        return MapUtils.sortByValue(bounceRateScoreMap).subList(0, count);
    }

}
