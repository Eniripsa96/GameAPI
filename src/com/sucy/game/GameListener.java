package com.sucy.game;

import com.sucy.game.data.CombatMode;
import com.sucy.game.data.Game;
import com.sucy.game.data.PlayerStats;
import com.sucy.game.data.Team;
import com.sucy.game.event.TeamKillEvent;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listener for the game API
 */
public class GameListener implements Listener {

    private GameAPI plugin;

    /**
     * Constructor
     *
     * @param plugin plugin reference
     */
    public GameListener(GameAPI plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Updates the number of players when a player joins
     *
     * @param event event details
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        updatePlayers();
        event.getPlayer().teleport(plugin.getActiveGame().getWorld().getSpawnLocation());
        event.getPlayer().setCanPickupItems(false);
        event.getPlayer().setGameMode(GameMode.CREATIVE);
        event.getPlayer().setScoreboard(plugin.getScoreboard());
        plugin.getScoreboard().getTeam("Spectator").addPlayer(event.getPlayer());
        if (plugin.getState() == GameState.WAITING /* && plugin.getServer().getOnlinePlayers().length >= 2 */) {
            plugin.prepareGame();
        }
    }

    /**
     * Updates the number of players when a player quits
     *
     * @param event event details
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        updatePlayers();
        plugin.getTeam(event.getPlayer().getName()).removePlayer(event.getPlayer());
        plugin.clearTeam(event.getPlayer().getName());
    }

    /**
     * Updates the number of players on the server
     */
    private void updatePlayers() {
        if (plugin.getServerTable() != null) {
            plugin.getServerTable().updatePlayers(plugin.getServer().getOnlinePlayers().length);
        }
    }

    /**
     * Override chat with team chat
     *
     * @param event event details
     */
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (plugin.getGame(event.getPlayer().getWorld()) == null) return;

        Team team = plugin.getTeam(event.getPlayer().getName());
        event.setCancelled(true);

        // Only override the chat if they are on a team
        if (team != null) {
            team.sendMessage(event.getPlayer(), event.getMessage());
        }
        else {
            String message = "<" + ChatColor.GRAY + "[Spectator] " + ChatColor.WHITE + event.getPlayer().getName() + "> " + event.getMessage();
            for (Player player : event.getPlayer().getWorld().getPlayers()) {
                player.sendMessage(message);
            }
        }
    }

    /**
     * Counts players kills and deaths
     *
     * @param event event details
     */
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {

        if (plugin.getState() != GameState.DURING_GAME) return;

        // Player must be on a team for it to count
        if (plugin.getTeam(event.getEntity().getName()) == null) return;

        // Add a death
        plugin.getStats(event.getEntity().getName()).addDeath();

        // Award a kill if applicable
        Player killer = event.getEntity().getKiller();
        if (killer != null && plugin.getTeam(killer.getName()) != null) {
            PlayerStats stats = plugin.getStats(killer.getName());
            stats.addKill();
            Team team = plugin.getTeam(killer.getName());
            TeamKillEvent e = new TeamKillEvent(team, killer);
            plugin.getServer().getPluginManager().callEvent(e);
        }
    }

    /**
     * Controls who can damage who
     *
     * @param event event details
     */
    @EventHandler
    public void onDamaged(EntityDamageByEntityEvent event) {

        if (plugin.getState() != GameState.DURING_GAME) return;
        Game game = plugin.getActiveGame();

        Player player = null;
        if (event.getDamager() instanceof Player) player = (Player)event.getDamager();
        else if (event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof Player) {
            player = (Player)((Projectile) event.getDamager()).getShooter();
        }
        if (player != null) {

            // Get the team
            Team team = plugin.getTeam(player.getName());

            // Players not on a team cannot attack
            if (team == null) {
                event.setCancelled(true);
                return;
            }

            // Get the target
            if (!(event.getEntity() instanceof LivingEntity)) return;
            LivingEntity target = (LivingEntity)event.getEntity();

            // Players
            if (target instanceof Player) {
                Team targetTeam = plugin.getTeam(((Player) target).getName());

                // Cannot attack someone outside the game
                if (targetTeam == null) {
                    event.setCancelled(true);
                }

                // Check combat mode for the same team
                else if (targetTeam == team && !team.getCombatMode().canAttack(CombatMode.TEAMMATES)) {
                    event.setCancelled(true);
                }

                // Check combat mode for other teams
                else if (targetTeam != team && !team.getCombatMode().canAttack(CombatMode.ENEMY_PLAYERS)) {
                    event.setCancelled(true);
                }
            }

            // Everything else
            else {
                if (!team.getCombatMode().canAttack(CombatMode.MOBS)) {
                    event.setCancelled(true);
                }
            }
        }

        // Spectators can't be damaged
        else if (event.getEntity() instanceof Player) {
            Player target = (Player)event.getEntity();
            if (plugin.getTeam(target.getName()) == null) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Cancel damage if the game is over
     *
     * @param event event details
     */
    @EventHandler
    public void onDamaged(EntityDamageEvent event) {

        if (plugin.getState() == GameState.DURING_GAME) return;

        Game game = plugin.getActiveGame();
        if (event.getEntity() instanceof Player) {
            event.setCancelled(true);
        }
    }
}
