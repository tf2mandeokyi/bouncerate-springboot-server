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
                    `category_id`   INT     NOT NULL,
                    `settopbox_id`  INT     NOT NULL,
                    `bouncerate`    FLOAT   NOT NULL
            );
            ALTER TABLE `bouncerates` ADD UNIQUE `unique_index` (`category_id`, `settopbox_id`);
    """)
    void initializeTable();


    @SqlUpdate("DROP TABLE IF EXISTS `bouncerates`")
    void deleteTable();


    @SqlUpdate("""
            INSERT INTO `bouncerates` (`category_id`, `settopbox_id`, `bouncerate`)
                    VALUES (:category_id, :settopbox_id, :bouncerate)
                    ON DUPLICATE KEY UPDATE
                            `bouncerate` = :bouncerate;
    """)
    void setBounceRate(
            @Bind("category_id")     int categoryId,
            @Bind("settopbox_id")   int setTopBoxId,
            @Bind("bouncerate")     float bounceRate
    );


    @SqlQuery("""
            SELECT `bouncerate` FROM `bouncerates`
                    WHERE `category_id` = :category_id AND `settopbox_id` = :settopbox_id
    """)
    Float getBounceRate(
            @Bind("category_id")     int categoryId,
            @Bind("settopbox_id")   int setTopBoxId
    );


    @SqlQuery("SELECT `settopbox_id`, `bouncerate` FROM `bouncerates` WHERE `category_id` = :category_id")
    @KeyColumn("settopbox_id")
    @ValueColumn("bouncerate")
    Map<Integer, Float> getBounceRatesOfCategory(@Bind("category_id") int categoryId);


    @SqlQuery("SELECT `category_id`, `bouncerate` FROM `bouncerates` WHERE `settopbox_id` = :settopbox_id")
    @KeyColumn("category_id")
    @ValueColumn("bouncerate")
    Map<Integer, Float> getBounceRatesOfSetTopBox(@Bind("settopbox_id") int categoryId);


    @SqlQuery("SELECT `category_id` FROM `bouncerates`")
    List<Integer> getAllCategoryIds();


    @SqlQuery("SELECT `settopbox_id` FROM `bouncerates`")
    List<Integer> getAllSetTopBoxIds();


    @SqlUpdate("""
            DELETE FROM `bouncerates`
                    WHERE `category_id` = :category_id AND `settopbox_id` = :settopbox_id
    """)
    void deleteBounceRate(
            @Bind("category_id")    int categoryId,
            @Bind("settopbox_id")   int setTopBoxId
    );


    @SqlUpdate("DELETE FROM `bouncerates` WHERE `category_id` = :category_id")
    void deleteBounceRatesOfCategory(@Bind("category_id") int categoryId);


    @SqlUpdate("DELETE FROM `bouncerates` WHERE `settopbox_id` = :settopbox_id")
    void deleteBounceRatesOfSetTopBox(@Bind("settopbox_id") int setTopBoxId);



}
