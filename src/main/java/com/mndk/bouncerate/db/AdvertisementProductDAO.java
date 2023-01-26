package com.mndk.bouncerate.db;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlScript;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.statement.UseRowMapper;

import java.util.List;

public interface AdvertisementProductDAO {


    String PRODUCT_QUERY = """
            SELECT  id,
                    name,
                    availability,
                    CAST((enough_count + 1) / (available_count + 2) AS DECIMAL(11, 10)) AS bouncerate_score
            FROM (
                    SELECT  A.`id`,
                            A.`name`,
                            A.`availability`,
                            SUM(if(B.`bouncerate` < 30, 1, 0)) AS enough_count,
                            CAST(COUNT(B.`bouncerate`) AS DECIMAL(30, 10)) AS available_count
                    from `products` A
                            LEFT JOIN `bouncerates` B ON B.`product_id`=A.id
                    GROUP BY A.id
            ) AS C\s
    """;


    @SqlScript("""
            CREATE TABLE IF NOT EXISTS `products` (
                    `id`                INT             NOT NULL AUTO_INCREMENT PRIMARY KEY,
                    `name`              VARCHAR(100)    NOT NULL,
                    `availability`      BOOLEAN         NOT NULL,
            );
    """)
    void initializeTable();


    @SqlUpdate("DROP TABLE IF EXISTS `products`")
    void deleteTable();


    @SqlUpdate("INSERT INTO `products` (`name`, `availability`) VALUES (:name, :availability)")
    void addOne(
            @Bind("name")           String productName,
            @Bind("availability")   boolean availability
    );


    @SqlUpdate("UPDATE `products` SET `name` = :name WHERE `id` = :id")
    void updateName(
            @Bind("id")     int productId,
            @Bind("name")   String newName
    );


    @SqlUpdate("UPDATE `products` SET `availability` = :availability WHERE `id` = :id")
    void updateAvailability(
            @Bind("id")             int productId,
            @Bind("availability")   boolean availability
    );


    @SqlQuery(PRODUCT_QUERY)
    @UseRowMapper(AdvertisementProduct.Mapper.class)
    List<AdvertisementProduct> getAll();


    /**
     * @param count Product count per page
     * @param offset Works as same way as mysql's select offset
     */
    @SqlQuery(PRODUCT_QUERY + "LIMIT :count OFFSET :offset")
    @UseRowMapper(AdvertisementProduct.Mapper.class)
    List<AdvertisementProduct> getBulk(
            @Bind("count")      int count,
            @Bind("offset")     int offset
    );


    /**
     * @param count Product count per page
     * @param offset Works as same way as mysql's select offset
     */
    @SqlQuery(PRODUCT_QUERY + "ORDER BY bouncerate_score DESC LIMIT :count OFFSET :offset")
    @UseRowMapper(AdvertisementProduct.Mapper.class)
    List<AdvertisementProduct> getBulk_orderByScore(
            @Bind("count")      int count,
            @Bind("offset")     int offset
    );


    @SqlQuery(PRODUCT_QUERY + "WHERE `id` = :id")
    @UseRowMapper(AdvertisementProduct.Mapper.class)
    AdvertisementProduct getOne(@Bind("id") int productId);


    @SqlQuery("SELECT COUNT(*) FROM `products`")
    int getCount();


    @SqlQuery(PRODUCT_QUERY + "WHERE `availability` = TRUE")
    @UseRowMapper(AdvertisementProduct.Mapper.class)
    List<AdvertisementProduct> getAllAvailable();


    @SqlUpdate("DELETE FROM `products` WHERE `id` = :id")
    void deleteOne(@Bind("id") int productId);

}
