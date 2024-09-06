package com.shweit.untitled;

import com.shweit.untitled.util.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Untitled extends JavaPlugin {
    public static FileConfiguration config;
    private static Untitled instance;

    @Override
    public void onEnable() {
        createConfig();
        config = getConfig();
        instance = this;

        Logger.info("Plugin enabled");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Untitled getInstance() {
        return instance;
    }

    private void createConfig() {
        saveDefaultConfig();

        File langFolder = new File(getDataFolder(), "lang");
        if (!langFolder.exists()) {
            langFolder.mkdirs();

            saveResource("lang/en.yml", false);
        }
    }
}
