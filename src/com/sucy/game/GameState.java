package com.sucy.game;

/**
 * Game state for the server
 */
public enum GameState {

    /**
     * The server is waiting for more players to join
     */
    WAITING (0),

    /**
     * Players are in the map for the next game and it is about to start
     */
    PRE_GAME (1),

    /**
     * The game is in progress
     */
    DURING_GAME (2),

    /**
     * The game has ended and players are waiting to move to the next map
     */
    POST_GAME (3),

    /**
     * The server is disabled and games are not running
     */
    DISABLED (4),

    /**
     * The server is currently paused due to the cancel command
     */
    PAUSED (5);

    private final int id;

    /**
     * Constructor
     *
     * @param id state ID
     */
    private GameState(int id) {
        this.id = id;
    }

    /**
     * @return state ID
     */
    public int getId() {
        return id;
    }

    /**
     * Retrieves the game state by ID
     *
     * @param id state ID
     * @return   game state
     */
    public static GameState getState(int id) {
        for (GameState state : values()) {
            if (state.id == id) return state;
        }
        return DISABLED;
    }
}
