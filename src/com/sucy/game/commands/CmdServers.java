package com.sucy.game.commands;

import com.sucy.game.GameAPI;
import com.sucy.game.PermissionNode;
import com.sucy.game.commands.CommandHandler;
import com.sucy.game.commands.ICommand;
import com.sucy.game.commands.SenderType;
import com.sucy.game.data.Game;
import com.sucy.game.data.PlayerStats;
import com.sucy.game.mysql.ServerResult;
import com.sucy.game.text.TextFormatter;
import com.sucy.game.text.TextSizer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Command for displaying the server list
 */
public class CmdServers implements ICommand {

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
        List<ServerResult> servers = api.getServerTable().getServerList();
        if (servers.size() == 0) {
            sender.sendMessage(ChatColor.DARK_RED + "No servers are currently online");
            return;
        }

        // Display server list
        sender.sendMessage(TextSizer.createLine(ChatColor.GOLD + " Online Servers ", "-", ChatColor.DARK_GRAY));
        for (ServerResult server : servers) {
            sender.sendMessage(ChatColor.GRAY + "[" + server.getPlayers() + "/" + server.getMaxPlayers() + "] "
                    + ChatColor.DARK_GREEN + server.getName()
                    + ChatColor.GRAY + " (" + ChatColor.AQUA + TextFormatter.format(server.getState().name()) + ChatColor.GRAY + ")");
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
        return "Views online servers";
    }

    /**
     * @return type of sender required for this command
     */
    @Override
    public SenderType getSenderType() {
        return SenderType.ANYONE;
    }
}
