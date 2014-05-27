package com.sucy.game;

import com.sucy.game.config.Config;

/**
 * Interface for a plugin providing a game
 */
public interface GamePlugin {

    /**
     * <p>Retrieves the default config for the game mode</p>
     * <p>This is copied to all newly set up maps</p>
     *
     * @return default config for the game mode
     */
    public Config getDefaultConfig();

    /**
     * <p>Retrieves the game mode key</p>
     * <p>This is used for selecting what game mode to set up</p>
     *
     * @return game mode key
     */
    public String getKey();

    /**
     * <p>Retrieves the description for the game mode</p>
     * <p>This is displayed with the available game modes
     * when setting up a map</p>
     *
     * @return game description
     */
    public String getLabel();

    /**
     * <p>Enables the game mode when the active game switches
     * to a world using the game mode</p>
     */
    public void enable();

    /**
     * <p>Disables the game mode when the active game switches
     * from a world using the game mode</p>
     */
    public void disable();
}
