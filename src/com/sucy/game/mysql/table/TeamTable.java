package com.sucy.game.mysql.table;

import com.sucy.game.data.ServerTeam;
import com.sucy.game.mysql.Column;
import com.sucy.game.mysql.ColumnType;
import com.sucy.game.mysql.MySQL;
import org.bukkit.plugin.Plugin;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Table for player stats
 */
public class TeamTable extends Table {

    private static final String
            PLAYER = "Player",
            ADMIN = "Admin";

    private String teamName;

    /**
     * Constructor
     *
     * @param plugin plugin reference
     * @param sql    sql reference
     */
    public TeamTable(Plugin plugin, MySQL sql, String teamName) {
        super(plugin, sql, teamName,
                new Column(PLAYER, ColumnType.STRING),
                new Column(ADMIN, ColumnType.INT));
        this.teamName = teamName;
    }

    /**
     * Updates an entry in the database
     *
     * @param admins  admins on the team
     * @param members members on the team
     */
    public void updateTeam(List<String> admins, List<String> members) {

        String update = "DELETE * FROM " + teamName + ";";

        // Admin updates
        for (String admin : admins) {
            update += "INSERT INTO " + teamName + " VALUES ('" + admin + "',1);";
        }

        // Member updates
        for (String member : members) {
            update += "INSERT INTO " + teamName + " VALUES ('" + member + "',2);";
        }

        // Send the update
        sql.updateSQL(update);
    }

    /**
     * Gets the stats for a player
     *
     * @param team name of player to query
     * @return     { kills, deaths }
     */
    public ServerTeam loadTeam(String team) {

        List<String> admins = new ArrayList<String>();
        List<String> members = new ArrayList<String>();

        // Retrieve the stats
        try {
            ResultSet result = sql.queryAll(teamName);
            while (result.next()) {
                String name = result.getString(PLAYER);
                int type = result.getInt(ADMIN);
                if (type == 1) admins.add(name);
                else members.add(name);
            }
        }

        // Failed to retrieve stats
        catch (Exception ex) {
            plugin.getLogger().severe("Failed to retrieve team data: " + ex.getMessage());
        }

        return new ServerTeam(this, members, admins);
    }
}
