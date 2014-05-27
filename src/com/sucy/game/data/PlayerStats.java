package com.sucy.game.data;

/**
 * PvP stats of a player
 */
public class PlayerStats {

    private int kills;
    private int deaths;

    /**
     * Initial Constructor
     */
    public PlayerStats() { }

    /**
     * Constructor for total data
     *
     * @param kills  number of kills
     * @param deaths number of deaths
     */
    public PlayerStats(int kills, int deaths) {
        this.kills = kills;
        this.deaths = deaths;
    }

    /**
     * @return player kill total
     */
    public int getKills() {
        return kills;
    }

    /**
     * @return player death total
     */
    public int getDeaths() {
        return deaths;
    }

    /**
     * @return player's KDA rounded to the nearest hundredth
     */
    public double getKDR() {
        if (deaths <= 1) return kills;
        return (int)(100.0 * kills / deaths + 0.5) / 100;
    }

    /**
     * Adds a kill to the stats
     */
    public void addKill() {
        kills++;
    }

    /**
     * Adds a death to the stats
     */
    public void addDeath() {
        deaths++;
    }

    /**
     * Clears the player stats
     */
    public void clear() {
        kills = 0;
        deaths = 0;
    }
}
