package com.sucy.game.commands.game;

import com.sucy.game.GameAPI;
import com.sucy.game.GameState;
import com.sucy.game.PermissionNode;
import com.sucy.game.commands.CommandHandler;
import com.sucy.game.commands.ICommand;
import com.sucy.game.commands.SenderType;
import com.sucy.game.data.Game;
import com.sucy.game.data.Team;
import com.sucy.game.text.TextSizer;
import com.sucy.game.time.Timer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;

/**
 * Command for viewing match details
 */
public class CmdMatch implements ICommand {

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

        // Not during a game
        if (api.getState() != GameState.DURING_GAME) {
            sender.sendMessage(ChatColor.DARK_RED + "The game isn't in progress");
            return;
        }

        // Get the game
        Game game = api.getActiveGame();

        Collection<Team> teams = game.getTeams();
        Team[] array = teams.toArray(new Team[teams.size()]);
        Timer timer = game.getTimer();
        int limit = timer.getTimeLimit() * timer.getTimeUnit().getNumberOfTicks() / 20;
        int left = timer.getTimeLeft();
        int current = limit - left;

        sender.sendMessage(TextSizer.createLine(ChatColor.AQUA + " " + game.getMapName() + " ", "-", ChatColor.DARK_GRAY));
        sender.sendMessage(ChatColor.DARK_GREEN + "Game Time: " + ChatColor.GOLD + current);
        sender.sendMessage(ChatColor.DARK_GREEN + "Time Left: " + ChatColor.GOLD + left);
        sender.sendMessage(ChatColor.DARK_GREEN + "Teams:");
        for (Team team : array) {
            sender.sendMessage("    " + team.getColoredName() + ": " + ChatColor.GOLD + team.getPlayerCount() + " players" + ChatColor.GRAY + ", " + ChatColor.GOLD + team.getScore() + " points");
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
        return "Views match details";
    }

    /**
     * @return type of sender required for this command
     */
    @Override
    public SenderType getSenderType() {
        return SenderType.PLAYER_ONLY;
    }
}