package com.shweit.sorter.commands;

import com.shweit.sorter.Sorter;
import org.bukkit.plugin.java.JavaPlugin;

public final class CommandRegister {
    public static void registerCommands() {
        JavaPlugin plugin = Sorter.getInstance();

        plugin.getCommand("sort").setExecutor(new SortCommand());
    }
}
