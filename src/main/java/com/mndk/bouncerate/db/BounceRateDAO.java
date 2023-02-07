package com.mndk.bouncerate.db;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlScript;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.statement.UseRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface BounceRateDAO {

    record BounceRateCount(
            int enoughCount,
            int availableCount
    ) {
        public double getScore() {
            return (enoughCount + 1) / (double) (availableCount + 2);
        }

        public static class Mapper implements RowMapper<BounceRateCount> {
            @Override
            public BounceRateCount map(ResultSet resultSet, StatementContext context) throws SQLException {
                return new BounceRateCount(
                        resultSet.getInt("enough_count"),
                        resultSet.getInt("available_count")
                );
            }
        }
    }


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


//    @SqlQuery("""
//            SELECT  SUM(if(B.`bouncerate` >= :br_min AND B.`bouncerate` <= :br_max, 1, 0)) AS enough_count,
//                    CAST(COUNT(B.`bouncerate`) AS DECIMAL(30, 10)) AS available_count
//            FROM `product_categories` A
//                    LEFT JOIN `bouncerates` B ON B.`category_id`=:category_id
//            GROUP BY A.id
//    """)
    @SqlQuery("""
            SELECT  SUM(if(`bouncerate` >= :br_min AND `bouncerate` <= :br_max, 1, 0)) AS enough_count,
                    COUNT(*) AS available_count
            FROM `bouncerates` WHERE `category_id` = :category_id
    """)
    @UseRowMapper(BounceRateCount.Mapper.class)
    BounceRateCount getBounceRateCountOfCategory(
            @Bind("category_id")    int categoryId,
            @Bind("br_min")         double minBounceRate,
            @Bind("br_max")         double maxBounceRate
    );


    @SqlUpdate("DELETE FROM `bouncerates` WHERE `category_id` = :category_id")
    void deleteBounceRatesOfCategory(@Bind("category_id") int categoryId);


    @SqlUpdate("DELETE FROM `bouncerates` WHERE `settopbox_id` = :settopbox_id")
    void deleteBounceRatesOfSetTopBox(@Bind("settopbox_id") int setTopBoxId);



}
