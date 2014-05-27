package com.sucy.game;

import com.sucy.game.commands.game.GameCommander;
import com.sucy.game.commands.setup.SetupCommander;
import com.sucy.game.config.Config;
import com.sucy.game.data.Game;
import com.sucy.game.data.PlayerStats;
import com.sucy.game.data.Team;
import com.sucy.game.mysql.MySQL;
import com.sucy.game.mysql.table.ServerTable;
import com.sucy.game.mysql.table.PlayerTable;
import com.sucy.game.time.TimeUnit;
import com.sucy.game.time.Timer;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GameAPI extends JavaPlugin implements PluginMessageListener {

    private final HashMap<String, PlayerStats> stats = new HashMap<String, PlayerStats>();
    private final HashMap<String, Team> players = new HashMap<String, Team>();
    private final HashMap<String, GamePlugin> types = new HashMap<String, GamePlugin>();
    private final ArrayList<Game> games = new ArrayList<Game>();

    private MySQL mySQL;
    private int activeGame;
    private int nextGame;
    private Timer endTimer;
    private Timer startTimer;
    private PlayerTable playerTable;
    private ServerTable serverTable;
    private GameState state;
    private TimeManager timeManager;
    private Scoreboard scoreboard;
    private String lobby;

    /**
     * Sets up plugin data
     */
    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Connect to the MySQL database
        ConfigurationSection sql = getConfig().getConfigurationSection("MySQL");
        mySQL = new MySQL(this, sql.getString("host"), sql.getString("port"), sql.getString("database"), sql.getString("username"), sql.getString("password"));
        boolean worked = mySQL.openConnection();

        // Failed to connect to MySQL database
        if (!worked) {
            getLogger().severe("Unable to connect to MySQL database - This server will be considered offline");
        }

        // Get game types
        for (Plugin plugin : getServer().getPluginManager().getPlugins()) {
            if (plugin instanceof GamePlugin) {
                GamePlugin game = (GamePlugin)plugin;
                String key = game.getKey().toLowerCase();
                if (types.containsKey(key)) {
                    getLogger().severe("Duplicate game type keys found: " + key);
                }
                else {
                    types.put(game.getKey().toLowerCase(), game);
                    getLogger().info("Loaded game type: [" + game.getKey() + "] " + game.getLabel());
                }
            }
        }

        // Load set up maps
        try {
            File mapDir = new File(getDataFolder().getAbsolutePath() + File.separator + "maps");
            File[] files = mapDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    String worldName = file.getName().replace(".yml", "");
                    try {
                        games.add(Game.load(this, worldName));
                        getLogger().info("Loaded data for the world: " + worldName);
                    }
                    catch (Exception ex) {
                        getLogger().info("Failed to load data for the world: " + worldName);
                    }
                }
            }
        }
        catch (Exception ex) {
            getLogger().severe(ChatColor.RED + "Encountered an error while trying to load game maps");
            ex.printStackTrace();
        }

        // Set up the MySQL tables if applicable
        if (worked) {
            playerTable = new PlayerTable(this, mySQL);
            serverTable = new ServerTable(this, mySQL, ProxyServer.getInstance().getName(), getServer().getIp());
            serverTable.enable(getConfig().getInt("max-players"));
        }

        // Enable the game if applicable
        if (getConfig().getBoolean("game-enabled", false) && games.size() > 0 && worked) {

            // Kick any previously online players
            for (Player player : getServer().getOnlinePlayers()) {
                player.kickPlayer("The server is now restricted for game usage only");
            }

            // Set up the game events
            new GameCommander(this);
            new GameListener(this);

            // Initialize variables
            activeGame = 0;
            updateScoreboard();
            nextGame = 1 % games.size();
            timeManager = new TimeManager(this);
            endTimer = new Timer(this, timeManager, TimeManager.END_GAME, 30, TimeUnit.SECOND, Game.DEFAULT_EVENT_TIMES);
            startTimer = new Timer(this, timeManager, TimeManager.START_GAME, 30, TimeUnit.SECOND, Game.DEFAULT_EVENT_TIMES);
            setState(GameState.WAITING);
            serverTable.setMap(getActiveGame().getMapName());
            getLogger().info("Game is now awaiting players");
            lobby = getConfig().getString("lobby-server");
            getServer().getMessenger().registerIncomingPluginChannel(this, "ipvp", this);
        }

        // If not enabled, then prepare setup functions
        else new SetupCommander(this);
    }

    /**
     * Cleans up plugin data before exiting
     */
    @Override
    public void onDisable() {

        // Clean up MySQL connections
        if (serverTable != null) {
            serverTable.disable();
        }
        submitStats();
        mySQL.closeConnection();

        // Clean up listeners
        HandlerList.unregisterAll(this);
    }

    /**
     * Locking down the server
     *
     * @param channel channel of the message
     * @param player  player who sent the message
     * @param message message received
     */
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        getLogger().info("Received message from channel: " + channel);
        if (!channel.equals("BungeeCord") || state == GameState.DISABLED) {
            return;
        }
        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
            String sub = in.readUTF();

            // lockdown
            if(sub.equals("ipvp")) {
                for (Player p : getServer().getOnlinePlayers()) {
                    if (BungeeCord.getInstance().getServerInfo(lobby) == null) {
                        p.kickPlayer("The server was shutdown");
                    }
                    else {
                        p.performCommand("send " + p.getName() + " " + lobby);
                    }
                }
                HandlerList.unregisterAll(this);
                if (getActiveGame().getTimer() != null) {
                    getActiveGame().getTimer().stop();
                }
                setState(GameState.DISABLED);
                startTimer.stop();
                endTimer.stop();
                scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                new SetupCommander(this);
                getLogger().info("The server has been locked down");
            }
        }
        catch (Exception ex) { /* Do nothing */ }
    }

    /**
     * Updates the scoreboard for the plugin
     */
    public void updateScoreboard() {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = scoreboard.registerNewObjective("score", "dummy");
        for (Team team : getActiveGame().getTeams()) {
            org.bukkit.scoreboard.Team t = scoreboard.registerNewTeam(team.getName());
            t.setPrefix(team.getPrefix());
            OfflinePlayer p = Bukkit.getOfflinePlayer(team.getColoredName());
            obj.getScore(p).setScore(0);
        }
        scoreboard.registerNewTeam("Spectator").setPrefix(ChatColor.AQUA + "");
        for (Player player : getServer().getOnlinePlayers()) {
            player.setScoreboard(scoreboard);
        }
    }

    /**
     * @return scoreboard used by the game
     */
    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    /**
     * @return current game state of the server
     */
    public GameState getState() {
        return state;
    }

    /**
     * Sets the state of the game and updates the MySQL database
     *
     * @param state state of the game
     */
    private void setState(GameState state) {
        this.state = state;
        if (serverTable != null) {
            serverTable.setState(state.getId());
        }
    }

    /**
     * Pauses the game if it is during pre-game
     */
    public void pause() {
        if (state == GameState.PRE_GAME) {
            setState(GameState.PAUSED);
            startTimer.stop();
        }
    }

    /**
     * Resumes the countdown to starting the game if it was paused with the new time
     *
     * @param time time to resume with
     */
    public void resume(int time) {
        if (state == GameState.PAUSED) {
            setState(GameState.PRE_GAME);
            startTimer.addTime(time - startTimer.getTimeLeft());
            startTimer.start();
        }
    }

    /**
     * @return active game on the server
     */
    public Game getActiveGame() {
        return games.get(activeGame);
    }

    /**
     * @return next game in the rotation
     */
    public Game getNextGame() {
        return games.get(nextGame);
    }

    /**
     * Retrieves a game by world
     *
     * @param world world reference
     * @return      game in the world or null if not found
     */
    public Game getGame(World world) {
        return getGame(world.getName());
    }

    /**
     * Retrieves a game by world name
     *
     * @param world name of the world
     * @return      game reference
     */
    public Game getGame(String world) {
        for (Game game : games) {
            if (game.getWorldName().equals(world)) {
                return game;
            }
        }
        return null;
    }

    /**
     * Sets the next game in the cycle
     *
     * @param game next game to cycle to
     */
    public void setNextGame(Game game) {
        for (int i = 0; i < games.size(); i++) {
            if (games.get(i) == game) nextGame = i;
        }
    }

    /**
     * @return the list of registered games
     */
    public ArrayList<Game> getGames() {
        return games;
    }

    /**
     * Starts the current gamet
     */
    public void startGame() {
        getType(getActiveGame().getGameKey()).enable();
        setState(GameState.DURING_GAME);
        getActiveGame().setupTimer(timeManager).start();
        for (Player player : getServer().getOnlinePlayers()) {
            player.sendMessage(ChatColor.GOLD + getActiveGame().getGameName()
                    + ChatColor.DARK_GREEN + " has started!");
        }
    }

    /**
     * Starts the transition period to the next game
     */
    public void startTransition() {
        endTimer.start();
        getType(getActiveGame().getGameKey()).disable();
        setState(GameState.POST_GAME);
        getLogger().info("Post game started");
    }

    /**
     * Changes to the next game in the rotation
     */
    public void changeGames() {
        endTimer.stop();
        Game game = getActiveGame();
        game.reset();
        players.clear();
        activeGame = nextGame;
        nextGame = (nextGame + 1) % games.size();
        prepareGame();

        // Teleport players to the new spawn
        Location spawn = getActiveGame().getWorld().getSpawnLocation();
        for (Player player : getServer().getOnlinePlayers()) {
            player.teleport(spawn);
        }
        serverTable.setMap(getActiveGame().getMapName());
    }

    /**
     * Prepares to start the next game
     */
    public void prepareGame() {
        startTimer.start();
        setState(GameState.PRE_GAME);
        getLogger().info("Pre game started");
    }

    /**
     * @return available game types
     */
    public Collection<GamePlugin> getTypes() {
        return types.values();
    }

    /**
     * Gets a game type by key
     *
     * @param key type key
     * @return    game type
     */
    public GamePlugin getType(String key) {
        return types.get(key.toLowerCase());
    }

    /**
     * Retrieves the stats for a player
     *
     * @param playerName name of player to retrieve for
     * @return           stats of the player
     */
    public PlayerStats getStats(String playerName) {
        playerName = playerName.toLowerCase();
        if (!stats.containsKey(playerName)) {
            stats.put(playerName, new PlayerStats());
        }
        return stats.get(playerName);
    }

    /**
     * Gets the total stats for a player
     *
     * @param playerName name of the player
     * @return           total stats for the player
     */
    public PlayerStats getTotalStats(String playerName) {
        playerName = playerName.toLowerCase();
        int[] data = playerTable.getStats(playerName);
        PlayerStats current = getStats(playerName);
        return new PlayerStats(data[0] + current.getKills(), data[1] + current.getDeaths());
    }

    /**
     * Submits all stats to the database
     */
    public void submitStats() {
        for (Map.Entry<String, PlayerStats> entry : stats.entrySet()) {
            playerTable.updatePlayer(entry.getKey(), entry.getValue().getKills(), entry.getValue().getDeaths());
        }
        stats.clear();
    }

    /**
     * Registers a player's team
     *
     * @param player name of player
     * @param team   name of team
     */
    public void registerTeam(String player, Team team) {
        players.put(player.toLowerCase(), team);
    }

    /**
     * Retrieves the name of the team the player is on
     *
     * @param player name of player to retrieve for
     * @return       name of the team the player is on
     */
    public Team getTeam(String player) {
        return players.get(player.toLowerCase());
    }

    /**
     * Clears the registered team for the player
     *
     * @param playerName player to clear for
     */
    public void clearTeam(String playerName) {
        players.remove(playerName.toLowerCase());
    }

    /**
     * @return MySQL table for the server
     */
    public ServerTable getServerTable() {
        return serverTable;
    }

    /**
     * Sets up a game for a world
     *
     * @param world world to set up for
     * @param type  game type to set up
     */
    public void setupGame(World world, GamePlugin type) {
        Config config = Game.getConfig(this, world.getName());
        type.getDefaultConfig().copyTo(config);
        Game game = Game.load(this, world.getName());
        games.add(game);
        game.setKey(type.getKey());
    }
}
