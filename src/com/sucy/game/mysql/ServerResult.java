package com.sucy.game.mysql;

import com.sucy.game.GameState;

/**
 * MySQL result of querying a game
 */
public class ServerResult {

    private String name;
    private String ip;
    private String map;
    private int players;
    private int max;
    private GameState state;

    /**
     * Constructor
     *
     * @param name    server name
     * @param ip      server IP
     * @param map     current map
     * @param players player on the server
     * @param max     max players allowed
     * @param state   game state ID of the server
     */
    public ServerResult(String name, String ip, String map, int players, int max, int state) {
        this.name = name;
        this.ip = ip;
        this.map = map;
        this.players = players;
        this.max = max;
        this.state = GameState.getState(state);
    }

    /**
     * @return server hosting the game
     */
    public String getName() {
        return name;
    }

    /**
     * @return server IP
     */
    public String getIp() {
        return ip;
    }

    /**
     * @return active map
     */
    public String getMap() { return map; }

    /**
     * @return player count
     */
    public int getPlayers() {
        return players;
    }

    /**
     * @return max number of players
     */
    public int getMaxPlayers() {
        return max;
    }

    /**
     * @return state of the server
     */
    public GameState getState() {
        return state;
    }
}
