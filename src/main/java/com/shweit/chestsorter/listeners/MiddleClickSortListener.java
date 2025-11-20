package com.shweit.chestsorter.listeners;

import com.shweit.chestsorter.ChestSorter;
import com.shweit.chestsorter.PlayerData;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * Listener for middle-click sorting of containers.
 */
public class MiddleClickSortListener implements Listener {

  private final ChestSorter plugin;

  /**
   * Constructor for MiddleClickSortListener.
   *
   * @param plugin The plugin instance
   */
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2",
      justification = "Plugin instance is intentionally shared in Bukkit plugins")
  public MiddleClickSortListener(ChestSorter plugin) {
    this.plugin = plugin;
  }

  /**
   * Handles inventory click events for middle-click sorting.
   *
   * @param event The inventory click event
   */
  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    // Check if middle-click sorting is enabled
    if (!plugin.getConfig().getBoolean("middle-click.enabled")) {
      return;
    }

    // Check if player has permission
    if (!(event.getWhoClicked() instanceof Player player)) {
      return;
    }

    if (!player.hasPermission("chestsorter.middleclick.sort")) {
      return;
    }

    // Check if player has sorting enabled
    PlayerData playerData = plugin.getPlayerData();
    PlayerData.PlayerSettings settings = playerData.getSettings(player);
    if (!settings.isEnabled()) {
      return;
    }

    // Check if middle-clicking
    if (event.getClick() != ClickType.MIDDLE) {
      return;
    }

    // Get the clicked inventory (the container inventory, not player's)
    Inventory clickedInventory = event.getClickedInventory();
    if (clickedInventory == null) {
      return;
    }

    // Check if it's a container (not player inventory)
    InventoryHolder holder = clickedInventory.getHolder();
    if (holder == null || holder instanceof Player) {
      return;
    }

    // Check if it's a block-based container and if blacklisted
    if (holder instanceof org.bukkit.block.BlockState blockState) {
      Block block = blockState.getBlock();
      Location location = block.getLocation();

      if (plugin.getSortHelper().isBlacklisted(location)) {
        return; // Blacklisted
      }

      // Check container type blacklist
      String blockKey = block.getType().getKey().toString();
      if (plugin.getConfig().getStringList("blacklist.container-types").contains(blockKey)) {
        return; // Container type blacklisted
      }
    }

    // Cancel the middle-click action
    event.setCancelled(true);

    // Sort the container using player's preferences
    plugin.getSortHelper().sortInventory(clickedInventory,
        settings.getDefaultSortType(), settings.getDefaultSortOrder());

    // Send success message
    String message = Objects.requireNonNullElse(
        plugin.getConfig().getString("messages.sort-success",
            "Container sorted successfully!"),
        "Container sorted successfully!");
    player.sendActionBar(Component.text(message, NamedTextColor.GREEN));

    // Play success sound
    plugin.getSortHelper().playSuccessSound(player);
  }
}
