package com.sucy.game.commands.game;

import com.sucy.game.GameAPI;
import com.sucy.game.commands.*;
import com.sucy.game.commands.setup.CmdSetup;
import com.sucy.game.commands.setup.CmdTypes;

/**
 * Command handler for the plugin
 */
public class GameCommander extends CommandHandler {

    /**
     * Constructor
     *
     * @param plugin plugin reference
     */
    public GameCommander(GameAPI plugin) {
        super(plugin, "iPvP Game Manager", "game");
    }

    /**
     * Sets up the commands
     */
    @Override
    protected void registerCommands() {
        registerCommand("cycle", new CmdCycle());
        registerCommand("end", new CmdEnd());
        registerCommand("join", new CmdJoin());
        registerCommand("leave", new CmdLeave());
        //registerCommand("lockdown", new CmdLockdown());
        //registerCommand("lockdownall", new CmdLockdownAll());
        registerCommand("map", new CmdMap());
        registerCommand("mapnext", new CmdMapNext());
        registerCommand("maps", new CmdMaps());
        registerCommand("match", new CmdMatch());
        registerCommand("rot", new CmdRotation());
        registerCommand("servers", new CmdServers());
        registerCommand("stats", new CmdStats());
    }
}
