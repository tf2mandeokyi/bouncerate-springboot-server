package com.mndk.bouncerate.db;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.statement.SqlScript;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ScheduleTableDAO {

    record ScheduleNode(
            int timeSlotId,
            int streamNumber,
            int categoryId
    ) {
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

}
