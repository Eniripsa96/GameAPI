package com.sucy.game.commands.game;

import com.sucy.game.GameAPI;
import com.sucy.game.PermissionNode;
import com.sucy.game.commands.CommandHandler;
import com.sucy.game.commands.ICommand;
import com.sucy.game.commands.SenderType;
import com.sucy.game.data.Team;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Command for leaving a team
 */
public class CmdLeave implements ICommand {

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

        // Get the team
        Team team = api.getTeam(sender.getName());

        // Invalid team
        if (team == null) {
            sender.sendMessage(ChatColor.DARK_RED + "You aren't on a team");
        }

        // Leave the team
        else {
            team.removePlayer(player);
            api.clearTeam(sender.getName());
            player.getScoreboard().getTeam(team.getName()).removePlayer(player);
            player.getScoreboard().getTeam("Spectator").addPlayer(player);
            player.setCanPickupItems(false);
            player.setGameMode(GameMode.CREATIVE);
            sender.sendMessage(ChatColor.DARK_GREEN + "You have left your team");
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
        return "leaves your team";
    }

    /**
     * @return type of sender required for this command
     */
    @Override
    public SenderType getSenderType() {
        return SenderType.PLAYER_ONLY;
    }
}
