package com.mndk.bouncerate.service;

import com.mndk.bouncerate.db.BounceRateDAO;
import com.mndk.bouncerate.db.SetTopBoxesDAO;
import com.mndk.bouncerate.db.SetTopBoxesDAO.SetTopBox;
import com.mndk.bouncerate.util.UUIDUtils;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SetTopBoxService {


    BounceRateDAO bounceRateDAO;
    SetTopBoxesDAO setTopBoxesDAO;


    public SetTopBox getOne(int id) {
        return setTopBoxesDAO.getSetTopBox(id);
    }

    public List<SetTopBox> getPage(int countPerPage, int pageNumber) {
        if(countPerPage == -1) return setTopBoxesDAO.getAll();

        if(countPerPage >= 1 && pageNumber >= 1) {
            return setTopBoxesDAO.getBulk(countPerPage, (pageNumber - 1) * countPerPage);
        }

        return null;
    }

    public int getCount() {
        return setTopBoxesDAO.getCount();
    }

    public void addOne(@Nullable String location) {
        addOne(UUIDUtils.getRandomBytes(), location);
    }

    public void addOne(byte[] uuidBuffer, @Nullable String location) {
        setTopBoxesDAO.addSetTopBox(uuidBuffer, location);
    }

    public void addManyRandom(int count) {
        for (int i = 0; i < count; i++) {
            addOne(null);
        }
    }

    public void updateOne(int id, @Nullable String location) {
        // TODO: implement this
        throw new UnsupportedOperationException();
    }

    public void deleteOne(int id) {
        setTopBoxesDAO.deleteOne(id);
        bounceRateDAO.deleteBounceRatesOfSetTopBox(id);
    }
}
