package com.mndk.bouncerate.db;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlScript;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.statement.UseRowMapper;

import java.util.List;

public interface ProductCategoryDAO {


    String CATEGORY_QUERY = """
            SELECT  id,
                    name,
                    CAST((enough_count + 1) / (available_count + 2) AS DECIMAL(11, 10)) AS bouncerate_score
            FROM (
                    SELECT  A.`id`,
                            A.`name`,
                            SUM(if(B.`bouncerate` < 30, 1, 0)) AS enough_count,
                            CAST(COUNT(B.`bouncerate`) AS DECIMAL(30, 10)) AS available_count
                    from `categories` A
                            LEFT JOIN `bouncerates` B ON B.`category_id`=A.id
                    GROUP BY A.id
            ) AS C\s
    """;


    @SqlScript("""
            CREATE TABLE IF NOT EXISTS `categories` (
                    `id`                INT             NOT NULL AUTO_INCREMENT PRIMARY KEY,
                    `name`              VARCHAR(100)    NOT NULL
            );
    """)
    void initializeTable();


    @SqlUpdate("DROP TABLE IF EXISTS `categories`")
    void deleteTable();


    @SqlUpdate("INSERT INTO `categories` (`name`) VALUES (:name)")
    void addOne(@Bind("name") String categoryName);


    @SqlUpdate("UPDATE `categories` SET `name` = :name WHERE `id` = :id")
    void updateName(
            @Bind("id")     int categoryId,
            @Bind("name")   String newName
    );


    @SqlQuery(CATEGORY_QUERY)
    @UseRowMapper(ProductCategory.Mapper.class)
    List<ProductCategory> getAll();


    /**
     * @param count Category count per page
     * @param offset Works as same way as mysql's select offset
     */
    @SqlQuery(CATEGORY_QUERY + "LIMIT :count OFFSET :offset")
    @UseRowMapper(ProductCategory.Mapper.class)
    List<ProductCategory> getBulk(
            @Bind("count")      int count,
            @Bind("offset")     int offset
    );


    /**
     * @param count Category count per page
     * @param offset Works as same way as mysql's select offset
     */
    @SqlQuery(CATEGORY_QUERY + "ORDER BY bouncerate_score DESC LIMIT :count OFFSET :offset")
    @UseRowMapper(ProductCategory.Mapper.class)
    List<ProductCategory> getBulk_orderByScore(
            @Bind("count")      int count,
            @Bind("offset")     int offset
    );


    @SqlQuery(CATEGORY_QUERY + "WHERE `id` = :id")
    @UseRowMapper(ProductCategory.Mapper.class)
    ProductCategory getOne(@Bind("id") int categoryId);


    @SqlQuery("SELECT COUNT(*) FROM `categories`")
    int getCount();


    @SqlUpdate("DELETE FROM `categories` WHERE `id` = :id")
    void deleteOne(@Bind("id") int categoryId);

}
