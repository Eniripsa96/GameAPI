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
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Command for joining a team
 */
public class CmdJoin implements ICommand {

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

        Player player = (Player)sender;
        GameAPI api = (GameAPI)plugin;

        // Make sure there's a game
        if (api.getState() == GameState.POST_GAME || api.getState() == GameState.DISABLED) {
            sender.sendMessage(ChatColor.DARK_RED + "You cannot join a team at this time");
            return;
        }

        // Already on a team
        if (api.getTeam(sender.getName()) != null) {
            sender.sendMessage(ChatColor.DARK_RED + "You are already on a team");
            return;
        }

        // Get the team
        Game game = api.getActiveGame();
        Team team = null;
        if (args.length >= 1) {
            game.getTeam(args[0]);
        }
        else {
            int min = Integer.MAX_VALUE;
            for (Team t : game.getTeams()) {
                if (t.canJoin() && t.getPlayerCount() < min) {
                    team = t;
                    min = t.getPlayerCount();
                }
            }
        }

        // Invalid team
        if (team == null) {
            sender.sendMessage(ChatColor.DARK_RED + "No team exists with that name");
        }

        // Team is full
        else if (!team.canJoin()) {
            sender.sendMessage(ChatColor.DARK_RED + "That team is currently full");
        }

        // Join the team
        else {
            team.addPlayer(player);
            player.setCanPickupItems(true);
            player.setGameMode(GameMode.SURVIVAL);
            player.getScoreboard().getTeam("Spectator").removePlayer(player);
            player.getScoreboard().getTeam(team.getName()).addPlayer(player);
            ((GameAPI)plugin).registerTeam(sender.getName(), team);
            sender.sendMessage(ChatColor.DARK_GREEN + "You have joined the " + team.getColoredName());
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
        return "[team]";
    }

    /**
     * @return description of the command
     */
    @Override
    public String getDescription() {
        return "joins a team";
    }

    /**
     * @return type of sender required for this command
     */
    @Override
    public SenderType getSenderType() {
        return SenderType.PLAYER_ONLY;
    }
}
