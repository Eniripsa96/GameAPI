package com.sucy.game.time;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * A simple timer that can launch events at certain times such as
 * when the time is about to run out
 */
public class Timer {

    private final Plugin plugin;
    private final TimerListener listener;
    private final String key;
    private final int[] eventTimes;

    private TimeUnit timeUnit;
    private int timeLimit;
    private Task task;
    private int time;


    /**
     * Constructor
     *
     * @param plugin     plugin owning this timer
     * @param listener   what will receive the time events
     * @param key        key to tell apart the timer from others
     * @param timeLimit  time limit for the timer
     * @param unit       unit of time counting by
     * @param eventTimes the times when events should be launched
     */
    public Timer(Plugin plugin, TimerListener listener, String key, int timeLimit, TimeUnit unit, int ... eventTimes) {
        this.timeLimit = timeLimit;
        this.time = this.timeLimit;
        this.eventTimes = eventTimes;
        this.timeUnit = unit;
        this.key = key;
        this.listener = listener;
        this.plugin = plugin;
    }

    /**
     * @return timer key
     */
    public String getKey() {
        return key;
    }

    /**
     * @return the time left in ticks
     */
    public int getTimeLeft() {
        return time;
    }

    /**
     * @return time limit in terms of the current time unit
     */
    public int getTimeLimit() {
        return timeLimit;
    }

    /**
     * Current time unit being used
     *
     * @return timeUnit
     */
    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    /**
     * Sets a new time limit for the timer
     *
     * @param amount time amount
     * @param unit   time unit to go by
     */
    public void setTimeLimit(int amount, TimeUnit unit) {
        timeUnit = unit;
        timeLimit = amount;
        time = timeLimit;
    }

    /**
     * Starts the timer
     *
     * @return true if started, false if already running
     */
    public boolean start() {
        if (task == null) {
            task = new Task(this);
            task.runTaskTimer(plugin, timeUnit.getNumberOfTicks(), timeUnit.getNumberOfTicks());
            return true;
        }
        else return false;
    }

    /**
     * <p>Starts the timer with a different time than the default</p>
     * <p>If the timer is already running, it will be updated with the new time</p>
     *
     * @param time time to use
     */
    public void start(int time) {
        stop();
        this.time = time;
        start();
    }

    /**
     * Pauses the timer
     *
     * @return true if paused, false if wasn't running
     */
    public boolean pause() {
        if (task != null) {
            task.cancel();
            task = null;
            return true;
        }
        else return false;
    }

    /**
     * <p>Stops the timer, resetting the time back to the original limit</p>
     * <p>If the timer isn't running, the time is still reset back to the limit</p>
     */
    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        time = timeLimit;
    }

    /**
     * <p>Stops the timer and starts it again at the time limit</p>
     * <p>If the timer isn't running, it just starts the timer
     * after resetting the time left to the time limit</p>
     */
    public void restart() {
        stop();
        start();
    }

    /**
     * <p>Adds time to the timer according to the current time unit</p>
     * <p>The new time can be above the initial limit and will act normally</p>
     * <p>If the added time causes the time to drop to 0 or below, the time
     * will be set to 1 tick so that the next tick will end the timer.</p>
     *
     * @param amount amount of time to add
     */
    public void addTime(int amount) {
        time += amount;
        if (time < 1) time = 1;
    }

    /**
     * Repeating task for timers
     */
    private class Task extends BukkitRunnable {

        Timer timer;

        /**
         * Constructor
         *
         * @param timer timer counting for
         */
        public Task(Timer timer) {
            this.timer = timer;
        }

        /**
         * Decrements the time left and checks if an event should be launched
         */
        @Override
        public void run() {
            time--;

            // Update the player's level bars
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                player.setLevel(time);
                player.setExp(0);
            }

            // Apply the expiration event if applicable
            if (time <= 0) {
                listener.TimerExpired(timer);
                stop();
                return;
            }

            // Apply event times if applicable
            for (int eventTime : eventTimes) {
                if (eventTime == time) {
                    listener.TimerEvent(timer);
                    return;
                }
            }
        }
    }
}
