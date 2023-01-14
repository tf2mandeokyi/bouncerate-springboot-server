package com.mndk.bouncerate.db;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public record SetTopBox(
        int id,
        String name
) {

    public static class Mapper implements RowMapper<SetTopBox> {
        @Override
        public SetTopBox map(ResultSet resultSet, StatementContext context) throws SQLException {
            return new SetTopBox(
                    resultSet.getInt("id"),
                    resultSet.getString("name")
            );
        }
    }
}
