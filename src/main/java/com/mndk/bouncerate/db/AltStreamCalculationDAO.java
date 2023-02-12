package com.mndk.bouncerate.db;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface AltStreamCalculationDAO {


    @SqlUpdate("""
            DROP TABLE IF EXISTS `alt_str_captured_settopboxes`;
            CREATE TABLE `alt_str_captured_settopboxes` (
                `settopbox_id`  INT     NOT NULL,
                `captured`      BOOL    DEFAULT FALSE,
                
                PRIMARY KEY (`settopbox_id`)
            );
            
            DROP TABLE IF EXISTS `alt_str_excluded_categories`;
            CREATE TABLE `alt_str_excluded_categories` (
                `category_id`   INT     NOT NULL PRIMARY KEY
            );
    """)
    void resetCalculationTable();


    @SqlUpdate("""
            INSERT INTO `alt_str_captured_settopboxes` (`settopbox_id`, `captured`)
                SELECT  S.id,
                        IF(B.bouncerate >= :brMin AND B.bouncerate <= :brMax, 1, 0)
                    FROM `settopboxes` S
                    LEFT JOIN `bouncerates` B
                        ON B.category_id = :categoryId AND B.settopbox_id = S.id;
    """)
    void initializeData(
            @Bind("categoryId")     int categoryId,
            @Bind("brMin")          double minBounceRate,
            @Bind("brMax")          double maxBounceRate
    );


    @SqlQuery("""
            SELECT Q.category_id FROM (
                SELECT 	C.id AS category_id,
                        SUM(IF(B.bouncerate >= :brMin AND B.bouncerate <= :brMax, 1, 0)) AS settopboxes_count
                    FROM `product_categories` C
                    LEFT JOIN `bouncerates` B ON B.category_id = C.id
                    LEFT JOIN `alt_str_captured_settopboxes` R ON B.settopbox_id = R.settopbox_id AND R.captured = FALSE
                    WHERE C.id NOT IN (
                        SELECT `category_id` FROM `alt_str_excluded_categories`
                    )
                    GROUP BY C.id
                    ORDER BY settopboxes_count DESC
                    LIMIT 1
            ) AS Q
    """)
    Integer getCurrentBestCategoryId(
            @Bind("timeSlotId")     int timeSlotId,
            @Bind("brMin")          double minBounceRate,
            @Bind("brMax")          double maxBounceRate
    );


    @SqlUpdate("""
            INSERT INTO `alt_str_excluded_categories` VALUES (:categoryId);
    """)
    void excludeCategory(@Bind("categoryId") int categoryId);


    @SqlUpdate("""
            UPDATE `alt_str_captured_settopboxes` R
                LEFT JOIN `bouncerates` B ON B.category_id = :categoryId AND B.settopbox_id = R.settopbox_id
                SET `captured` = TRUE
                WHERE B.bouncerate >= :brMin AND B.bouncerate <= :brMax;
    """)
    void excludeCapturedSetTopBoxes(
            @Bind("categoryId")     int categoryId,
            @Bind("brMin")          double minBounceRate,
            @Bind("brMax")          double maxBounceRate
    );

}
