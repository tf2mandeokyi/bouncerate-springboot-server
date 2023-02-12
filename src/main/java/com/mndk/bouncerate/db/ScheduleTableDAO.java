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


    @SqlScript("""
            CREATE TABLE IF NOT EXISTS `schedule_table` (
                    `time_slot_id`  INT NOT NULL,
                    `stream_no`     INT NOT NULL,
                    `category_id`   INT NOT NULL,
                    
                    FOREIGN KEY (`category_id`) REFERENCES `product_categories` (`id`),
                    PRIMARY KEY (`time_slot_id`, `stream_no`)
            );
    """)
    void initializeTable();


    @SqlQuery("SELECT * FROM `schedule_table` A")
    @UseRowMapper(ScheduleNode.Mapper.class)
    List<ScheduleNode> getAll();


    @SqlUpdate("""
            INSERT INTO `schedule_table` (`time_slot_id`, `stream_no`, `category_id`)
                    VALUES (:node.timeSlotId, :node.streamNumber, :node.categoryId)
                    ON DUPLICATE KEY UPDATE `category_id` = :node.categoryId
    """)
    void insertNode(@BindBean(value = "node") ScheduleNode node) throws SQLIntegrityConstraintViolationException;


    @SqlUpdate("""
            DELETE FROM `schedule_table`
                    WHERE `time_slot_id` = :timeSlotId AND `stream_no` = :streamNumber
    """)
    void deleteNode(
            @Bind(value = "timeSlotId") int timeSlotId,
            @Bind(value = "streamNumber") int streamNumber
    );

    

}
