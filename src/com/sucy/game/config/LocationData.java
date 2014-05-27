package com.sucy.game.config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides methods of serializing and then parsing back
 * location data for config storage
 */
public class LocationData {

    /**
     * <p>Serializes a location using as little space as possible</p>
     * <p>This only keeps the block coordinates instead of the precise coordinates</p>
     * <p>Yaw and pitch are not preserved either</p>
     * <p>Returns null for a null location</p>
     *
     * @param loc location to serialize
     * @return    data string
     */
    public static String serializeSimpleLocation(Location loc) {

        // Null locations return null
        if (loc == null) {
            return null;
        }

        // Serialize the location
        return loc.getBlockX() + ","
                + loc.getBlockY() + ","
                + loc.getBlockZ();
    }

    /**
     * <p>Serializes a location with the exact coordinates but without keeping
     * the yaw and pitch</p>
     * <p>Returns null for a null location</p>
     *
     * @param loc location to serialize
     * @return    data string
     */
    public static String serializeLocation(Location loc) {

        // Null locations return null
        if (loc == null) {
            return null;
        }

        // Serialize the location
        return loc.getX() + ","
                + loc.getY() + ","
                + loc.getZ();
    }

    /**
     * <p>Serializes all data for a location including exact coordinates,
     * yaw, and pitch</p>
     * <p>Returns null for a null location</p>
     *
     * @param loc location to serialize
     * @return    data string
     */
    public static String serializeDetailedLocation(Location loc) {

        // Null locations return null
        if (loc == null) {
            return null;
        }

        // Serialize the location
        return loc.getX() + ","
                + loc.getY() + ","
                + loc.getZ() + ","
                + loc.getYaw() + ","
                + loc.getPitch();
    }

    /**
     * <p>Parses a location from a data string</p>
     * <p>This accepts simple, normal, and detailed locations</p>
     * <p>Returns null for invalid formats or a null data string</p>
     *
     * @param dataString data string to parse
     * @return           parsed location
     */
    public static Location parseLocation(World world, String dataString) {

        // Must have a comma and not be null
        if (dataString == null || !dataString.contains(",")) {
            return null;
        }

        String[] pieces = dataString.split(",");

        // Simple and normal locations
        if (pieces.length == 3) {
            return new Location(world,
                    Double.parseDouble(pieces[0]),
                    Double.parseDouble(pieces[1]),
                    Double.parseDouble(pieces[2]));
        }

        // Detailed locations
        else if (pieces.length == 5) {
            return new Location(world,
                    Double.parseDouble(pieces[0]),
                    Double.parseDouble(pieces[1]),
                    Double.parseDouble(pieces[2]),
                    Float.parseFloat(pieces[3]),
                    Float.parseFloat(pieces[4]));
        }

        // Invalid format
        else return null;
    }
}
