package com.sucy.game.commands.game;

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
public class CmdRotation implements ICommand {

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

        // Display map list
        sender.sendMessage(TextSizer.createLine(ChatColor.GOLD + " Map Rotation ", "-", ChatColor.DARK_GRAY));
        for (Game game : api.getGames()) {
            ChatColor color = ChatColor.GRAY;
            if (game == api.getActiveGame()) color = ChatColor.GREEN;
            else if (game == api.getNextGame()) color = ChatColor.AQUA;
            sender.sendMessage(color + game.getMapName() + " (" + game.getGameName() + ")");
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
        return "Views map rotation";
    }

    /**
     * @return type of sender required for this command
     */
    @Override
    public SenderType getSenderType() {
        return SenderType.ANYONE;
    }
}
