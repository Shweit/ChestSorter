package com.shweit.sorter.commands;

import com.shweit.sorter.Sorter;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class CommandRegister {
    public static void registerCommands() {
        JavaPlugin plugin = Sorter.getInstance();

        plugin.getCommand("sortcontainer").setExecutor(new SortContainerCommand());
        plugin.getCommand("sortplayer").setExecutor(new SortPlayerCommand());
    }
}
