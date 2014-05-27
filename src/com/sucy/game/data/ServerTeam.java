package com.sucy.game.data;

import com.sucy.game.mysql.table.TeamTable;

import java.util.ArrayList;
import java.util.List;

/**
 * A permanent team formed for playing minigames
 */
public class ServerTeam {

    private final TeamTable sqlTable;
    private final List<String> members;
    private final List<String> admins;

    /**
     * Initial constructor
     *
     * @param table team table for storing team data
     */
    public ServerTeam(TeamTable table) {
        this.sqlTable = table;
        this.members = new ArrayList<String>();
        this.admins = new ArrayList<String>();
    }

    /**
     * Constructor from loaded data
     *
     * @param table   team table for storing team data
     * @param members list of members on the team
     * @param admins  list of admins on the team
     */
    public ServerTeam(TeamTable table, List<String> members, List<String> admins) {
        this.members = members;
        this.admins = admins;
        this.sqlTable = table;
    }

    /**
     * Checks if the player is on the team
     *
     * @param playerName player to check
     * @return           true if on the team, false otherwise
     */
    public boolean isOnTeam(String playerName) {
        return members.contains(playerName) || admins.contains(playerName);
    }

    /**
     * Checks if the player is an admin on the team
     *
     * @param playerName name of the player to check
     * @return           true if an admin, false otherwise
     */
    public boolean isAdmin(String playerName) {
        return admins.contains(playerName);
    }
}
