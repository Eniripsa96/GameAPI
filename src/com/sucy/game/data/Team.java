package com.sucy.game.data;

import com.sucy.game.config.Config;
import com.sucy.game.config.LocationData;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * A team in the game
 */
public final class Team {

    private Game game;
    private String name;
    private Location spawn;
    private ChatColor chatColor;
    private int maxPlayers;
    private int score;
    private CombatMode mode;

    private final HashSet<String> players = new HashSet<String>();
    private final List<ItemStack> items = new ArrayList<ItemStack>();

    /**
     * Constructor
     *
     * @param name       team name
     * @param spawn      spawn location
     * @param chatColor  team chat color
     * @param maxPlayers maximum players allowed on the team at once
     */
    public Team(Game game, String name, Location spawn, CombatMode mode, ChatColor chatColor, int maxPlayers) {
        this.name = name;
        this.spawn = spawn;
        this.mode = mode;
        this.chatColor = chatColor;
        this.maxPlayers = maxPlayers;
    }

    /**
     * @return true if can pvp, false otherwise
     */
    public CombatMode getCombatMode() {
        return mode;
    }

    /**
     * @return whether or not more players can join this team
     */
    public boolean canJoin() {
        return players.size() < maxPlayers;
    }

    /**
     * @return team name
     */
    public String getName() {
        return name;
    }

    /**
     * @return team name with it's chat color
     */
    public String getColoredName() {
        return chatColor + name;
    }

    public String getPrefix() {
        return chatColor + "";
    }

    /**
     * @return max players allowed on the team
     */
    public int getMaxPlayers() {
        return maxPlayers;
    }

    /**
     * @return number of players on the team
     */
    public int getPlayerCount() {
        return players.size();
    }

    /**
     * Checks if the player is on the team
     *
     * @param player player to check
     * @return       true if on team, false otherwise
     */
    public boolean isPlayerOnTeam(Player player) {
        return players.contains(player.getName());
    }

    /**
     * @return the current score of the team
     */
    public int getScore() {
        return score;
    }

    /**
     * Adds to the team's score
     *
     * @param amount amount to add
     */
    public void addScore(int amount) {
        score += amount;
        if (game.getTargetScore() <= score) {
            game.endGame(this);
        }
    }

    /**
     * Resets the team
     */
    public void reset() {
        players.clear();
        score = 0;
    }

    /**
     * Attempts to add a player to the team
     *
     * @param player player to add
     * @return       true if added, false if team was full
     */
    public boolean addPlayer(Player player) {
        if (canJoin()) {
            players.add(player.getName());
            player.getInventory().clear();
            player.getInventory().setArmorContents(new ItemStack[4]);
            for (ItemStack item : items) {
                player.getInventory().addItem(item);
            }
            player.teleport(spawn);
            player.updateInventory();
            return true;
        }
        else return false;
    }

    /**
     * Removes a player from the team
     *
     * @param player player to remove
     */
    public void removePlayer(Player player) {
        if (players.contains(player.getName())) {
            players.remove(player.getName());
            player.getInventory().clear();
            player.teleport(player.getWorld().getSpawnLocation());
        }
    }

    /**
     * Sends a message from the player to all members of the team
     *
     * @param player  player who sent the message
     * @param message message to send
     * @return        true if message was sent, false if player was not on the team
     */
    public boolean sendMessage(Player player, String message) {
        if (isPlayerOnTeam(player)) {
            String formatted = "<" + chatColor + "[Team] " + player.getName() + ChatColor.WHITE + "> " + message;
            for (String memberName : players) {
                Player member = player.getServer().getPlayer(memberName);
                if (member != null) {
                    member.sendMessage(formatted);
                }
            }
            return true;
        }
        else return false;
    }

    /**
     * Displays the team list to the target player
     *
     * @param player player to show the team list
     */
    public void displayTeamList(Player player) {
        String message = chatColor + name + ChatColor.GRAY + ": ";
        if (players.size() == 0) message += "None";
        else {
            for (String playerName : players) {
                message += ChatColor.GOLD + playerName + ChatColor.GRAY + ", ";
            }
            message = message.substring(0, message.length() - 3);
        }

        player.sendMessage(message);
    }

    /**
     * Sets the spawn point for the team
     *
     * @param spawn new spawn point
     */
    public void setSpawn(Location spawn) {
        this.spawn = spawn;
        Config config = game.getConfig();
        ConfigurationSection teamConfig = getConfig(config);
        teamConfig.set("spawn", LocationData.serializeSimpleLocation(spawn));
        config.saveConfig();
    }

    /**
     * Retrieves the team's configuration section from the game's config
     *
     * @param config Owning game's config manager
     * @return       Configuration section for the team data
     */
    public ConfigurationSection getConfig(Config config) {
        return game.getConfig().getConfig().getConfigurationSection("teams." + name);
    }

    /**
     * Loads a team from the configuration
     *
     * @param config config file to load from
     * @return       team loaded
     */
    public static Team load(Game game, ConfigurationSection config) {
        String n = config.getString("name");
        World world = game.getWorld();
        Location s = LocationData.parseLocation(world, config.getString("spawn"));
        ChatColor c = ChatColor.valueOf(config.getString("chatColor").toUpperCase());
        CombatMode cm = CombatMode.valueOf(config.getString("combatMode").toUpperCase());
        int m = config.getInt("maxPlayers");

        Team team = new Team(game, n, s, cm, c, m);

        if (!config.contains("items")) return team;

        ConfigurationSection itemSection = config.getConfigurationSection("items");
        for (String key : itemSection.getKeys(false)) {
            ConfigurationSection section = itemSection.getConfigurationSection(key);
            ItemStack item = new ItemStack(Material.getMaterial(section.getString("type").toUpperCase()), section.getInt("amount"));

            if (section.contains("name")) {
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(section.getString("name"));
                item.setItemMeta(meta);
            }

            if (section.contains("lore")) {
                ItemMeta meta = item.getItemMeta();
                meta.setLore(section.getStringList("lore"));
                item.setItemMeta(meta);
            }

            if (section.contains("enchants")) {
                ConfigurationSection enchantSection = section.getConfigurationSection("enchants");
                for (String enchant : enchantSection.getKeys(false)) {
                    Enchantment enchantment = Enchantment.getByName(enchant.toUpperCase().replace(" ", "_"));
                    if (enchantment != null) {
                        item.addEnchantment(enchantment, enchantSection.getInt(enchant));
                    }
                    else game.getAPI().getLogger().info("Failed to load enchantment for team item: " + enchant);
                }
            }

            team.items.add(item);
        }

        return team;
    }
}
