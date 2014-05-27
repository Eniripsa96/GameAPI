package com.sucy.game.commands;

import com.sucy.game.PermissionNode;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

public class CmdLockdownAll implements ICommand {

    @Override
    public void execute(CommandHandler handler, Plugin plugin, org.bukkit.command.CommandSender sender, String[] args) {
        for (ServerInfo info : BungeeCord.getInstance().getServers().values()) {
            info.sendData("ipvp", "lockdown".getBytes());
        }
        sender.sendMessage(ChatColor.DARK_GREEN + "The lockdown signal has been sent to all game servers");
    }

    @Override
    public String getPermissionNode() {
        return PermissionNode.ADMIN;
    }

    @Override
    public String getArgsString() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Locks down all servers";
    }

    @Override
    public SenderType getSenderType() {
        return SenderType.ANYONE;
    }
}
