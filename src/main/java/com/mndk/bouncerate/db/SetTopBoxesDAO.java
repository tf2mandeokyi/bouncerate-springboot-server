package com.mndk.bouncerate.db;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlScript;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.statement.UseRowMapper;

import java.util.List;

public interface SetTopBoxesDAO {

    @SqlScript("""
            CREATE TABLE IF NOT EXISTS `settopboxes` (
                    `id`    INT             NOT NULL AUTO_INCREMENT PRIMARY KEY,
                    `name`  VARCHAR(100)    NOT NULL
            );
    """)
    void initializeTable();


    @SqlUpdate("DROP TABLE IF EXISTS `settopboxes`")
    void deleteTable();


    @SqlQuery("SELECT * FROM `settopboxes`")
    @UseRowMapper(SetTopBox.Mapper.class)
    List<SetTopBox> getAll();


    @SqlQuery("SELECT * FROM `settopboxes` WHERE `id` = :id")
    @UseRowMapper(SetTopBox.Mapper.class)
    SetTopBox getSetTopBox(@Bind("id") int setTopBoxId);


    @SqlUpdate("INSERT INTO `settopboxes` (name) VALUES (:name)")
    void addSetTopBox(@Bind("name") String setTopBoxName);


    @SqlQuery("SELECT `id` FROM `products`")
    List<Integer> getAllIds();

}