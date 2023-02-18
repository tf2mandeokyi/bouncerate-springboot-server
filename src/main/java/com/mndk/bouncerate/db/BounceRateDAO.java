package com.mndk.bouncerate.db;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.config.KeyColumn;
import org.jdbi.v3.sqlobject.config.ValueColumn;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBeanList;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlScript;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public interface BounceRateDAO {


    /* Don't make this a record! The current jdbi isn't smart enough to detect record's getters/setters! */
    @Getter @Setter @RequiredArgsConstructor
    class BounceRateNode {
        private final int categoryId;
        private final int setTopBoxId;
        private final double bounceRate;

        public static class Mapper implements RowMapper<BounceRateNode> {
            @Override
            public BounceRateNode map(ResultSet resultSet, StatementContext context) throws SQLException {
                return new BounceRateNode(
                        resultSet.getInt("category_id"),
                        resultSet.getInt("settopbox_id"),
                        resultSet.getDouble("bouncerate")
                );
            }
        }
    }


    @SqlScript("""
            CREATE TABLE IF NOT EXISTS `bouncerates` (
                    `category_id`   INT     NOT NULL,
                    `settopbox_id`  INT     NOT NULL,
                    `bouncerate`    FLOAT   NOT NULL,
                    
                    FOREIGN KEY (`category_id`)  REFERENCES `product_categories` (`id`),
                    FOREIGN KEY (`settopbox_id`) REFERENCES `settopboxes`        (`id`),
                    PRIMARY KEY (`category_id`, `settopbox_id`)
            );
    """)
    void initializeTable();


    @SqlUpdate("""
            INSERT INTO `bouncerates` (`category_id`, `settopbox_id`, `bouncerate`)
                    VALUES <nodes> AS N
                    ON DUPLICATE KEY UPDATE
                            `bouncerate` = N.bouncerate;
    """)
    void setBounceRate(
            @BindBeanList(
                    value = "nodes",
                    propertyNames = { "categoryId", "setTopBoxId", "bounceRate" }
            ) BounceRateNode... nodes
    );


    @SqlQuery("""
            SELECT `bouncerate` FROM `bouncerates`
                    WHERE `category_id` = :category_id AND `settopbox_id` = :settopbox_id
    """)
    Float getBounceRate(
            @Bind("category_id")    int categoryId,
            @Bind("settopbox_id")   int setTopBoxId
    );


    @SqlQuery("SELECT `settopbox_id`, `bouncerate` FROM `bouncerates` WHERE `category_id` = :category_id")
    @KeyColumn("settopbox_id")
    @ValueColumn("bouncerate")
    Map<Integer, Float> getBounceRateMapOfCategory(@Bind("category_id") int categoryId);


    @SqlUpdate("DELETE FROM `bouncerates` WHERE `category_id` = :category_id")
    void deleteBounceRatesOfCategory(@Bind("category_id") int categoryId);


    @SqlUpdate("DELETE FROM `bouncerates` WHERE `settopbox_id` = :settopbox_id")
    void deleteBounceRatesOfSetTopBox(@Bind("settopbox_id") int setTopBoxId);



}
