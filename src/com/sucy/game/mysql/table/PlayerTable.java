package com.sucy.game.mysql.table;

import com.sucy.game.mysql.Column;
import com.sucy.game.mysql.ColumnType;
import com.sucy.game.mysql.MySQL;
import org.bukkit.plugin.Plugin;

import java.sql.ResultSet;

/**
 * Table for player stats
 */
public class PlayerTable extends Table {

    private static final String
            TABLE = "Players",
            NAME = "Name",
            KILLS = "Kills",
            DEATHS = "Deaths",
            TEAM = "Team";

    /**
     * Constructor
     *
     * @param plugin plugin reference
     * @param sql    sql reference
     */
    public PlayerTable(Plugin plugin, MySQL sql) {
        super(plugin, sql, TABLE,
                new Column(NAME, ColumnType.STRING),
                new Column(KILLS, ColumnType.INT),
                new Column(DEATHS, ColumnType.INT));
    }

    /**
     * Updates an entry in the database
     *
     * @param player player player
     * @param kills  number of kills
     * @param deaths number of deaths
     */
    public void updatePlayer(String player, int kills, int deaths) {

        // Update if exists
        if (sql.entryExists(TABLE, player)) {
            int[] stats = getStats(player);
            sql.updateSQL("UPDATE " + TABLE + " SET " + KILLS + "=" + (stats[0] + kills) + ", " + DEATHS + "=" + (stats[1] + deaths) + " WHERE " + NAME + "='" + player + "'");
        }

        // Insert if doesn't exist
        else sql.updateSQL("INSERT INTO " + TABLE + " VALUES ('" + player + "'," + kills + "," + deaths + ")");
    }

    /**
     * Gets the stats for a player
     *
     * @param player name of player to query
     * @return       { kills, deaths }
     */
    public int[] getStats(String player) {

        // Retrieve the stats
        try {
            ResultSet result = sql.query(TABLE, player);
            result.next();
            int kills = result.getInt(KILLS);
            int deaths = result.getInt(DEATHS);
            return new int[] { kills, deaths };
        }

        // Failed to retrieve stats
        catch (Exception ex) {
            plugin.getLogger().severe("Failed to retrieve player stats: " + ex.getMessage());
            return new int[] { 0, 0 };
        }
    }
}
