package com.sucy.game.commands;

import com.sucy.game.GameAPI;
import com.sucy.game.PermissionNode;
import com.sucy.game.commands.CommandHandler;
import com.sucy.game.commands.ICommand;
import com.sucy.game.commands.SenderType;
import com.sucy.game.data.Game;
import com.sucy.game.text.TextSizer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Command for joining a team
 */
public class CmdMap implements ICommand {

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
        Game game = api.getGame(((Player)sender).getWorld());

        // No game found
        if (game == null) {
            sender.sendMessage(ChatColor.DARK_RED + "There's no loaded map in your world");
            return;
        }

        sender.sendMessage(TextSizer.createLine(ChatColor.AQUA + " " + game.getMapName() + " ", "-", ChatColor.DARK_GRAY));
        sender.sendMessage(ChatColor.DARK_GREEN + "Game Name: " + ChatColor.GOLD + game.getGameName());
        sender.sendMessage(ChatColor.DARK_GREEN + "Objective: " + ChatColor.GOLD + game.getObjective());
        sender.sendMessage(ChatColor.DARK_GREEN + "Target Score: " + ChatColor.GOLD + game.getTargetScore());
        sender.sendMessage(ChatColor.DARK_GREEN + "Version: " + ChatColor.GOLD + game.getVersion());
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
        return "Views map details";
    }

    /**
     * @return type of sender required for this command
     */
    @Override
    public SenderType getSenderType() {
        return SenderType.PLAYER_ONLY;
    }
}