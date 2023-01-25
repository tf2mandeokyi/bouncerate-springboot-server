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


    /**
     * @param count Product count per page
     * @param offset Works as same way as mysql's select offset
     */
    @SqlQuery("SELECT * FROM `settopboxes` LIMIT :count OFFSET :offset")
    @UseRowMapper(SetTopBox.Mapper.class)
    List<SetTopBox> getBulk(
            @Bind("count")      int count,
            @Bind("offset")     int offset
    );


    @SqlQuery("SELECT * FROM `settopboxes` WHERE `id` = :id")
    @UseRowMapper(SetTopBox.Mapper.class)
    SetTopBox getSetTopBox(@Bind("id") int setTopBoxId);


    @SqlQuery("SELECT COUNT(*) FROM `settopboxes`")
    int getCount();


    @SqlUpdate("INSERT INTO `settopboxes` (name) VALUES (:name)")
    void addSetTopBox(@Bind("name") String setTopBoxName);


    @SqlUpdate("UPDATE `settopboxes` SET `name` = :name WHERE `id` = :id")
    void updateName(
            @Bind("id")     int setTopBoxId,
            @Bind("name")   String newName
    );


    @SqlQuery("SELECT `id` FROM `settopboxes`")
    List<Integer> getAllIds();


    @SqlUpdate("DELETE FROM `settopboxes` WHERE `id` = :id")
    void deleteOne(@Bind("id") int setTopBoxId);

}