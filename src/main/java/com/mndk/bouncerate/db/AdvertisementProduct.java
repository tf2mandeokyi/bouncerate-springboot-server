package com.mndk.bouncerate.db;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public record AdvertisementProduct(
        int id,
        String name,
        boolean availability,
        float bounceRateScore,
        Date scoreUpdatedDate
) {

    public static class Mapper implements RowMapper<AdvertisementProduct> {
        @Override
        public AdvertisementProduct map(ResultSet resultSet, StatementContext context) throws SQLException {
            return new AdvertisementProduct(
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getBoolean("availability"),
                    resultSet.getFloat("bouncerate_score"),
                    resultSet.getDate("score_updated")
            );
        }
    }
}
