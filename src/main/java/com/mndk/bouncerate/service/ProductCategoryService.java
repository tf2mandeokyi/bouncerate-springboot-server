package com.mndk.bouncerate.service;

import com.mndk.bouncerate.db.BounceRateDAO;
import com.mndk.bouncerate.db.ProductCategoryDAO;
import com.mndk.bouncerate.db.ProductCategoryDAO.*;
import com.mndk.bouncerate.util.Validator;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductCategoryService {


    ProductCategoryDAO categoryDAO;
    BounceRateDAO bounceRateDAO;


    public ProductCategory getOne(int id) {
        return Validator.checkNull(
                categoryDAO.getOne(id),
                () -> new HttpClientErrorException(HttpStatus.NOT_FOUND)
        );
    }

    public List<ProductCategory> getPage(int countPerPage, int pageNumber) {
        if(countPerPage == -1) return categoryDAO.getAll();

        if(countPerPage >= 1 && pageNumber >= 1) {
            return categoryDAO.getBulk(countPerPage, (pageNumber - 1) * countPerPage);
        }

        return null;
    }

    public int getCount() {
        return categoryDAO.getCount();
    }

    public void addOne(String name) {
        categoryDAO.addOne(name);
    }

    public void updateOne(int id, String name) {
        Validator.checkNull(
                categoryDAO.getOne(id),
                () -> new HttpClientErrorException(HttpStatus.NOT_FOUND)
        );
        if(name != null) categoryDAO.updateName(id, name);
    }

    public void deleteOne(int id) {
        Validator.checkNull(
                categoryDAO.getOne(id),
                () -> new HttpClientErrorException(HttpStatus.NOT_FOUND)
        );
        categoryDAO.deleteOne(id);
        bounceRateDAO.deleteBounceRatesOfCategory(id);
    }

}
