package com.sucy.game.mysql.table;

import com.sucy.game.mysql.Column;
import com.sucy.game.mysql.MySQL;
import org.bukkit.plugin.Plugin;

/**
 * A table definition for a MySQL server
 */
public class Table {

    private final Column[] columns;
    private final String name;

    protected final MySQL sql;
    protected final Plugin plugin;

    /**
     * Constructor
     *
     * @param plugin  plugin reference
     * @param sql     MySQL reference
     * @param name    table name
     * @param columns table columns
     */
    public Table(Plugin plugin, MySQL sql, String name, Column ... columns) {
        if (columns.length == 0) throw new IllegalArgumentException("Must have at least one column");

        this.plugin = plugin;
        this.sql = sql;
        this.name = name;
        this.columns = columns;

        initialize();
    }

    /**
     * @return table name
     */
    public String getName() {
        return name;
    }

    /**
     * @return table columns
     */
    public Column[] getColumns() {
        return columns;
    }

    /**
     * Initializes the table if it doesn't already exist
     */
    public void initialize() {
        if (!sql.tableExists(name)) sql.createTable(this);
    }
}
