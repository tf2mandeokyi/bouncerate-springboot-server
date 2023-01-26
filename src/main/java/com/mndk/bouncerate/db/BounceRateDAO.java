package com.mndk.bouncerate.db;

import org.jdbi.v3.sqlobject.config.KeyColumn;
import org.jdbi.v3.sqlobject.config.ValueColumn;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlScript;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.*;

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
            @Bind("bouncerate")     float bounceRate
    );


    @SqlQuery("""
            SELECT `bouncerate` FROM `bouncerates`
                    WHERE `product_id` = :product_id AND `settopbox_id` = :settopbox_id
    """)
    Float getBounceRate(
            @Bind("product_id")     int productId,
            @Bind("settopbox_id")   int setTopBoxId
    );


    @SqlQuery("SELECT `settopbox_id`, `bouncerate` FROM `bouncerates` WHERE `product_id` = :product_id")
    @KeyColumn("settopbox_id")
    @ValueColumn("bouncerate")
    Map<Integer, Float> getBounceRatesOfProduct(@Bind("product_id") int productId);


    @SqlQuery("SELECT `product_id`, `bouncerate` FROM `bouncerates` WHERE `settopbox_id` = :settopbox_id")
    @KeyColumn("product_id")
    @ValueColumn("bouncerate")
    Map<Integer, Float> getBounceRatesOfSetTopBox(@Bind("settopbox_id") int productId);


    @SqlQuery("SELECT `product_id` FROM `bouncerates`")
    List<Integer> getAllProductIds();


    @SqlQuery("SELECT `settopbox_id` FROM `bouncerates`")
    List<Integer> getAllSetTopBoxIds();


    @SqlUpdate("""
            DELETE FROM `bouncerates`
                    WHERE `product_id` = :product_id AND `settopbox_id` = :settopbox_id
    """)
    void deleteBounceRate(
            @Bind("product_id")     int productId,
            @Bind("settopbox_id")   int setTopBoxId
    );


    @SqlUpdate("DELETE FROM `bouncerates` WHERE `product_id` = :product_id")
    void deleteBounceRatesOfProduct(@Bind("product_id") int productId);


    @SqlUpdate("DELETE FROM `bouncerates` WHERE `settopbox_id` = :settopbox_id")
    void deleteBounceRatesOfSetTopBox(@Bind("settopbox_id") int setTopBoxId);



}
