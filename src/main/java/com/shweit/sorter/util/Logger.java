package com.shweit.sorter.util;

import com.shweit.sorter.Sorter;
import org.bukkit.Bukkit;

import java.util.logging.Level;

public final class Logger {

    private Logger() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static final String PREFIX = "[Sorter] ";

    public static void info(final String message) {
        Bukkit.getLogger().log(Level.INFO, PREFIX + message);
    }

    public static void warning(final String message) {
        Bukkit.getLogger().log(Level.WARNING, PREFIX + message);
    }

    public static void error(final String message) {
        Bukkit.getLogger().log(Level.SEVERE, PREFIX + message);
    }

    public static void debug(final String message) {
        boolean debugMode = Sorter.config.getBoolean("debug", false);

        if (debugMode) {
            Bukkit.getLogger().log(Level.INFO, "[DEBUG] " + PREFIX + message);
        }
    }

    public static java.util.logging.Logger getLogger() {
        return Bukkit.getLogger();
    }
}
