package com.sucy.game.commands;

import com.sucy.game.text.TextSizer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages organizing commands into sub-commands
 */
public abstract class CommandHandler implements CommandExecutor {

    protected static final String BREAK = ChatColor.STRIKETHROUGH + "" + ChatColor.DARK_GRAY + "-----------------------------------------------------";

    /**
     * Table of registered sub-commands
     */
    protected final Map<String, ICommand> commands = new HashMap<String, ICommand>();

    /**
     * Plugin reference
     */
    protected final Plugin plugin;

    /**
     * Usage title
     */
    protected String title;

    /**
     * Command label
     */
    protected final String label;

    /**
     * Constructor
     *
     * @param plugin  plugin reference
     * @param title   usage title
     * @param command command label
     */
    public CommandHandler(Plugin plugin, String title, String command) {
        this.plugin = plugin;
        this.title = title;
        this.label = command;
        registerCommands();

        PluginCommand cmd = ((JavaPlugin)plugin).getCommand(command);
        if (cmd != null) {
            cmd.setExecutor(this);
        }
    }

    /**
     * Constructor
     *
     * @param plugin  plugin reference
     * @param command command label and usage title
     */
    public CommandHandler(Plugin plugin, String command) {
        this(plugin, command, command);
    }

    /**
     * @return plugin reference
     */
    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * @return command label
     */
    public String getLabel() {
        return label.toLowerCase();
    }

    /**
     * Registers a new sub-command
     *
     * @param command  command prefix
     * @param executor handler for the command
     */
    protected void registerCommand(String command, ICommand executor) {
        commands.put(command, executor);
    }

    /**
     * Called on a command
     *
     * @param sender sender of the command
     * @param cmd    command executed
     * @param label  command label
     * @param args   command arguments
     * @return       true
     */
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        // No arguments simply shows the command usage
        if (args.length == 0) displayUsage(sender);

            // If a sub-command is found, execute it
        else if (commands.containsKey(args[0].toLowerCase())) {
            ICommand command = commands.get(args[0].toLowerCase());
            if (sender.hasPermission(command.getPermissionNode()))
                command.execute(this, plugin, sender, trimArgs(args));
            else
                sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to do that");
        }
        else {

            // Try to get a page number from the args
            try {
                int page = Integer.parseInt(args[0]);
                displayUsage(sender, page);
            }

            // If it wasn't a number, just display the first page
            catch (Exception e) {
                displayUsage(sender);
            }
        }

        // Use custom command usage
        return true;
    }

    /**
     * Trims the first element off of args
     *
     * @param args initial args
     * @return     trimmed args
     */
    protected String[] trimArgs(String[] args) {

        // Can't trim a zero-length array
        if (args.length == 0) return args;

        // Make a new array that is one smaller in size
        String[] trimmed = new String[args.length - 1];

        // Copy the array over if there are elements left
        if (trimmed.length > 0)
            System.arraycopy(args, 1, trimmed, 0, trimmed.length);

        // Return the new array
        return trimmed;
    }

    /**
     * Displays the command usage
     * - If you want custom displays, override the method with the page argument -
     *
     * @param sender sender of the command
     */
    public void displayUsage(CommandSender sender) {
        displayUsage(sender, 1);
    }

    /**
     * Displays the command usage
     * Can be overridden for custom displays
     *
     * @param sender sender of the command
     * @param page   page number
     */
    public void displayUsage (CommandSender sender, int page) {
        if (page < 1) page = 1;

        // Get the key set alphabetized
        ArrayList<String> keys = new ArrayList<String>(commands.keySet());
        Collections.sort(keys);

        // Limit the page number
        int validKeys = 0;
        for (String key : keys)
            if (canUseCommand(sender, commands.get(key)))
                validKeys++;

        if (validKeys == 0) {
            sender.sendMessage(ChatColor.GRAY + "   No commands available");
            return;
        }

        int maxPage = (validKeys + 6) / 7;
        if (page > maxPage)
            page = maxPage;

        sender.sendMessage(BREAK);
        sender.sendMessage(ChatColor.DARK_GREEN + title + " - Command Usage" + (maxPage > 1 ? " (Page " + page + "/" + maxPage + ")" : ""));

        // Get the maximum length
        int maxSize = 0;
        int index = 0;
        for (String key : keys) {
            if (!canUseCommand(sender, commands.get(key)))
                continue;
            index++;
            if (index <= (page - 1) * 7 || index > page * 7) continue;
            int size = TextSizer.measureString(key + " " + commands.get(key).getArgsString());
            if (size > maxSize) maxSize = size;
        }
        maxSize += 4;

        // Display usage, squaring everything up nicely
        index = 0;
        for (String key : keys) {
            if (!canUseCommand(sender, commands.get(key)))
                continue;
            index++;
            if (index <= (page - 1) * 7 || index > page * 7) continue;
            sender.sendMessage(ChatColor.GOLD + "/" + label.toLowerCase() + " " + TextSizer.expand(key + " "
                    + ChatColor.LIGHT_PURPLE + commands.get(key).getArgsString() + ChatColor.GRAY, maxSize, false)
                    + ChatColor.GRAY + "- " + commands.get(key).getDescription());
        }

        sender.sendMessage(BREAK);
    }

    /**
     * Checks whether or not a command sender can use a certain command
     * - Can be overridden for custom checks -
     *
     * @param sender  sender of the command
     * @param command command to check
     * @return        true if able to use it, false otherwise
     */
    protected boolean canUseCommand(CommandSender sender, ICommand command) {
        if (command.getSenderType() == SenderType.CONSOLE_ONLY && sender instanceof Player) return false;
        if (command.getSenderType() == SenderType.PLAYER_ONLY && !(sender instanceof Player)) return false;
        return sender.hasPermission(command.getPermissionNode());
    }

    /**
     * Registers all sub-commands
     */
    protected abstract void registerCommands();
}
