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
 * Command for ending a running game
 */
public class CmdEnd implements ICommand {

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

        // No active game
        if (api.getState() != GameState.DURING_GAME) {
            sender.sendMessage(ChatColor.DARK_RED + "There is not a game currently in progress");
            return;
        }

        // No arguments
        Game game = api.getActiveGame();
        if (args.length == 0) {
            game.endGame();
            return;
        }

        // Get the team
        Team team = game.getTeam(args[0]);

        // Invalid team
        if (team == null) {
            sender.sendMessage(ChatColor.DARK_RED + "No team exists with that name");
        }

        // End the game
        else game.endGame(team);
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
        return "Ends the game";
    }

    /**
     * @return type of sender required for this command
     */
    @Override
    public SenderType getSenderType() {
        return SenderType.PLAYER_ONLY;
    }
}
