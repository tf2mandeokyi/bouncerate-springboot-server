package com.mndk.bouncerate.db;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlScript;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Random;

public interface BounceRateDAO {

    @SqlScript("""
            CREATE TABLE IF NOT EXISTS `bouncerates` (
                    `product_id`    INT     NOT NULL,
                    `settopbox_id`  INT     NOT NULL,
                    `bouncerate`    FLOAT   NOT NULL
            );
            ALTER TABLE `bouncerates` ADD UNIQUE `unique_index` (`product_id`, `settopbox_id`);
    """)
    void initializeTable();


    @SqlUpdate("DROP TABLE IF EXISTS `bouncerates`")
    void deleteTable();


    @SqlUpdate("""
            INSERT INTO `bouncerates` (`product_id`, `settopbox_id`, `bouncerate`)
                    VALUES (:product_id, :settopbox_id, :bouncerate)
                    ON DUPLICATE KEY UPDATE
                            `bouncerate` = :bouncerate;
    """)
    void setBounceRate(
            @Bind("product_id")     int productId,
            @Bind("settopbox_id")   int setTopBoxId,
            @Bind("bouncerate")     float bouncerate
    );


    default void insertRandomizedBounceRates(
            List<Integer> productIdList, List<Integer> setTopBoxIdList, float min, float max
    ) {
        Random random = new Random();
        for(int productId : productIdList) for(int setTopBoxId : setTopBoxIdList) {
            this.setBounceRate(productId, setTopBoxId, random.nextFloat(min, max));
        }
    }


    @SqlQuery("SELECT `bouncerate` FROM `bouncerates` WHERE `product_id` = :product_id")
    List<Float> getBounceRates(@Bind("product_id") int productId);


    default float getScore(int productId, float bounceRateThreshold) {
        int lessThanThreshold = 0;

        List<Float> bounceRates = this.getBounceRates(productId);
        for(float bounceRate : bounceRates) {
            if(bounceRate <= bounceRateThreshold) lessThanThreshold++;
        }

        return (lessThanThreshold + 1) / (float) (bounceRates.size() + 2);
    }

}
