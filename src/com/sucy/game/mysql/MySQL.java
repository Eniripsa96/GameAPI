package com.sucy.game.mysql;

import com.sucy.game.mysql.table.Table;
import org.bukkit.plugin.Plugin;

import java.sql.*;
import java.util.logging.Level;

/**
 * Manager for connection to and interacting with a MySQL database
 */
public class MySQL {

    private final Plugin plugin;
    private final String user;
    private final String database;
    private final String password;
    private final String port;
    private final String host;

    private Connection connection;

    /**
     * Constructor
     *
     * @param plugin   Plugin reference
     * @param host     Host name
     * @param port     Port number
     * @param database Database name
     * @param username Username
     * @param password Password
     */
    public MySQL(Plugin plugin, String host, String port, String database, String username, String password) {
        this.plugin = plugin;
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = username;
        this.password = password;
        this.connection = null;
    }

    /**
     * Opens a connection to the server
     *
     * @return true if connected successfully, false otherwise
     */
    public boolean openConnection() {

        // Connect to the server
        try {
            plugin.getLogger().info("Connecting to MySQL database...");
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.user, this.password);
            plugin.getLogger().info("Connected to the MySQL database successfully");
        }

        // Unable to connect to the server
        catch (Exception ex) {
            plugin.getLogger().log(Level.SEVERE, "Failed to connect to the MySQL server: " + ex.getMessage());
        }

        return connection != null;
    }

    /**
     * Closes the connection to the database
     */
    public void closeConnection() {

        // Must have a connection to close it
        if (connection != null) {

            // Close the database connection
            try {
                plugin.getLogger().info("Closing connection to the MySQL database...");
                connection.close();
                plugin.getLogger().info("The connection to the MySQL database has been closed!");
            }

            // Unable to close the database connection
            catch (Exception ex) {
                plugin.getLogger().severe("Could not close the MySQL connection: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    /**
     * Queries the MySQL database for a specific entry
     *
     * @param table table name
     * @param name  entry name
     * @return      query results
     */
    public ResultSet query(String table, String name) {

        // Query the database
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery("SELECT * FROM " + table + " WHERE Name='" + name + "'");
        }

        // Problems occurred
        catch (Exception ex) {
            plugin.getLogger().severe("Failed to query SQL database: " + ex.getMessage());
            return null;
        }
    }

    /**
     * Queries the MySQL database
     *
     * @param table table name
     * @return      query results
     */
    public ResultSet queryAll(String table) {

        // Query the database
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery("SELECT * FROM " + table);
        }

        // Problems occurred
        catch (Exception ex) {
            plugin.getLogger().severe("Failed to query SQL database: " + ex.getMessage());
            return null;
        }
    }

    /**
     * Updates the MySQL database
     *
     * @param update update command
     */
    public void updateSQL(String update) {

        // Update the database
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(update);
        }

        // Problems occurred
        catch (Exception ex) {
            plugin.getLogger().severe("Failed to update SQL database: " + ex.getMessage());
        }
    }

    /**
     * Checks if an entry already exists
     *
     * @param table table name
     * @param name  entry name
     * @return      true if exists, false otherwise
     */
    public boolean entryExists(String table, String name) {

        // Query for the player
        ResultSet result = query(table, name);

        // Try to get the next result
        try {
            boolean exists = result.next();
            result.close();
            return exists;

        }

        // Failed to get the next result
        catch (Exception ex) {
            plugin.getLogger().severe("Failed to check for an existing entry: " + ex.getMessage());
            return false;
        }
    }

    /**
     * Checks if a table with the name exists
     *
     * @param name table name
     * @return     true if exists, false otherwise
     */
    public boolean tableExists(String name) {

        // Check if the table exists
        try {
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet result = meta.getTables(null, null, null, new String[] {"TABLE"});
            while (result.next()) {
                if (result.getString("TABLE_NAME").equalsIgnoreCase(name)) return true;
            }
            result.close();
        }

        // An error occurred
        catch (Exception ex) {
            plugin.getLogger().severe("Unable to validate table: " + ex.getMessage());
        }

        return false;
    }

    /**
     * Creates a new table
     *
     * @param table table to create
     */
    public void createTable(Table table) {
        String command = "CREATE TABLE " + table.getName() + " (";
        for (Column column : table.getColumns()) {
            command += column.toString() + ",";
        }
        command = command.substring(0, command.length() - 1) + ")";
        updateSQL(command);
        plugin.getLogger().info("Created a new MySQL table with the name: " + table.getName());
    }
}
