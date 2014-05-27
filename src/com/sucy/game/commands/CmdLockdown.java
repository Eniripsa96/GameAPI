package com.sucy.game.commands;

import com.sucy.game.PermissionNode;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

public class CmdLockdown implements ICommand {

    @Override
    public void execute(CommandHandler handler, Plugin plugin, org.bukkit.command.CommandSender sender, String[] args) {
        ServerInfo server = ProxyServer.getInstance().getServerInfo(ProxyServer.getInstance().getName());
        if (args.length > 0) {
            String name = args[0];
            for (int i = 1; i < args.length; i++) {
                name += " " + args[i];
            }
            ServerInfo info = ProxyServer.getInstance().getServerInfo(name);
            if (info == null) {
                sender.sendMessage(ChatColor.DARK_RED + "That is not a valid server name");
                return;
            }
            server = info;
        }
        server.sendData("ipvp", "lockdown".getBytes());
        sender.sendMessage(ChatColor.DARK_GREEN + "The lockdown signal has been sent to " + server.getName());
    }

    @Override
    public String getPermissionNode() {
        return PermissionNode.ADMIN;
    }

    @Override
    public String getArgsString() {
        return "[server]";
    }

    @Override
    public String getDescription() {
        return "Locks down a server";
    }

    @Override
    public SenderType getSenderType() {
        return SenderType.ANYONE;
    }
}
