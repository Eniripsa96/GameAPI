package com.sucy.game.commands.setup;

import com.sucy.game.GameAPI;
import com.sucy.game.commands.*;
import com.sucy.game.commands.setup.CmdSetup;
import com.sucy.game.commands.setup.CmdTypes;

/**
 * Command handler for the plugin
 */
public class SetupCommander extends CommandHandler {

    /**
     * Constructor
     *
     * @param plugin plugin reference
     */
    public SetupCommander(GameAPI plugin) {
        super(plugin, "iPvP Game Manager", "game");
    }

    /**
     * Sets up the commands
     */
    @Override
    protected void registerCommands() {
        //registerCommand("lockdown", new CmdLockdown());
        //registerCommand("lockdownall", new CmdLockdownAll());
        registerCommand("map", new CmdMap());
        registerCommand("maps", new CmdMaps());
        registerCommand("servers", new CmdServers());
        registerCommand("setup", new CmdSetup());
        registerCommand("spawn", new CmdSpawn());
        registerCommand("stats", new CmdStats());
        registerCommand("types", new CmdTypes());
    }
}
