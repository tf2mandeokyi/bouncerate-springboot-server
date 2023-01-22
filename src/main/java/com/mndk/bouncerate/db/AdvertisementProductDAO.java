package com.mndk.bouncerate.db;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlScript;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.statement.UseRowMapper;

import java.util.List;

public interface AdvertisementProductDAO {

    @SqlScript("""
            CREATE TABLE IF NOT EXISTS `products` (
                    `id`                INT             NOT NULL AUTO_INCREMENT PRIMARY KEY,
                    `name`              VARCHAR(100)    NOT NULL,
                    `availability`      BOOLEAN         NOT NULL,
                    `bouncerate_score`  FLOAT           DEFAULT 0.5,
                    `score_updated`     DATE
            );
    """)
    void initializeTable();


    @SqlUpdate("DROP TABLE IF EXISTS `products`")
    void deleteTable();


    @SqlUpdate("INSERT INTO `products` (`name`, `availability`) VALUES (:name, :availability)")
    void addProduct(
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


    @SqlUpdate("""
            UPDATE `products` SET
                    `bouncerate_score` = :bouncerate_score,
                    `score_updated` = NOW()
                            WHERE `id` = :id
    """)
    void updateBounceRateScore(
            @Bind("id")                 int productId,
            @Bind("bouncerate_score")   float score
    );


    @SqlQuery("SELECT * FROM `products`")
    @UseRowMapper(AdvertisementProduct.Mapper.class)
    List<AdvertisementProduct> getAll();


    /**
     * @param count Product count per page
     * @param offset Works as same way as mysql's select offset
     */
    @SqlQuery("SELECT * FROM `products` LIMIT :count OFFSET :offset")
    @UseRowMapper(AdvertisementProduct.Mapper.class)
    List<AdvertisementProduct> getBulk(
            @Bind("count")      int count,
            @Bind("offset")     int offset
    );


    /**
     * @param count Product count per page
     * @param offset Works as same way as mysql's select offset
     */
    @SqlQuery("SELECT * FROM `products` ORDER BY `bouncerate_score` DESC LIMIT :count OFFSET :offset")
    @UseRowMapper(AdvertisementProduct.Mapper.class)
    List<AdvertisementProduct> getBulk_orderByScore(
            @Bind("count")      int count,
            @Bind("offset")     int offset
    );


    @SqlQuery("SELECT * FROM `products` WHERE `id` = :id")
    @UseRowMapper(AdvertisementProduct.Mapper.class)
    AdvertisementProduct getProduct(@Bind("id") int productId);


    @SqlQuery("SELECT COUNT(*) FROM `products`")
    int getCount();


    @SqlQuery("SELECT `id` FROM `products` WHERE `availability` = TRUE")
    List<Integer> getAllAvailabileIds();

}
