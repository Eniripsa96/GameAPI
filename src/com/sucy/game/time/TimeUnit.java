package com.sucy.game.time;

/**
 * Units of time
 */
public enum TimeUnit {

    TICK (1),
    SECOND (20),
    MINUTE (60 * 20),
    HOUR (60 * 60 * 20),
    DAY (24 * 60 * 60 * 20);

    private final int multiplier;

    /**
     * Enum constructor
     *
     * @param multiplier multiplier to convert this time to seconds
     */
    private TimeUnit(int multiplier) {
        this.multiplier = multiplier;
    }

    /**
     * @return multiplier to convert this time to seconds
     */
    public int getNumberOfTicks() {
        return multiplier;
    }
}