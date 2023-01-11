package com.mndk.bouncerate.db;

import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

@RequiredArgsConstructor
public class AdvertisementProduct {

    private final int id;
    private final String name;
    private final boolean availability;
    private final float bounceRateScore;

    public static class Mapper implements RowMapper<AdvertisementProduct> {

        @Override
        public AdvertisementProduct map(ResultSet resultSet, StatementContext context) throws SQLException {
            return new AdvertisementProduct(
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getBoolean("availability"),
                    resultSet.getFloat("bouncerate_score")
            );
        }

    }
}
