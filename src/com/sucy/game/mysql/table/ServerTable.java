package com.sucy.game.mysql.table;

import com.sucy.game.mysql.Column;
import com.sucy.game.mysql.ColumnType;
import com.sucy.game.mysql.ServerResult;
import com.sucy.game.mysql.MySQL;
import org.bukkit.plugin.Plugin;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Table for active games on the server
 */
public class ServerTable extends Table {

    private static final String
            TABLE = "Servers",
            NAME = "Name",
            MAP = "Map",
            IP = "IP",
            PLAYERS = "Players",
            MAX = "Max",
            STATE = "State";

    private String server, ip;

    /**
     * Constructor
     *
     * @param plugin plugin reference
     * @param sql    sql reference
     * @param name   server name
     * @param ip     server IP address
     */
    public ServerTable(Plugin plugin, MySQL sql, String name, String ip) {
        super(plugin, sql, TABLE,
                new Column(NAME, ColumnType.STRING),
                new Column(IP, ColumnType.STRING, 64),
                new Column(MAP, ColumnType.STRING),
                new Column(PLAYERS, ColumnType.INT),
                new Column(MAX, ColumnType.INT),
                new Column(STATE, ColumnType.INT));
        this.server = name;
        this.ip = ip;
    }

    /**
     * @return names of all active servers
     */
    public List<String> getServers() {
        List<String> games = new ArrayList<String>();

        // Get the active games
        try {
            ResultSet result = sql.queryAll(TABLE);
            while (result.next()) {
                games.add(result.getString(NAME));
            }
            result.close();
        }

        // An error occurred
        catch (Exception ex) {
            plugin.getLogger().severe("Failed to retrieve available servers: " + ex.getMessage());
        }

        return games;
    }

    /**
     * Retrieves the data for this server
     *
     * @return server game data
     */
    public ServerResult getData() {
        return getData(server);
    }

    /**
     * Retrieves data for the server
     *
     * @param server server to retrieve the data for
     * @return       game data
     */
    public ServerResult getData(String server) {

        // Get the game
        try {
            ResultSet result = sql.query(TABLE, server);
            result.next();
            ServerResult gameResult = new ServerResult(result.getString(NAME), result.getString(IP), result.getString(MAP), result.getInt(PLAYERS), result.getInt(MAX), result.getInt(STATE));
            result.close();
            return gameResult;
        }

        // A problem occurred
        catch (Exception ex) {
            plugin.getLogger().severe("Failed to retrieve server info: " + ex.getMessage());
            return null;
        }
    }

    /**
     * Retrieves the list of registered servers
     *
     * @return server list
     */
    public List<ServerResult> getServerList() {

        // Get the game
        List<ServerResult> list = new ArrayList<ServerResult>();
        try {
            ResultSet result = sql.queryAll(TABLE);
            while (result.next()) {
                list.add(new ServerResult(result.getString(NAME), result.getString(IP), result.getString(MAP), result.getInt(PLAYERS), result.getInt(MAX), result.getInt(STATE)));
            }
            result.close();
        }

        // A problem occurred
        catch (Exception ex) {
            plugin.getLogger().severe("Failed to retrieve server info: " + ex.getMessage());
            return null;
        }

        return list;
    }

    /**
     * Enables a server by adding it to the MySQL database
     *
     * @param maxPlayers max players allowed on the server
     */
    public void enable(int maxPlayers) {

        // Register the game
        sql.updateSQL("DELETE FROM " + TABLE + " WHERE " + NAME + "='" + server + "'");
        sql.updateSQL("INSERT INTO " + TABLE + " VALUES ('" + server + "','" + ip + "','None',0," + maxPlayers + ",4)");
        plugin.getLogger().info("Registered the server with the MySQL database");
    }

    /**
     * Updates the number of players for the server
     *
     * @param players new player count
     */
    public void updatePlayers(int players) {
        sql.updateSQL("UPDATE " + TABLE + " SET " + PLAYERS + "=" + players + " WHERE " + NAME + "='" + server + "'");
    }

    /**
     * Sets the state of the server for the MySQL database
     *
     * @param state server state
     */
    public void setState(int state) {
        sql.updateSQL("UPDATE " + TABLE + " SET " + STATE + "=" + state + " WHERE " + NAME + "='" + server + "'");
    }

    /**
     * Sets the map of the server for the MySQL database
     *
     * @param map map to set
     */
    public void setMap(String map) {
        sql.updateSQL("UPDATE " + TABLE + " SET " + MAP + "=" + map + " WHERE " + NAME + "='" + server + "'");
    }

    /**
     * Disables a server by removing it from the MySQL database
     */
    public void disable() {

        // Disable the game
        sql.updateSQL("DELETE FROM " + TABLE + " WHERE " + NAME + "='" + server + "'");
        plugin.getLogger().info("Unregistered the server from the MySQL database");
    }
}
