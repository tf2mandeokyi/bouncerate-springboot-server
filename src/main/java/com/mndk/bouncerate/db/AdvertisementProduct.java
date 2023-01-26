package com.mndk.bouncerate.db;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public record AdvertisementProduct(
        int id,
        String name,
        boolean availability,
        double bounceRateScore
) {

    public static class Mapper implements RowMapper<AdvertisementProduct> {
        @Override
        public AdvertisementProduct map(ResultSet resultSet, StatementContext context) throws SQLException {
            return new AdvertisementProduct(
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getBoolean("availability"),
                    resultSet.getDouble("bouncerate_score")
            );
        }
    }
}
