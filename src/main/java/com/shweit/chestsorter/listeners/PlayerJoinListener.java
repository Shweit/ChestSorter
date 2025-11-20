package com.shweit.chestsorter.listeners;

import com.shweit.chestsorter.ChestSorter;
import com.shweit.chestsorter.PlayerData;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Listener for player join events to show welcome message.
 */
public class PlayerJoinListener implements Listener {

  private final ChestSorter plugin;

  /**
   * Constructor for PlayerJoinListener.
   *
   * @param plugin The plugin instance
   */
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2",
      justification = "Plugin instance is intentionally shared in Bukkit plugins")
  public PlayerJoinListener(ChestSorter plugin) {
    this.plugin = plugin;
  }

  /**
   * Handles player join events.
   *
   * @param event The player join event
   */
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    PlayerData playerData = plugin.getPlayerData();
    PlayerData.PlayerSettings settings = playerData.getSettings(player);

    // Show welcome message if they haven't seen it yet
    if (!settings.hasSeenWelcome()) {
      String welcomeMessage = plugin.getConfig().getString("messages.welcome",
          "&6Welcome to ChestSorter! Use /sortprefs to customize settings.");

      // Convert color codes and send message if message is not null
      if (welcomeMessage != null) {
        Component message = LegacyComponentSerializer.legacyAmpersand().deserialize(welcomeMessage);
        player.sendMessage(message);
      }

      // Mark as seen
      playerData.markWelcomeSeen(player);
    }
  }
}
