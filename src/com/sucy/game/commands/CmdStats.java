package com.sucy.game.commands;

import com.sucy.game.GameAPI;
import com.sucy.game.PermissionNode;
import com.sucy.game.commands.CommandHandler;
import com.sucy.game.commands.ICommand;
import com.sucy.game.commands.SenderType;
import com.sucy.game.data.PlayerStats;
import com.sucy.game.text.TextSizer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.text.DecimalFormat;

/**
 * Command for joining a team
 */
public class CmdStats implements ICommand {

    private static final DecimalFormat format = new DecimalFormat("######0.00");

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

        // Get player stats
        String name;
        PlayerStats stats;
        PlayerStats totals;
        if (args.length == 0) {
            stats = api.getStats(sender.getName());
            totals = api.getTotalStats(sender.getName());
            name = sender.getName();
        }
        else {
            if (!api.getServer().getOfflinePlayer(args[0]).hasPlayedBefore()) {
                sender.sendMessage(ChatColor.DARK_RED + "That player could not be found");
                return;
            }
            else {
                stats = api.getStats(args[0]);
                totals = api.getTotalStats(args[0]);
                name = plugin.getServer().getOfflinePlayer(args[0]).getName();
            }
        }

        // Display the stats
        sender.sendMessage(TextSizer.createLine(ChatColor.GOLD + " " + name + "'s Stats ", "-", ChatColor.DARK_GRAY));
        sender.sendMessage(ChatColor.DARK_GREEN + "Kills: " + ChatColor.GOLD + stats.getKills() + ChatColor.GRAY + " (" + ChatColor.GOLD + totals.getKills() + ChatColor.GRAY + ")");
        sender.sendMessage(ChatColor.DARK_GREEN + "Deaths: " + ChatColor.GOLD + stats.getDeaths() + ChatColor.GRAY + " (" + ChatColor.GOLD + totals.getDeaths() + ChatColor.GRAY + ")");
        sender.sendMessage(ChatColor.DARK_GREEN + "KDR: " + ChatColor.GOLD + format.format(stats.getKDR()) + ChatColor.GRAY + " (" + ChatColor.GOLD + format.format(totals.getKDR()) + ChatColor.GRAY + ")");
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
        return "[player]";
    }

    /**
     * @return description of the command
     */
    @Override
    public String getDescription() {
        return "Views KDR stats";
    }

    /**
     * @return type of sender required for this command
     */
    @Override
    public SenderType getSenderType() {
        return SenderType.PLAYER_ONLY;
    }
}
