package com.mndk.bouncerate.db;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public record ProductCategory(
        int id,
        String name,
        double bounceRateScore
) {

    public static class Mapper implements RowMapper<ProductCategory> {
        @Override
        public ProductCategory map(ResultSet resultSet, StatementContext context) throws SQLException {
            return new ProductCategory(
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getDouble("bouncerate_score")
            );
        }
    }
}
