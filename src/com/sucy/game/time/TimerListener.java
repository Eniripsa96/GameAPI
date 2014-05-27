package com.sucy.game.time;

/**
 * Interface for classes listening to the timer
 */
public interface TimerListener {

    /**
     * A timer expired
     *
     * @param timer timer that expired
     */
    public void TimerExpired(Timer timer);

    /**
     * An event time for the timer has been reached
     *
     * @param timer timer launching the event
     */
    public void TimerEvent(Timer timer);
}
