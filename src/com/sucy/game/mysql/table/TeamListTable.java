package com.sucy.game.mysql.table;

import com.sucy.game.mysql.Column;
import com.sucy.game.mysql.ColumnType;
import com.sucy.game.mysql.MySQL;
import org.bukkit.plugin.Plugin;

import java.sql.ResultSet;
import java.util.HashMap;

/**
 * Table for player stats
 */
public class TeamListTable extends Table {

    private static final String
            TABLE = "Teams",
            NAME = "Name",
            GAME = "Game";

    /**
     * Constructor
     *
     * @param plugin plugin reference
     * @param sql    sql reference
     */
    public TeamListTable(Plugin plugin, MySQL sql) {
        super(plugin, sql, TABLE,
                new Column(NAME, ColumnType.STRING),
                new Column(GAME, ColumnType.STRING));
    }

    /**
     * Updates an entry in the database
     *
     * @param name name of team to update
     * @param game game the team has reserved
     */
    public void updateTeam(String name, String game) {

        // Update if exists
        if (sql.entryExists(TABLE, name)) {
            sql.updateSQL("UPDATE " + TABLE + " SET " + GAME + "='" + game + "' WHERE " + NAME + "='" + name + "'");
        }

        // Insert if doesn't exist
        else sql.updateSQL("INSERT INTO " + TABLE + " VALUES ('" + name + "','" + game + "')");
    }

    /**
     * Removes a team from the table
     *
     * @param name team to remove
     */
    public void removeTeam(String name) {
        sql.updateSQL("DELETE FROM " + TABLE + " WHERE " + NAME + "='" + name + "'");
    }

    /**
     * Gets the stats for a player
     *
     * @return       { kills, deaths }
     */
    public HashMap<String, String> getTeamList() {

        HashMap<String, String> teams = new HashMap<String, String>();

        // Retrieve the stats
        try {
            ResultSet result = sql.queryAll(TABLE);
            while (result.next()) {
                teams.put(result.getString(NAME), result.getString(GAME));
            }
        }

        // Failed to retrieve stats
        catch (Exception ex) {
            plugin.getLogger().severe("Failed to retrieve team data: " + ex.getMessage());
        }

        return teams;
    }
}
