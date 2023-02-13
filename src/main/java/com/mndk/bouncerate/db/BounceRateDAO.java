package com.mndk.bouncerate.db;

import org.jdbi.v3.sqlobject.config.KeyColumn;
import org.jdbi.v3.sqlobject.config.ValueColumn;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlScript;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.Map;

public interface BounceRateDAO {


    @SqlScript("""
            CREATE TABLE IF NOT EXISTS `bouncerates` (
                    `category_id`   INT     NOT NULL,
                    `settopbox_id`  INT     NOT NULL,
                    `bouncerate`    FLOAT   NOT NULL,
                    
                    FOREIGN KEY (`category_id`)  REFERENCES `product_categories` (`id`),
                    FOREIGN KEY (`settopbox_id`) REFERENCES `settopboxes`        (`id`),
                    PRIMARY KEY (`category_id`, `settopbox_id`)
            );
    """)
    void initializeTable();


    @SqlUpdate("""
            INSERT INTO `bouncerates` (`category_id`, `settopbox_id`, `bouncerate`)
                    VALUES (:category_id, :settopbox_id, :bouncerate)
                    ON DUPLICATE KEY UPDATE
                            `bouncerate` = :bouncerate;
    """)
    void setBounceRate(
            @Bind("category_id")    int categoryId,
            @Bind("settopbox_id")   int setTopBoxId,
            @Bind("bouncerate")     float bounceRate
    );


    @SqlUpdate("""
            INSERT INTO `bouncerates` (category_id, settopbox_id, bouncerate) (
                    SELECT  `id` AS category_id,
                            :settopbox_id AS settopbox_id,
                            (:rand_start + RAND() * :rand_size) AS bouncerate
                    FROM `product_categories`
            ) ON DUPLICATE KEY UPDATE
                    `bouncerate` = (RAND()) * 100
    """)
    void randomizeBounceRatesOfSetTopBox(
            @Bind("settopbox_id")   int setTopBoxId,
            @Bind("rand_start")     double randomStart,
            @Bind("rand_size")      double randomSize
    );


    @SqlUpdate("""
            INSERT INTO `bouncerates` (category_id, settopbox_id, bouncerate) (
                    SELECT  :category_id AS category_id,
                            `id` AS settopbox_id,
                            (:rand_start + RAND() * :rand_size) AS bouncerate
                    FROM `settopboxes`
            ) ON DUPLICATE KEY UPDATE
                    `bouncerate` = (RAND()) * 100
    """)
    void randomizeBounceRatesOfCategory(
            @Bind("category_id")    int categoryId,
            @Bind("rand_start")     double randomStart,
            @Bind("rand_size")      double randomSize
    );


    @SqlQuery("""
            SELECT `bouncerate` FROM `bouncerates`
                    WHERE `category_id` = :category_id AND `settopbox_id` = :settopbox_id
    """)
    Float getBounceRate(
            @Bind("category_id")    int categoryId,
            @Bind("settopbox_id")   int setTopBoxId
    );


    @SqlQuery("SELECT `settopbox_id`, `bouncerate` FROM `bouncerates` WHERE `category_id` = :category_id")
    @KeyColumn("settopbox_id")
    @ValueColumn("bouncerate")
    Map<Integer, Float> getBounceRateMapOfCategory(@Bind("category_id") int categoryId);


    @SqlUpdate("DELETE FROM `bouncerates` WHERE `category_id` = :category_id")
    void deleteBounceRatesOfCategory(@Bind("category_id") int categoryId);


    @SqlUpdate("DELETE FROM `bouncerates` WHERE `settopbox_id` = :settopbox_id")
    void deleteBounceRatesOfSetTopBox(@Bind("settopbox_id") int setTopBoxId);



}
