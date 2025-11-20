package com.shweit.chestsorter.sorting;

import com.shweit.chestsorter.ChestSorter;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * Central helper class for all sorting operations.
 */
public class SortHelper {

  private final ChestSorter plugin;
  private final InventorySorter sorter;

  /**
   * Constructor for SortHelper.
   *
   * @param plugin The plugin instance
   */
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2",
      justification = "Plugin instance is intentionally shared in Bukkit plugins")
  public SortHelper(ChestSorter plugin) {
    this.plugin = plugin;
    this.sorter = new InventorySorter();
  }

  /**
   * Sorts a container at the given location.
   *
   * @param player The player sorting the container
   * @param location The location of the container
   * @param sortType The sort type (null for default)
   * @param sortOrder The sort order (null for default)
   * @return true if successful, false otherwise
   */
  public boolean sortContainer(Player player, Location location, SortType sortType,
      SortOrder sortOrder) {
    // Get the block
    Block block = location.getBlock();
    BlockState state = block.getState();

    // Check if it's a container
    if (!(state instanceof InventoryHolder holder)) {
      String message = Objects.requireNonNullElse(
          plugin.getConfig().getString("messages.invalid-location",
              "The target block is not a container."),
          "The target block is not a container.");
      player.sendActionBar(Component.text(message, NamedTextColor.RED));
      return false;
    }

    // Check location blacklist
    String locationKey = String.format("%s:%d:%d:%d",
        location.getWorld().getName(),
        location.getBlockX(),
        location.getBlockY(),
        location.getBlockZ());
    if (plugin.getConfig().getStringList("blacklist.locations").contains(locationKey)) {
      String message = Objects.requireNonNullElse(
          plugin.getConfig().getString("messages.blacklisted",
              "This container is blacklisted from sorting."),
          "This container is blacklisted from sorting.");
      player.sendActionBar(Component.text(message, NamedTextColor.RED));
      return false;
    }

    // Check container type blacklist
    String blockKey = block.getType().getKey().toString();
    if (plugin.getConfig().getStringList("blacklist.container-types").contains(blockKey)) {
      String message = Objects.requireNonNullElse(
          plugin.getConfig().getString("messages.blacklisted",
              "This container type is blacklisted from sorting."),
          "This container type is blacklisted from sorting.");
      player.sendActionBar(Component.text(message, NamedTextColor.RED));
      return false;
    }

    // Get default sort settings if not provided
    if (sortType == null) {
      sortType = SortType.fromString(plugin.getConfig().getString("default-sort-type", "NAME"));
    }
    if (sortOrder == null) {
      sortOrder = SortOrder.fromString(plugin.getConfig().getString("default-sort-order", "ASC"));
    }

    // Validate sort settings
    if (sortType == null) {
      String message = Objects.requireNonNullElse(
          plugin.getConfig().getString("messages.invalid-sort-type",
              "Invalid sort type. Available: NAME, QUANTITY, MATERIAL_TYPE, DURABILITY"),
          "Invalid sort type. Available: NAME, QUANTITY, MATERIAL_TYPE, DURABILITY");
      player.sendActionBar(Component.text(message, NamedTextColor.RED));
      return false;
    }
    if (sortOrder == null) {
      String message = Objects.requireNonNullElse(
          plugin.getConfig().getString("messages.invalid-sort-order",
              "Invalid sort order. Use ASC or DESC"),
          "Invalid sort order. Use ASC or DESC");
      player.sendActionBar(Component.text(message, NamedTextColor.RED));
      return false;
    }

    // Sort the container
    sorter.sortInventory(holder.getInventory(), sortType, sortOrder);
    String message = Objects.requireNonNullElse(
        plugin.getConfig().getString("messages.sort-success",
            "Container sorted successfully!"),
        "Container sorted successfully!");
    player.sendActionBar(Component.text(message, NamedTextColor.GREEN));
    playSuccessSound(player);

    return true;
  }

  /**
   * Sorts a container by looking at the target block the player is facing.
   *
   * @param player The player sorting the container
   * @param sortType The sort type (null for default)
   * @param sortOrder The sort order (null for default)
   * @return true if successful, false otherwise
   */
  public boolean sortContainerByRayTrace(Player player, SortType sortType, SortOrder sortOrder) {
    Block block = player.getTargetBlockExact(5);
    if (block == null) {
      String message = Objects.requireNonNullElse(
          plugin.getConfig().getString("messages.invalid-location",
              "No container found. Please look at a container or specify coordinates."),
          "No container found. Please look at a container or specify coordinates.");
      player.sendActionBar(Component.text(message, NamedTextColor.RED));
      return false;
    }

    return sortContainer(player, block.getLocation(), sortType, sortOrder);
  }

  /**
   * Sorts a player's inventory.
   *
   * @param executor The player executing the sort
   * @param target The target player whose inventory to sort
   * @param sortType The sort type (null for default)
   * @param sortOrder The sort order (null for default)
   * @return true if successful, false otherwise
   */
  public boolean sortPlayer(Player executor, Player target, SortType sortType,
      SortOrder sortOrder) {
    // Get default sort settings if not provided
    if (sortType == null) {
      sortType = SortType.fromString(plugin.getConfig().getString("default-sort-type", "NAME"));
    }
    if (sortOrder == null) {
      sortOrder = SortOrder.fromString(plugin.getConfig().getString("default-sort-order", "ASC"));
    }

    // Validate sort settings
    if (sortType == null) {
      String message = Objects.requireNonNullElse(
          plugin.getConfig().getString("messages.invalid-sort-type",
              "Invalid sort type. Available: NAME, QUANTITY, MATERIAL_TYPE, DURABILITY"),
          "Invalid sort type. Available: NAME, QUANTITY, MATERIAL_TYPE, DURABILITY");
      executor.sendActionBar(Component.text(message, NamedTextColor.RED));
      return false;
    }
    if (sortOrder == null) {
      String message = Objects.requireNonNullElse(
          plugin.getConfig().getString("messages.invalid-sort-order",
              "Invalid sort order. Use ASC or DESC"),
          "Invalid sort order. Use ASC or DESC");
      executor.sendActionBar(Component.text(message, NamedTextColor.RED));
      return false;
    }

    // Sort the inventory
    sorter.sortInventory(target.getInventory(), sortType, sortOrder);

    // Send appropriate message
    String message;
    if (target.equals(executor)) {
      message = Objects.requireNonNullElse(
          plugin.getConfig().getString("messages.sort-player-success",
              "Inventory sorted successfully!"),
          "Inventory sorted successfully!");
    } else {
      message = Objects.requireNonNullElse(
          plugin.getConfig().getString("messages.sort-other-success",
              "Sorted %player%'s inventory!"),
          "Sorted %player%'s inventory!")
          .replace("%player%", target.getName());
    }
    executor.sendActionBar(Component.text(message, NamedTextColor.GREEN));
    playSuccessSound(executor);

    return true;
  }

  /**
   * Sorts an inventory directly without player-specific logic.
   * Used by listeners for automatic sorting.
   *
   * @param inventory The inventory to sort
   * @param sortType The sort type (null for default)
   * @param sortOrder The sort order (null for default)
   */
  public void sortInventory(Inventory inventory, SortType sortType, SortOrder sortOrder) {
    // Get default sort settings if not provided
    if (sortType == null) {
      sortType = SortType.fromString(plugin.getConfig().getString("default-sort-type", "NAME"));
    }
    if (sortOrder == null) {
      sortOrder = SortOrder.fromString(plugin.getConfig().getString("default-sort-order", "ASC"));
    }

    // Only sort if settings are valid
    if (sortType != null && sortOrder != null) {
      sorter.sortInventory(inventory, sortType, sortOrder);
    }
  }

  /**
   * Checks if a location is blacklisted.
   *
   * @param location The location to check
   * @return true if blacklisted
   */
  public boolean isBlacklisted(Location location) {
    String locationKey = String.format("%s:%d:%d:%d",
        location.getWorld().getName(),
        location.getBlockX(),
        location.getBlockY(),
        location.getBlockZ());
    return plugin.getConfig().getStringList("blacklist.locations").contains(locationKey);
  }

  /**
   * Plays the success sound to a player.
   *
   * @param player The player to play the sound to
   */
  public void playSuccessSound(Player player) {
    if (!plugin.getConfig().getBoolean("sound.enabled", true)) {
      return;
    }

    String soundName = plugin.getConfig().getString("sound.success", "ITEM_BOOK_PAGE_TURN");
    if (soundName == null) {
      soundName = "ITEM_BOOK_PAGE_TURN";
    }

    float volume = (float) plugin.getConfig().getDouble("sound.volume", 0.5);
    float pitch = (float) plugin.getConfig().getDouble("sound.pitch", 1.0);

    try {
      // Convert to namespaced key (ITEM_BOOK_PAGE_TURN -> item.book.page_turn)
      String keyName = soundName.toLowerCase().replace('_', '.');
      NamespacedKey key = NamespacedKey.minecraft(keyName);
      Sound sound = Registry.SOUNDS.get(key);
      if (sound == null) {
        plugin.getLogger().warning("Invalid sound configured: " + soundName);
        return;
      }
      player.playSound(player.getLocation(), sound, volume, pitch);
    } catch (IllegalArgumentException e) {
      plugin.getLogger().warning("Invalid sound configured: " + soundName);
    }
  }
}
