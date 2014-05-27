package com.sucy.game;

import com.sucy.game.data.Game;
import com.sucy.game.time.Timer;
import com.sucy.game.time.TimerListener;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Transition manager for games
 */
public class TimeManager implements TimerListener {

    public static final String
        END_GAME = "e",
        START_GAME = "s";

    private GameAPI plugin;

    /**
     * Constructor
     *
     * @param plugin plugin reference
     */
    public TimeManager(GameAPI plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs expiration actions
     *
     * @param timer timer that expired
     */
    @Override
    public void TimerExpired(Timer timer) {

        // Start game action
        if (timer.getKey().equals(START_GAME)) {
            plugin.startGame();
        }

        // End game transition action
        else if (timer.getKey().equals(END_GAME)) {
            plugin.changeGames();
        }

        // Game finished action
        else if (timer.getKey().equals(Game.TIMER_KEY)) {
            plugin.getActiveGame().endGame();
        }
    }

    /**
     * Event handler for the timer events
     *
     * @param timer timer launching the event
     */
    @Override
    public void TimerEvent(Timer timer) {

        // Starting game message
        if (timer.getKey().equals(START_GAME)) {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                player.sendMessage(ChatColor.GOLD + plugin.getActiveGame().getGameName()
                        + ChatColor.GRAY + " is starting in "
                        + ChatColor.GOLD + timer.getTimeLeft() + (timer.getTimeLeft() == 1 ? " second" : " seconds"));
            }
        }

        // End game transition message
        else if (timer.getKey().equals(END_GAME)) {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                player.sendMessage(ChatColor.GRAY + "Moving to " + plugin.getNextGame().getMapName() + " in "
                        + ChatColor.GOLD + timer.getTimeLeft() + (timer.getTimeLeft() == 1 ? " second" : " seconds"));
            }
        }

        // End game message
        else if (timer.getKey().equals(Game.TIMER_KEY)) {
            String message;
            if (timer.getTimeLeft() >= 60) {
                message = ChatColor.GRAY + "There " + (timer.getTimeLeft() == 60 ? "is " : "are ")
                        + ChatColor.GOLD + timer.getTimeLeft() / 60 + (timer.getTimeLeft() == 60 ? " minute " : " minutes ")
                        + ChatColor.GRAY + "left";
            }
            else {
                message = ChatColor.GRAY + "There " + (timer.getTimeLeft() == 1 ? "is " : "are ")
                        + ChatColor.GOLD + timer.getTimeLeft() + (timer.getTimeLeft() == 1 ? " second " : " seconds ")
                        + ChatColor.GRAY + "left";
            }
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                player.sendMessage(message);
            }
        }
    }
}
