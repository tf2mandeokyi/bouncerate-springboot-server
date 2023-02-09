package com.mndk.bouncerate.db;

import jakarta.annotation.Nullable;
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

public interface SetTopBoxesDAO {

    record SetTopBox(
            int id,
            String uuid,
            @Nullable String location
    ) {
        public static class Mapper implements RowMapper<SetTopBox> {
            @Override
            public SetTopBox map(ResultSet resultSet, StatementContext context) throws SQLException {
                return new SetTopBox(
                        resultSet.getInt("id"),
                        resultSet.getString("uuid"),
                        resultSet.getString("location")
                );
            }
        }
    }


    String SETTOPBOX_QUERY = """
            SELECT  `id`,
                    BIN_TO_UUID(`uuid_bin`) AS `uuid`,
                    `location`
            FROM `settopboxes`
    """;


    @SqlScript("""
            CREATE TABLE IF NOT EXISTS `settopboxes` (
                    `id`        INT             NOT NULL AUTO_INCREMENT,
                    `uuid_bin`  BINARY(16)      NOT NULL,
                    `location`  VARCHAR(100),
                    
                    PRIMARY KEY (`id`),
                    UNIQUE KEY uuid_key (`uuid_bin`)
            );
    """)
    void initializeTable();


    @SqlQuery(SETTOPBOX_QUERY)
    @UseRowMapper(SetTopBox.Mapper.class)
    List<SetTopBox> getAll();


    /**
     * @param count Product count per page
     * @param offset Works as same way as mysql's select offset
     */
    @SqlQuery(SETTOPBOX_QUERY + "LIMIT :count OFFSET :offset")
    @UseRowMapper(SetTopBox.Mapper.class)
    List<SetTopBox> getBulk(
            @Bind("count")      int count,
            @Bind("offset")     int offset
    );


    @SqlQuery(SETTOPBOX_QUERY + "WHERE `id` = :id")
    @UseRowMapper(SetTopBox.Mapper.class)
    SetTopBox getSetTopBox(@Bind("id") int setTopBoxId);


    @SqlQuery(SETTOPBOX_QUERY + "WHERE `uuid_bin` = UUID_TO_BIN(:uuid)")
    @UseRowMapper(SetTopBox.Mapper.class)
    SetTopBox getSetTopBox(@Bind("uuid") String uuid);


    @SqlQuery("SELECT COUNT(*) FROM `settopboxes`")
    int getCount();


    @SqlUpdate("INSERT INTO `settopboxes` (uuid_bin, location) VALUES (UUID_TO_BIN(UUID()), :location)")
    void addSetTopBox(@Bind("location") String location);


    @SqlUpdate("DELETE FROM `settopboxes` WHERE `id` = :id")
    void deleteOne(@Bind("id") int setTopBoxId);
}