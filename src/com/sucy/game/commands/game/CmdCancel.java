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
public class CmdCancel implements ICommand {

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

        // Before timers even start up
        if (api.getState() == GameState.WAITING || api.getState() == GameState.PAUSED || api.getState() == GameState.DURING_GAME) {
            sender.sendMessage(ChatColor.DARK_RED + "There is no active countdown to cancel");
        }

        // Pre-game pausing
        else if (api.getState() == GameState.PRE_GAME) {
            api.pause();
            sender.sendMessage(ChatColor.DARK_GREEN + "The starting countdown was cancelled and the server is now paused");
        }

        // Post-game countdown
        else if (api.getState() == GameState.POST_GAME) {
            api.changeGames();
            sender.sendMessage(ChatColor.DARK_GREEN + "The transition countdown was cancelled and players have been moved");
        }
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
        return "Cancels a countdown";
    }

    /**
     * @return type of sender required for this command
     */
    @Override
    public SenderType getSenderType() {
        return SenderType.ANYONE;
    }
}
