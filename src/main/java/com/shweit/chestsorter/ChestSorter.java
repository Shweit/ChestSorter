package com.shweit.chestsorter;

import com.shweit.chestsorter.commands.SortCommands;
import com.shweit.chestsorter.listeners.MiddleClickSortListener;
import com.shweit.chestsorter.listeners.PlayerJoinListener;
import com.shweit.chestsorter.listeners.SneakSortListener;
import com.shweit.chestsorter.sorting.SortHelper;
import io.papermc.lib.PaperLib;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * Created by Dennis van den Brock.
 *
 * @author Copyright (c) Dennis van den Brock. All Rights Reserved.
 */
public class ChestSorter extends JavaPlugin {

  private SortHelper sortHelper;
  private PlayerData playerData;

  @Override
  public void onEnable() {
    PaperLib.suggestPaper(this);

    saveDefaultConfig();

    // Initialize sort helper
    sortHelper = new SortHelper(this);

    // Initialize player data
    playerData = new PlayerData(this);

    // Register commands using Brigadier API
    registerCommands();

    // Register listeners
    registerListeners();

    getLogger().info("ChestSorter v2 enabled successfully!");
  }

  /**
   * Gets the sort helper instance.
   *
   * @return The sort helper
   */
  public SortHelper getSortHelper() {
    return sortHelper;
  }

  /**
   * Gets the player data manager instance.
   *
   * @return The player data manager
   */
  public PlayerData getPlayerData() {
    return playerData;
  }

  /**
   * Registers all command handlers using Brigadier Command API.
   */
  private void registerCommands() {
    LifecycleEventManager<Plugin> manager = this.getLifecycleManager();
    manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
      final Commands commands = event.registrar();
      commands.register(SortCommands.createSortContainerCommand(this).build());
      commands.register(SortCommands.createSortPlayerCommand(this).build());
      commands.register(SortCommands.createSortPrefsCommand(this).build());
    });
  }

  /**
   * Registers all event listeners.
   */
  private void registerListeners() {
    getServer().getPluginManager().registerEvents(new SneakSortListener(this), this);
    getServer().getPluginManager().registerEvents(new MiddleClickSortListener(this), this);
    getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
  }
}
