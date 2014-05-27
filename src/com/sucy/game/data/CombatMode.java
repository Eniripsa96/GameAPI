package com.sucy.game.data;

/**
 * Combat mode for teams
 */
public enum CombatMode {

    /**
     * Can hit all targets
     */
    ALL (0x111),

    /**
     * Can hit any players but not mobs
     */
    ALL_PLAYERS (0x110),

    /**
     * Can hit enemy players but not mobs or allies
     */
    ENEMY_PLAYERS (0x100),

    /**
     * Can hit anything besides allies
     */
    NON_TEAMMATES (0x101),

    /**
     * Can attack only mobs
     */
    MOBS (0x001),

    /**
     * Can attack only teammates
     */
    TEAMMATES (0x010),

    ;

    private int id;

    /**
     * Enum constructor
     *
     * @param id mode ID
     */
    private CombatMode(int id) {
        this.id = id;
    }

    /**
     * Checks if the mode enables attacking the group
     *
     * @param mode mode group
     * @return     true if can attack the group, false otherwise
     */
    public boolean canAttack(CombatMode mode) {
        return (id & mode.id) > 0;
    }
}
