package com.sucy.game.mysql;

/**
 * Types of columns supported
 */
public enum ColumnType {

    /**
     * A string
     */
    STRING ("VARCHAR", 32),

    /**
     * An integer
     */
    INT ("INT", 1),

    ;

    private String key;
    private int defaultSize;

    /**
     * Enum constructor
     *
     * @param key type key
     */
    private ColumnType(String key, int defaultSize) {
        this.key = key;
        this.defaultSize = defaultSize;
    }

    /**
     * @return type key
     */
    public String getKey() {
        return key;
    }

    /**
     * Default size of the variable
     *
     * @return default size
     */
    public int getDefaultSize() {
        return defaultSize;
    }
}
