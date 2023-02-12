package com.mndk.bouncerate.service;

import com.mndk.bouncerate.db.BounceRateDAO;
import com.mndk.bouncerate.db.ProductCategoryDAO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductCategoryService implements EntityService<ProductCategoryDAO.ProductCategory> {

    ProductCategoryDAO categoryDAO;
    BounceRateDAO bounceRateDAO;

    @Override
    public ProductCategoryDAO.ProductCategory getOne(int id) {
        return categoryDAO.getOne(id);
    }

    @Override
    public List<ProductCategoryDAO.ProductCategory> getPage(int countPerPage, int pageNumber) {
        if(countPerPage == -1) return categoryDAO.getAll();

        if(countPerPage >= 1 && pageNumber >= 1) {
            return categoryDAO.getBulk(countPerPage, (pageNumber - 1) * countPerPage);
        }

        return null;
    }

    @Override
    public int getCount() {
        return categoryDAO.getCount();
    }

    @Override
    public void addOne(ProductCategoryDAO.ProductCategory objectPart) {
        categoryDAO.addOne(objectPart.name());
    }

    @Override
    public void addManyRandom(int count) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateOne(int id, ProductCategoryDAO.ProductCategory objectPart) {
        if(objectPart.name() != null) categoryDAO.updateName(id, objectPart.name());
    }

    @Override
    public void deleteOne(int id) {
        categoryDAO.deleteOne(id);
        bounceRateDAO.deleteBounceRatesOfCategory(id);
    }

}
