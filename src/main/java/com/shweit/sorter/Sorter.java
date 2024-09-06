package com.shweit.sorter;

import com.shweit.sorter.commands.CommandRegister;
import com.shweit.sorter.util.Logger;
import com.shweit.sorter.util.Translator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Sorter extends JavaPlugin {

    public static FileConfiguration config;

    @Override
    public void onEnable() {
        createConfig();
        config = getConfig();
        Translator.loadLanguageFile();
        Logger.debug("Plugin enabled");

        CommandRegister.registerCommands();

    }

    @Override
    public void onDisable() {
        Logger.debug("Plugin disabled");
    }

    public static Sorter getInstance() {
        return getPlugin(Sorter.class);
    }

    private void createConfig() {
        saveDefaultConfig();

        File langFolder = new File(getDataFolder(), "lang");
        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }

        saveResource("lang/en.yml", false);
    }
}
