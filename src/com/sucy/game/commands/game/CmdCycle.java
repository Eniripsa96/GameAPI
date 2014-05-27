package com.sucy.game.commands.game;

import com.sucy.game.GameAPI;
import com.sucy.game.GameState;
import com.sucy.game.PermissionNode;
import com.sucy.game.commands.CommandHandler;
import com.sucy.game.commands.ICommand;
import com.sucy.game.commands.SenderType;
import com.sucy.game.data.Game;
import com.sucy.game.data.Team;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Command for setting a map to cycle to
 */
public class CmdCycle implements ICommand {

    /**
     * Executes the command
     *
     * @param handler command handler
     * @param plugin  plugin reference
     * @param sender  sender of the command
     * @param args    command arguments
     */
    @Override
    public void execute(CommandHandler handler, Plugin plugin, CommandSender sender, String[] args) {

        GameAPI api = (GameAPI)plugin;

        // No arguments
        if (args.length == 0) {
            handler.displayUsage(sender);
            return;
        }

        // Get the map
        Game game = api.getGame(args[0]);

        // Invalid map
        if (game == null) {
            sender.sendMessage(ChatColor.DARK_RED + "No map exists with that name");
        }

        // Set it as the next map
        else {
            api.setNextGame(game);
            sender.sendMessage(ChatColor.GOLD + game.getMapName() + ChatColor.DARK_GREEN + " is now the next map in the cycle");
        }
    }

    /**
     * @return Permission required by the command
     */
    @Override
    public String getPermissionNode() {
        return PermissionNode.ADMIN;
    }

    /**
     * @return arguments used by the command
     */
    @Override
    public String getArgsString() {
        return "[team]";
    }

    /**
     * @return description of the command
     */
    @Override
    public String getDescription() {
        return "Sets the next cycle map";
    }

    /**
     * @return type of sender required for this command
     */
    @Override
    public SenderType getSenderType() {
        return SenderType.ANYONE;
    }
}
