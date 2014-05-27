package com.sucy.game.event;

import com.sucy.game.data.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event for when a team gets a kill
 */
public class TeamKillEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Team team;
    private Player killer;

    /**
     * Constructor
     *
     * @param team   team who scored the kill
     * @param killer player who scored the kill
     */
    public TeamKillEvent(Team team, Player killer) {
        this.team = team;
        this.killer = killer;
    }

    /**
     * @return team who scored the kill
     */
    public Team getTeam() {
        return team;
    }

    /**
     * @return player who scored the kill
     */
    public Player getKiller() {
        return killer;
    }

    /**
     * @return gets the handlers for the event
     */
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * @return gets the handlers for the event
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
