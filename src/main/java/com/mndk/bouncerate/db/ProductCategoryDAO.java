package com.mndk.bouncerate.db;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlScript;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.statement.UseRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface ProductCategoryDAO {

    record ProductCategory(
            int id,
            String name
    ) {
        public static class Mapper implements RowMapper<ProductCategory> {
            @Override
            public ProductCategory map(ResultSet resultSet, StatementContext context) throws SQLException {
                return new ProductCategory(
                        resultSet.getInt("id"),
                        resultSet.getString("name")
                );
            }
        }
    }


    @SqlScript("""
            CREATE TABLE IF NOT EXISTS `product_categories` (
                    `id`    INT             NOT NULL AUTO_INCREMENT,
                    `name`  VARCHAR(100)    NOT NULL,
                    
                    PRIMARY KEY (`id`)
            );
    """)
    void initializeTable();


    @SqlUpdate("INSERT INTO `product_categories` (`name`) VALUES (:name)")
    void addOne(@Bind("name") String categoryName);


    @SqlUpdate("UPDATE `product_categories` SET `name` = :name WHERE `id` = :id")
    void updateName(
            @Bind("id")     int categoryId,
            @Bind("name")   String newName
    );


    @SqlQuery("SELECT * FROM `product_categories`")
    @UseRowMapper(ProductCategory.Mapper.class)
    List<ProductCategory> getAll();


    /**
     * @param count Category count per page
     * @param offset Works as same way as mysql's select offset
     */
    @SqlQuery("SELECT * FROM `product_categories` LIMIT :count OFFSET :offset")
    @UseRowMapper(ProductCategory.Mapper.class)
    List<ProductCategory> getBulk(
            @Bind("count")      int count,
            @Bind("offset")     int offset
    );


    /**
     * @param count Category count per page
     * @param offset Works as same way as mysql's select offset
     */
    @SqlQuery("SELECT * FROM `product_categories` ORDER BY bouncerate_score DESC LIMIT :count OFFSET :offset")
    @UseRowMapper(ProductCategory.Mapper.class)
    List<ProductCategory> getBulk_orderByScore(
            @Bind("count")      int count,
            @Bind("offset")     int offset
    );


    @SqlQuery("SELECT * FROM `product_categories` WHERE `id` = :id")
    @UseRowMapper(ProductCategory.Mapper.class)
    ProductCategory getOne(@Bind("id") int categoryId);


    @SqlQuery("SELECT COUNT(*) FROM `product_categories`")
    int getCount();


    @SqlUpdate("DELETE FROM `product_categories` WHERE `id` = :id")
    void deleteOne(@Bind("id") int categoryId);
}
