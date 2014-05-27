package com.sucy.game.data;

import com.sucy.game.GameAPI;
import com.sucy.game.config.Config;
import com.sucy.game.time.TimeUnit;
import com.sucy.game.time.Timer;
import com.sucy.game.time.TimerListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Game data for an arena
 */
public final class Game {

    public static final String TIMER_KEY = "g";
    public static final int[] DEFAULT_EVENT_TIMES = new int[]
            { 1, 2, 3, 4, 5, 10, 20, 30, 60, 120, 180, 240, 300, 600, 1200, 1800, 3600 };

    private final ArrayList<String> authors = new ArrayList<String>();
    private final ArrayList<Contributor> contributors = new ArrayList<Contributor>();
    private final HashMap<String, Team> teams = new HashMap<String, Team>();
    private final JavaPlugin plugin;

    private String gameName, mapName, version, objective, gameKey, world;
    private int targetScore;
    private int timeLimit;
    private Timer timer;

    private GameAPI api;

    /**
     * Constructor
     *
     * @param plugin    plugin reference
     * @param gameName  name of the game
     * @param mapName   name of the map
     * @param version   current version of the arena
     * @param objective description of the game objective
     * @param time      time limit for the game
     */
    public Game(JavaPlugin plugin, String gameName, String mapName, String version, String objective, int time, int targetScore) {
        this.gameName = gameName;
        this.mapName = mapName;
        this.version = version;
        this.objective = objective;
        this.targetScore = targetScore;
        this.timeLimit = time;
        this.plugin = plugin;

        this.api = (GameAPI) Bukkit.getPluginManager().getPlugin("GameAPI");
    }

    /**
     * @return game name
     */
    public String getGameName() {
        return gameName;
    }

    /**
     * @return map name
     */
    public String getMapName() {
        return mapName;
    }

    /**
     * @return game key
     */
    public String getGameKey() {
        return gameKey;
    }

    /**
     * @return game version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @return game objective
     */
    public String getObjective() {
        return objective;
    }

    /**
     * @return list of authors of the map
     */
    public List<String> getAuthors() {
        return authors;
    }

    /**
     * @return list of contributors
     */
    public List<Contributor> getContributors() {
        return contributors;
    }

    /**
     * @return the game timer
     */
    public Timer getTimer() {
        return timer;
    }

    /**
     * @return name of the world containing the game
     */
    public String getWorldName() {
        return world;
    }

    /**
     * @return target score for the game
     */
    public int getTargetScore() {
        return targetScore;
    }

    /**
     * Sets the key for the attached game mode
     *
     * @param key game key
     */
    public void setKey(String key) {
        this.gameKey = key;
        Config config = getConfig();
        config.getConfig().set("key", key);
        config.saveConfig();
    }

    /**
     * Resets the game to prepare for the next one
     */
    public void reset() {
        for (Team team : teams.values()) {
            team.reset();
        }
    }

    /**
     * Ends the game with the given team as the victor
     *
     * @param winner winning team
     */
    public void endGame(Team winner) {
        String winnerMessage = "";
        String loserMessage = "";
        if (winner != null) {
            winnerMessage = ChatColor.DARK_GREEN + "Your team has won! The game is now over.";
            loserMessage = ChatColor.GOLD + winner.getName() + ChatColor.DARK_RED + " has won! The game is now over.";
        }
        String tiedMessage = ChatColor.DARK_GREEN + "The game has ended in a draw!";
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (winner == null) {
                player.sendMessage(tiedMessage);
            }
            else if (winner.isPlayerOnTeam(player)) {
                player.sendMessage(winnerMessage);
            }
            else player.sendMessage(loserMessage);
        }
        timer.stop();
        timer = null;
        api.startTransition();
    }

    /**
     * Ends the game
     */
    public void endGame() {
        int mostPoints = -1;
        Team winner = null;

        // Get the winning team
        for (Team team : teams.values()) {

            // Having more points translates to a win
            if (team.getScore() > mostPoints) {
                winner = team;
                mostPoints = winner.getScore();
            }

            // Having the same points means it is a draw
            else if (team.getScore() == mostPoints) {
                winner = null;
            }
        }
        endGame(winner);
    }

    /**
     * Retrieves a team by name
     *
     * @param name team name
     * @return     team reference
     */
    public Team getTeam(String name) {
        return teams.get(name.toLowerCase());
    }

    /**
     * @return collection of all registered teams
     */
    public Collection<Team> getTeams() {
        return teams.values();
    }

    /**
     * @return world reference for the game
     */
    public World getWorld() {
        World world = api.getServer().getWorld(this.world);
        if (world != null) return world;
        return api.getServer().createWorld(new WorldCreator(this.world));
    }

    /**
     * Sets up the timer for the game with default event times
     *
     * @param listener listener for the time events
     * @return         game timer
     */
    public Timer setupTimer(TimerListener listener) {
        return setupTimer(listener, DEFAULT_EVENT_TIMES);
    }

    /**
     * Sets up the timer for the game
     *
     * @param listener   listener for the time events
     * @param eventTimes times in which to launch events
     * @return           game timer
     */
    public Timer setupTimer(TimerListener listener, int ... eventTimes) {
        timer = new Timer(plugin, listener, TIMER_KEY, timeLimit, TimeUnit.SECOND, eventTimes);
        return timer;
    }

    public Config getConfig() {
        return getConfig(plugin, world);
    }

    /**
     * @return API reference if registered, null otherwise
     */
    public GameAPI getAPI() {
        return api;
    }

    public static Config getConfig(JavaPlugin plugin, String world) {
        return new Config(plugin, "maps" + File.separator + world);
    }

    /**
     * Loads a game from the config section
     *
     * @param plugin   plugin reference
     * @return         game loaded
     */
    public static Game load(JavaPlugin plugin, String world) {
        ConfigurationSection config = getConfig(plugin, world).getConfig();
        String gn = config.getString("gameName");
        String mn = config.getString("mapName");
        String v = config.getString("version");
        String o = config.getString("objective");
        int t = config.getInt("timeLimit");
        int s = config.getInt("targetScore");

        Game game = new Game(plugin, gn, mn, v, o, t, s);
        game.world = world;
        game.authors.addAll(config.getStringList("authors"));
        game.gameKey = config.getString("key", "");
        ConfigurationSection contributors = config.getConfigurationSection("contributors");
        for (String key : contributors.getKeys(false)) {
            game.contributors.add(new Contributor(key, contributors.getString(key)));
        }
        for (String team : config.getConfigurationSection("teams").getKeys(false)) {
            game.teams.put(team, Team.load(game, config.getConfigurationSection("teams." + team)));
        }
        return game;
    }
}
