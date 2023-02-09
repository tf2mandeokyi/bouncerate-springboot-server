package com.mndk.bouncerate.db;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface TemporaryBounceRateCalculationDAO {

    @SqlUpdate("""
            DROP TABLE IF EXISTS `range_included_settopboxes`;
            CREATE TABLE `range_included_settopboxes` (
                `settopbox_id`  INT     NOT NULL,
                `included`      BOOL    DEFAULT FALSE,
                
                PRIMARY KEY (`settopbox_id`)
            );
            
            SET @selectedId := (
                SELECT `category_id` FROM SCHEDULE_TABLE
                    WHERE `time_slot_id` = :timeSlotId AND `stream_no` = 0
            );
            
            DELETE FROM `schedule_table` WHERE `time_slot_id` = :timeSlotId AND `stream_no` != 0;
            
            INSERT INTO `range_included_settopboxes` (`settopbox_id`, `included`)
                SELECT  S.id,
                        IF(B.bouncerate >= :brMin AND B.bouncerate <= :brMax, 1, 0)
                    FROM `settopboxes` S
                    LEFT JOIN `bouncerates` B
                        ON B.category_id = @selectedId AND B.settopbox_id = S.id;
    """)
    void initialize(
            @Bind("timeSlotId") int timeSlotId,
            @Bind("brMin")      double minBounceRate,
            @Bind("brMax")      double maxBounceRate
    );

    @SqlUpdate("""
            SET @selectedId := (
                SELECT Q.category_id FROM (
                    SELECT 	C.id AS category_id,
                            SUM(IF(B.bouncerate >= :brMin AND B.bouncerate <= :brMax, 1, 0)) AS settopboxes_count
                        FROM `product_categories` C
                        LEFT JOIN `bouncerates` B ON B.category_id = C.id
                        LEFT JOIN `range_included_settopboxes` R ON B.settopbox_id = R.settopbox_id AND R.included = FALSE
                        WHERE C.id NOT IN (
                            SELECT `category_id` FROM `schedule_table` S WHERE S.time_slot_id = :timeSlotId
                        )
                        GROUP BY C.id
                        ORDER BY settopboxes_count DESC
                        LIMIT 1
                ) AS Q
            );
            
            INSERT INTO `schedule_table` (`time_slot_id`, `stream_no`, `category_id`)
                VALUES (:timeSlotId, :nthTime, @selectedId)
                ON DUPLICATE KEY UPDATE `category_id` = @selectedId;
            
            UPDATE `range_included_settopboxes` R
                LEFT JOIN `bouncerates` B ON B.category_id = @selectedId AND B.settopbox_id = R.settopbox_id
                SET `included` = TRUE
                WHERE B.bouncerate >= :brMin AND B.bouncerate <= :brMax;
    """)
    void loop(
            @Bind("timeSlotId") int timeSlotId,
            @Bind("nthTime")    int nthTime,
            @Bind("brMin")      double minBounceRate,
            @Bind("brMax")      double maxBounceRate
    );

}
