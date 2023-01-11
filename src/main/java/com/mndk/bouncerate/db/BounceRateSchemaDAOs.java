package com.mndk.bouncerate.db;

import lombok.Getter;
import org.jdbi.v3.core.Handle;

public class BounceRateSchemaDAOs {

    @Getter private final BounceRateDAO bounceRateDAO;
    @Getter private final AdvertisementProductDAO advertisementProductDAO;
    @Getter private final SetTopBoxesDAO setTopBoxesDAO;

    public BounceRateSchemaDAOs(Handle handle) {
        this.bounceRateDAO = handle.attach(BounceRateDAO.class);
        this.advertisementProductDAO = handle.attach(AdvertisementProductDAO.class);
        this.setTopBoxesDAO = handle.attach(SetTopBoxesDAO.class);
    }

    public void deleteTables() {
        this.bounceRateDAO.deleteTable();
        this.advertisementProductDAO.deleteTable();
        this.setTopBoxesDAO.deleteTable();
    }

    public void initializeTables() {
        this.bounceRateDAO.initializeTable();
        this.advertisementProductDAO.initializeTable();
        this.setTopBoxesDAO.initializeTable();
    }

}
