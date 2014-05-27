package com.sucy.game.mysql;

/**
 * A column for a MySQL table
 */
public class Column {

    private final String name;
    private final ColumnType type;
    private final int size;

    /**
     * Constructor using a default size
     *
     * @param name column name
     * @param type column type
     */
    public Column(String name, ColumnType type) {
        this.name = name;
        this.type = type;
        this.size = type.getDefaultSize();
    }

    /**
     * Constructor using a custom size
     *
     * @param name column name
     * @param type column type
     * @param size data size
     */
    public Column(String name, ColumnType type, int size) {
        this.name = name;
        this.type = type;
        this.size = Math.max(size, 1);
    }

    /**
     * @return toString data
     */
    public String toString() {
        return name + " " + type.getKey() + "(" + size + ")";
    }
}
