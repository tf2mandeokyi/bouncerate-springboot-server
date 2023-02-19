package com.mndk.bouncerate.db;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.customizer.BindBeanList;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlScript;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.statement.UseRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.IntStream;

public interface ScheduleTableDAO {


    /* Don't make this a record! The current jdbi isn't smart enough to detect record's getters/setters! */
    @Getter @Setter @RequiredArgsConstructor
    class TimeSlotScheduleNode {
        private final int timeSlotId;
        private final int streamNumber;
        private final @Nullable Integer categoryId;

        public static class Mapper implements RowMapper<TimeSlotScheduleNode> {
            @Override
            public TimeSlotScheduleNode map(ResultSet resultSet, StatementContext context) throws SQLException {
                return new TimeSlotScheduleNode(
                        resultSet.getInt("time_slot_id"),
                        resultSet.getInt("stream_no"),
                        resultSet.getInt("category_id")
                );
            }
        }
    }


    record TimeSlotBounceRateValue(
            double bounceRate,
            boolean needsUpdate
    ) {
        public TimeSlotBounceRateNode toNode(int timeSlotId, int streamNumber) {
            return new TimeSlotBounceRateNode(timeSlotId, streamNumber, this.bounceRate, this.needsUpdate);
        }

        public static TimeSlotBounceRateNode[] toNodeArray(int timeSlotId, TimeSlotBounceRateValue[] valueArray) {
            return IntStream.range(0, valueArray.length)
                    .filter(index -> valueArray[index] != null)
                    .mapToObj(index -> valueArray[index].toNode(timeSlotId, index))
                    .toArray(TimeSlotBounceRateNode[]::new);
        }
    }


    /* Don't make this a record! The current jdbi isn't smart enough to detect record's getters/setters! */
    @Getter @Setter @RequiredArgsConstructor
    class TimeSlotBounceRateNode {
        private final int timeSlotId;
        private final int streamNumber;
        private final double bounceRate;
        private final boolean needsUpdate;

        public TimeSlotBounceRateValue toValueObject() {
            return new TimeSlotBounceRateValue(this.bounceRate, this.needsUpdate);
        }

        public static class Mapper implements RowMapper<TimeSlotBounceRateNode> {
            @Override
            public TimeSlotBounceRateNode map(ResultSet resultSet, StatementContext context) throws SQLException {
                return new TimeSlotBounceRateNode(
                        resultSet.getInt("time_slot_id"),
                        resultSet.getInt("stream_no"),
                        resultSet.getDouble("bouncerate"),
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
                    `stream_no`     INT     NOT NULL,
                    `bouncerate`    DOUBLE  NOT NULL,
                    `needs_update`  BOOL    DEFAULT FALSE,
                    
                    PRIMARY KEY (`time_slot_id`, `stream_no`)
            );
    """)
    void initializeTable();


    @SqlQuery("""
            SELECT `category_id` FROM `schedule_table`
                    WHERE `time_slot_id` = :timeSlotId AND `stream_no` = :streamNumber
    """)
    @Nullable
    Integer getCategoryId(
            @Bind("timeSlotId")     int timeSlotId,
            @Bind("streamNumber")   int streamNumber
    );


    @SqlQuery("SELECT * FROM `schedule_table`")
    @UseRowMapper(TimeSlotScheduleNode.Mapper.class)
    List<TimeSlotScheduleNode> getAll();


    @SqlUpdate("""
            INSERT INTO `schedule_table` (`time_slot_id`, `stream_no`, `category_id`)
                    VALUES (:node.timeSlotId, :node.streamNumber, :node.categoryId)
                    ON DUPLICATE KEY UPDATE `category_id` = :node.categoryId
    """)
    void insertNode(@BindBean("node") TimeSlotScheduleNode node);


    @SqlUpdate("""
            DELETE FROM `schedule_table`
                    WHERE `time_slot_id` = :timeSlotId AND `stream_no` = :streamNumber
    """)
    void deleteNode(
            @Bind("timeSlotId")     int timeSlotId,
            @Bind("streamNumber")   int streamNumber
    );


    @SqlQuery("""
            SELECT * FROM `schedule_table_bouncerate`
                    WHERE `time_slot_id` = :timeSlotId
    """)
    @UseRowMapper(TimeSlotBounceRateNode.Mapper.class)
    List<TimeSlotBounceRateNode> getTimeSlotBounceRate(@Bind("timeSlotId") int timeSlotId);


    @SqlUpdate("""
            INSERT INTO `schedule_table_bouncerate` (`time_slot_id`, `stream_no`, `bouncerate`, `needs_update`)
                    VALUES <nodes> AS N
                    ON DUPLICATE KEY UPDATE
                            `bouncerate` = N.bouncerate,
                            `needs_update` = N.needs_update
    """)
    void updateTimeSlotBounceRate(
            @BindBeanList(
                    value = "nodes",
                    propertyNames = { "timeSlotId", "streamNumber", "bounceRate", "needsUpdate" }
            ) TimeSlotBounceRateNode... bounceRate
    );


    @SqlUpdate("""
            UPDATE `schedule_table_bouncerate`
                    SET `needs_update` = TRUE
                    WHERE `time_slot_id` = :timeSlotId
    """)
    void markTimeSlotBounceRateOutdated(@Bind("timeSlotId") int timeSlotId);

}
