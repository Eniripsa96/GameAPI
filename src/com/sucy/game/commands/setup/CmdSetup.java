package com.sucy.game.commands.setup;

import com.sucy.game.GameAPI;
import com.sucy.game.GamePlugin;
import com.sucy.game.PermissionNode;
import com.sucy.game.commands.CommandHandler;
import com.sucy.game.commands.ICommand;
import com.sucy.game.commands.SenderType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Command for setting up a game
 */
public class CmdSetup implements ICommand {

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
        Player player = (Player)sender;

        // Requires 1 argument
        if (args.length == 0) {
            handler.displayUsage(sender);
            return;
        }

        // Get the game type
        GamePlugin type = api.getType(args[0]);
        if (type == null) {
            sender.sendMessage(ChatColor.DARK_RED + "That is not an available game type");
            return;
        }

        // Make sure there isn't already a game in that world
        if (api.getGame(player.getWorld()) != null) {
            sender.sendMessage(ChatColor.DARK_RED + "The world already has a game set up for it");
            return;
        }

        // Set up the game
        api.setupGame(player.getWorld(), type);
        sender.sendMessage(ChatColor.DARK_GREEN + "The game has been set up and a config was generated");
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
        return "";
    }

    /**
     * @return description of the command
     */
    @Override
    public String getDescription() {
        return "Sets up a game";
    }

    /**
     * @return type of sender required for this command
     */
    @Override
    public SenderType getSenderType() {
        return SenderType.PLAYER_ONLY;
    }
}