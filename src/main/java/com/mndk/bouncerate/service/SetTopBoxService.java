package com.mndk.bouncerate.service;

import com.mndk.bouncerate.db.BounceRateDAO;
import com.mndk.bouncerate.db.SetTopBoxesDAO;
import com.mndk.bouncerate.db.SetTopBoxesDAO.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SetTopBoxService implements EntityService<SetTopBox> {


    BounceRateDAO bounceRateDAO;
    SetTopBoxesDAO setTopBoxesDAO;


    @Override
    public SetTopBox getOne(int id) {
        return setTopBoxesDAO.getSetTopBox(id);
    }

    @Override
    public List<SetTopBox> getPage(int countPerPage, int pageNumber) {
        if(countPerPage == -1) return setTopBoxesDAO.getAll();

        if(countPerPage >= 1 && pageNumber >= 1) {
            return setTopBoxesDAO.getBulk(countPerPage, (pageNumber - 1) * countPerPage);
        }

        return null;
    }

    @Override
    public int getCount() {
        return setTopBoxesDAO.getCount();
    }

    @Override
    public void addOne(SetTopBox objectPart) {
        setTopBoxesDAO.addSetTopBox(objectPart.location());
    }

    @Override
    public void addManyRandom(int count) {
        for (int i = 0; i < count; i++) {
            setTopBoxesDAO.addSetTopBox(null);
        }
    }

    @Override
    public void updateOne(int id, SetTopBox objectPart) {
        // TODO: implement this
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteOne(int id) {
        setTopBoxesDAO.deleteOne(id);
        bounceRateDAO.deleteBounceRatesOfSetTopBox(id);
    }
}
