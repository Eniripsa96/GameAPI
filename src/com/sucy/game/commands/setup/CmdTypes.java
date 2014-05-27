package com.sucy.game.commands.setup;

import com.sucy.game.GameAPI;
import com.sucy.game.GamePlugin;
import com.sucy.game.PermissionNode;
import com.sucy.game.commands.CommandHandler;
import com.sucy.game.commands.ICommand;
import com.sucy.game.commands.SenderType;
import com.sucy.game.text.TextSizer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Collection;

/**
 * Command for joining a team
 */
public class CmdTypes implements ICommand {

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
        Collection<GamePlugin> types = api.getTypes();

        // No game types found
        if (types.size() == 0) {
            sender.sendMessage(ChatColor.DARK_RED + "There's no available game types");
            return;
        }

        sender.sendMessage(TextSizer.createLine(ChatColor.GOLD + " Game Types ", "-", ChatColor.DARK_GRAY));
        for (GamePlugin type : types) {
            sender.sendMessage("[" + ChatColor.GOLD + type.getKey().toUpperCase() + ChatColor.WHITE + "]" + ChatColor.GRAY + " - " + type.getLabel());
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
        return "Views game types";
    }

    /**
     * @return type of sender required for this command
     */
    @Override
    public SenderType getSenderType() {
        return SenderType.ANYONE;
    }
}