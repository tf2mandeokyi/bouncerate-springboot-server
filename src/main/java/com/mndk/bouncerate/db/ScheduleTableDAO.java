package com.mndk.bouncerate.db;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlScript;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.statement.UseRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

public interface ScheduleTableDAO {


    /* Don't make this a record! The current jdbi isn't smart enough to detect record's getters/setters! */
    @Getter @Setter @RequiredArgsConstructor
    class ScheduleNode {
        private final int timeSlotId;
        private final int streamNumber;
        private final @Nullable Integer categoryId;

        public static class Mapper implements RowMapper<ScheduleNode> {
            @Override
            public ScheduleNode map(ResultSet resultSet, StatementContext context) throws SQLException {
                return new ScheduleNode(
                        resultSet.getInt("time_slot_id"),
                        resultSet.getInt("stream_no"),
                        resultSet.getInt("category_id")
                );
            }
        }
    }


    /* Don't make this a record! The current jdbi isn't smart enough to detect record's getters/setters! */
    @Getter @Setter @RequiredArgsConstructor
    class TimeSlotBounceRate {
        private final Double onlyDefault;
        private final Double withAlt;
        private final boolean needsUpdate;

        public static class Mapper implements RowMapper<TimeSlotBounceRate> {
            @Override
            public TimeSlotBounceRate map(ResultSet resultSet, StatementContext context) throws SQLException {
                return new TimeSlotBounceRate(
                        resultSet.getDouble("only_default"),
                        resultSet.getDouble("with_alt"),
                        resultSet.getBoolean("needs_update")
                );
            }
        }
    }


    @SqlScript("""
            CREATE TABLE IF NOT EXISTS `schedule_table` (
                    `time_slot_id`  INT NOT NULL,
                    `stream_no`     INT NOT NULL,
                    `category_id`   INT NOT NULL,
                    
                    FOREIGN KEY (`category_id`) REFERENCES `product_categories` (`id`),
                    PRIMARY KEY (`time_slot_id`, `stream_no`)
            );
            CREATE TABLE IF NOT EXISTS `schedule_table_bouncerate` (
                    `time_slot_id`  INT     NOT NULL,
                    `only_default`  DOUBLE  NOT NULL,
                    `with_alt`      DOUBLE  NOT NULL,
                    `needs_update`  BOOL    DEFAULT FALSE,
                    
                    PRIMARY KEY (`time_slot_id`)
            );
    """)
    void initializeTable();


    @SqlQuery("""
            SELECT `category_id` FROM `schedule_table`
                    WHERE `time_slot_id` = :timeSlotId AND `stream_no` = :streamNumber
    """)
    Integer getCategoryId(
            @Bind("timeSlotId")     int timeSlotId,
            @Bind("streamNumber")   int streamNumber
    );


    @SqlQuery("SELECT * FROM `schedule_table`")
    @UseRowMapper(ScheduleNode.Mapper.class)
    List<ScheduleNode> getAll();


    @SqlUpdate("""
            INSERT INTO `schedule_table` (`time_slot_id`, `stream_no`, `category_id`)
                    VALUES (:node.timeSlotId, :node.streamNumber, :node.categoryId)
                    ON DUPLICATE KEY UPDATE `category_id` = :node.categoryId
    """)
    void insertNode(@BindBean("node") ScheduleNode node) throws SQLIntegrityConstraintViolationException;


    @SqlUpdate("""
            DELETE FROM `schedule_table`
                    WHERE `time_slot_id` = :timeSlotId AND `stream_no` = :streamNumber
    """)
    void deleteNode(
            @Bind("timeSlotId")     int timeSlotId,
            @Bind("streamNumber")   int streamNumber
    );


    @SqlQuery("""
            SELECT `only_default`, `with_alt`, `needs_update` FROM `schedule_table_bouncerate`
                    WHERE `time_slot_id` = :timeSlotId
    """)
    @UseRowMapper(TimeSlotBounceRate.Mapper.class)
    TimeSlotBounceRate getTimeSlotBounceRate(@Bind("timeSlotId") int timeSlotId);


    @SqlUpdate("""
            INSERT INTO `schedule_table_bouncerate` (`time_slot_id`, `only_default`, `with_alt`, `needs_update`)
                    VALUES (:timeSlotId, :bounceRate.onlyDefault, :bounceRate.withAlt, FALSE)
                    ON DUPLICATE KEY UPDATE
                            `only_default` = :bounceRate.onlyDefault,
                            `with_alt` = :bounceRate.withAlt,
                            `needs_update` = FALSE
    """)
    void updateTimeSlotBounceRate(
            @Bind("timeSlotId")         int timeSlotId,
            @BindBean("bounceRate")     TimeSlotBounceRate bounceRate
    );


    @SqlUpdate("""
            UPDATE `schedule_table_bouncerate`
                    SET `needs_update` = TRUE
                    WHERE `time_slot_id` = :timeSlotId
    """)
    void markTimeSlotBounceRateOutdated(@Bind("timeSlotId") int timeSlotId);

}
