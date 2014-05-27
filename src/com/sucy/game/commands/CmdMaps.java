package com.sucy.game.commands;

import com.sucy.game.GameAPI;
import com.sucy.game.PermissionNode;
import com.sucy.game.commands.CommandHandler;
import com.sucy.game.commands.ICommand;
import com.sucy.game.commands.SenderType;
import com.sucy.game.data.Game;
import com.sucy.game.data.PlayerStats;
import com.sucy.game.text.TextSizer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.text.DecimalFormat;

/**
 * Command for displaying the map list
 */
public class CmdMaps implements ICommand {

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

        // No games exist
        if (api.getGames().size() == 0) {
            sender.sendMessage(ChatColor.DARK_RED + "No maps currently exist");
            return;
        }

        // Display map list
        sender.sendMessage(TextSizer.createLine(ChatColor.GOLD + " Loaded Maps ", "-", ChatColor.DARK_GRAY));
        for (Game game : api.getGames()) {
            sender.sendMessage(ChatColor.DARK_GREEN + game.getMapName() + ChatColor.GRAY + " (" + ChatColor.AQUA + game.getGameName() + ChatColor.GRAY + ")");
        }
    }

    /**
     * @return Permission required by the command
     */
    @Override
    public String getPermissionNode() {
        return PermissionNode.PLAYER;
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
        return "Views registered maps";
    }

    /**
     * @return type of sender required for this command
     */
    @Override
    public SenderType getSenderType() {
        return SenderType.ANYONE;
    }
}
