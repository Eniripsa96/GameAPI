package com.sucy.game.commands.setup;

import com.sucy.game.GameAPI;
import com.sucy.game.GamePlugin;
import com.sucy.game.PermissionNode;
import com.sucy.game.commands.CommandHandler;
import com.sucy.game.commands.ICommand;
import com.sucy.game.commands.SenderType;
import com.sucy.game.data.Game;
import com.sucy.game.data.Team;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Command for setting a spawn point
 */
public class CmdSpawn implements ICommand {

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

        // Requires at least 1 argument
        if (args.length == 0) {
            handler.displayUsage(sender);
            return;
        }

        // Get the team
        Game game = api.getGame(player.getWorld());

        // Make sure there is a game in that world
        if (api.getGame(player.getWorld()) == null) {
            sender.sendMessage(ChatColor.DARK_RED + "No game is set up in this world");
            return;
        }

        Location spawn = player.getLocation();

        // Spectator
        if (args[0].equalsIgnoreCase("spectator")) {
            player.getWorld().setSpawnLocation(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ());
        }

        // Declared teams
        else {

            Team team = game.getTeam(args[0]);

            // Invalid team
            if (team == null) {
                player.sendMessage(ChatColor.DARK_RED + "That is not a valid team name");
                return;
            }

            team.setSpawn(spawn);
        }

        sender.sendMessage(ChatColor.DARK_GREEN + "The spawn point has been set");

        // Set up the game

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
        return "[team]";
    }

    /**
     * @return description of the command
     */
    @Override
    public String getDescription() {
        return "Sets a team spawn";
    }

    /**
     * @return type of sender required for this command
     */
    @Override
    public SenderType getSenderType() {
        return SenderType.PLAYER_ONLY;
    }
}