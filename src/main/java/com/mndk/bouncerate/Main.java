package com.mndk.bouncerate;

import com.mndk.bouncerate.db.AdvertisementProductDAO;
import com.mndk.bouncerate.db.BounceRateDAO;
import com.mndk.bouncerate.db.BounceRateSchemaDAOs;
import com.mndk.bouncerate.db.SetTopBoxesDAO;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import java.util.List;
import java.util.Properties;

public class Main {

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.setProperty("user", "root");
        properties.setProperty("password", "asdf");

        Jdbi jdbi = Jdbi.create("jdbc:mysql://localhost:3306/bouncerate?characterEncoding=utf8", properties);
        jdbi.installPlugin(new SqlObjectPlugin());

        Handle jdbiHandle = jdbi.open();
        BounceRateSchemaDAOs schemaDAOs = new BounceRateSchemaDAOs(jdbiHandle);

        AdvertisementProductDAO productDAO = schemaDAOs.getAdvertisementProductDAO();
        BounceRateDAO bounceRateDAO = schemaDAOs.getBounceRateDAO();
        SetTopBoxesDAO setTopBoxesDAO = schemaDAOs.getSetTopBoxesDAO();

//        schemaDAOs.deleteTables();
//        schemaDAOs.initializeTables();
//        System.out.println("Tables ready");
//
//        for(int i = 0; i < 100; i++) {
//            setTopBoxesDAO.addSetTopBox(StringRandomizer.nextAZaz09String(5));
//            productDAO.addProduct(StringRandomizer.nextAZaz09String(5), true);
//        }
//        System.out.println("Set-top boxes and advertisement products are made");

        List<Integer> productIds = productDAO.getAllAvailabileIds();
//        bounceRateDAO.insertRandomizedBounceRates(productIds, setTopBoxesDAO.getAllIds(), 0, 70);
//        System.out.println("Bounce rates set");

        for(int productId : productIds) {
            float score = bounceRateDAO.getScore(productId, 30);
            productDAO.updateBounceRateScore(productId, score);
        }
        System.out.println("Bounce rate score calculated");

        jdbiHandle.close();
    }

}