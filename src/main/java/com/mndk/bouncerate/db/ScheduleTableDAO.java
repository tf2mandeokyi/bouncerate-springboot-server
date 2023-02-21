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
    class ScheduleTableStreamNode {
        private final int timeSlotId;
        private final int streamNumber;
        private final @Nullable Integer categoryId;

        public static class Mapper implements RowMapper<ScheduleTableStreamNode> {
            @Override
            public ScheduleTableStreamNode map(ResultSet resultSet, StatementContext context) throws SQLException {
                return new ScheduleTableStreamNode(
                        resultSet.getInt("time_slot_id"),
                        resultSet.getInt("stream_no"),
                        resultSet.getInt("category_id")
                );
            }
        }
    }


    record ScheduleTableBounceRateNodeValue(
            double bounceRate,
            boolean needsUpdate
    ) {
        public ScheduleTableBounceRateNode toNode(int timeSlotId, int streamNumber) {
            return new ScheduleTableBounceRateNode(timeSlotId, streamNumber, this.bounceRate, this.needsUpdate);
        }

        public static ScheduleTableBounceRateNode[] toNodeArray(int timeSlotId, ScheduleTableBounceRateNodeValue[] valueArray) {
            return IntStream.range(0, valueArray.length)
                    .filter(index -> valueArray[index] != null)
                    .mapToObj(index -> valueArray[index].toNode(timeSlotId, index))
                    .toArray(ScheduleTableBounceRateNode[]::new);
        }
    }


    /* Don't make this a record! The current jdbi isn't smart enough to detect record's getters/setters! */
    @Getter @Setter @RequiredArgsConstructor
    class ScheduleTableBounceRateNode {
        private final int timeSlotId;
        private final int streamNumber;
        private final double bounceRate;
        private final boolean needsUpdate;

        public ScheduleTableBounceRateNodeValue toValueObject() {
            return new ScheduleTableBounceRateNodeValue(this.bounceRate, this.needsUpdate);
        }

        public static class Mapper implements RowMapper<ScheduleTableBounceRateNode> {
            @Override
            public ScheduleTableBounceRateNode map(ResultSet resultSet, StatementContext context) throws SQLException {
                return new ScheduleTableBounceRateNode(
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
    @UseRowMapper(ScheduleTableStreamNode.Mapper.class)
    List<ScheduleTableStreamNode> getAll();


    @SqlUpdate("""
            INSERT INTO `schedule_table` (`time_slot_id`, `stream_no`, `category_id`)
                    VALUES (:node.timeSlotId, :node.streamNumber, :node.categoryId)
                    ON DUPLICATE KEY UPDATE `category_id` = :node.categoryId
    """)
    void insertNode(@BindBean("node") ScheduleTableStreamNode node);


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
    @UseRowMapper(ScheduleTableBounceRateNode.Mapper.class)
    List<ScheduleTableBounceRateNode> getTimeSlotBounceRate(@Bind("timeSlotId") int timeSlotId);


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
            ) ScheduleTableBounceRateNode... bounceRate
    );


    @SqlUpdate("""
            UPDATE `schedule_table_bouncerate`
                    SET `needs_update` = TRUE
                    WHERE `time_slot_id` = :timeSlotId AND `stream_no` >= :streamNumber
    """)
    void markTimeSlotBounceRateOutdated(
            @Bind("timeSlotId")     int timeSlotId,
            @Bind("streamNumber")   int streamNumber
    );

}
