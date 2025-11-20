package com.shweit.chestsorter;

import com.shweit.chestsorter.sorting.SortOrder;
import com.shweit.chestsorter.sorting.SortType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 * Manages per-player settings for ChestSorter.
 */
public class PlayerData {

  private final ChestSorter plugin;
  private final File dataFile;
  private FileConfiguration data;
  private final Map<UUID, PlayerSettings> cache;

  /**
   * Constructor for PlayerData.
   *
   * @param plugin The plugin instance
   */
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2",
      justification = "Plugin instance is intentionally shared in Bukkit plugins")
  public PlayerData(ChestSorter plugin) {
    this.plugin = plugin;
    this.dataFile = new File(plugin.getDataFolder(), "players.yml");
    this.cache = new HashMap<>();
    loadData();
  }

  /**
   * Loads player data from file.
   */
  private void loadData() {
    if (!dataFile.exists()) {
      plugin.saveResource("players.yml", false);
    }
    data = YamlConfiguration.loadConfiguration(dataFile);
  }

  /**
   * Saves player data to file.
   */
  public void saveData() {
    try {
      data.save(dataFile);
    } catch (IOException e) {
      plugin.getLogger().warning("Could not save players.yml: " + e.getMessage());
    }
  }

  /**
   * Gets player settings for a player.
   *
   * @param player The player
   * @return The player settings
   */
  public PlayerSettings getSettings(Player player) {
    return getSettings(player.getUniqueId());
  }

  /**
   * Gets player settings by UUID.
   *
   * @param uuid The player UUID
   * @return The player settings
   */
  public PlayerSettings getSettings(UUID uuid) {
    // Check cache first
    if (cache.containsKey(uuid)) {
      return cache.get(uuid);
    }

    // Load from file
    String key = uuid.toString();
    boolean enabled = data.getBoolean(key + ".enabled", true);
    boolean hasSeenWelcome = data.getBoolean(key + ".has-seen-welcome", false);
    String sortTypeStr = data.getString(key + ".default-sort-type");
    String sortOrderStr = data.getString(key + ".default-sort-order");

    SortType sortType = sortTypeStr != null ? SortType.fromString(sortTypeStr) : null;
    SortOrder sortOrder = sortOrderStr != null ? SortOrder.fromString(sortOrderStr) : null;

    PlayerSettings settings = new PlayerSettings(enabled, sortType, sortOrder, hasSeenWelcome);
    cache.put(uuid, settings);
    return settings;
  }

  /**
   * Saves player settings.
   *
   * @param uuid The player UUID
   * @param settings The settings to save
   */
  public void saveSettings(UUID uuid, PlayerSettings settings) {
    String key = uuid.toString();
    data.set(key + ".enabled", settings.isEnabled());
    data.set(key + ".has-seen-welcome", settings.hasSeenWelcome());
    data.set(key + ".default-sort-type",
        settings.getDefaultSortType() != null ? settings.getDefaultSortType().name() : null);
    data.set(key + ".default-sort-order",
        settings.getDefaultSortOrder() != null ? settings.getDefaultSortOrder().name() : null);

    cache.put(uuid, settings);
    saveData();
  }

  /**
   * Marks that a player has seen the welcome message.
   *
   * @param player The player
   */
  public void markWelcomeSeen(Player player) {
    PlayerSettings settings = getSettings(player);
    settings.setHasSeenWelcome(true);
    saveSettings(player.getUniqueId(), settings);
  }

  /**
   * Container class for player settings.
   */
  public static class PlayerSettings {
    private boolean enabled;
    private SortType defaultSortType;
    private SortOrder defaultSortOrder;
    private boolean hasSeenWelcome;

    /**
     * Constructor for PlayerSettings.
     *
     * @param enabled Whether sorting is enabled
     * @param defaultSortType Default sort type
     * @param defaultSortOrder Default sort order
     * @param hasSeenWelcome Whether player has seen welcome message
     */
    public PlayerSettings(boolean enabled, SortType defaultSortType,
        SortOrder defaultSortOrder, boolean hasSeenWelcome) {
      this.enabled = enabled;
      this.defaultSortType = defaultSortType;
      this.defaultSortOrder = defaultSortOrder;
      this.hasSeenWelcome = hasSeenWelcome;
    }

    /**
     * Checks if sorting is enabled for this player.
     *
     * @return true if enabled
     */
    public boolean isEnabled() {
      return enabled;
    }

    /**
     * Sets whether sorting is enabled for this player.
     *
     * @param enabled true to enable
     */
    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }

    /**
     * Gets the default sort type for this player.
     *
     * @return the default sort type
     */
    public SortType getDefaultSortType() {
      return defaultSortType;
    }

    /**
     * Sets the default sort type for this player.
     *
     * @param defaultSortType the sort type
     */
    public void setDefaultSortType(SortType defaultSortType) {
      this.defaultSortType = defaultSortType;
    }

    /**
     * Gets the default sort order for this player.
     *
     * @return the default sort order
     */
    public SortOrder getDefaultSortOrder() {
      return defaultSortOrder;
    }

    /**
     * Sets the default sort order for this player.
     *
     * @param defaultSortOrder the sort order
     */
    public void setDefaultSortOrder(SortOrder defaultSortOrder) {
      this.defaultSortOrder = defaultSortOrder;
    }

    /**
     * Checks if the player has seen the welcome message.
     *
     * @return true if seen
     */
    public boolean hasSeenWelcome() {
      return hasSeenWelcome;
    }

    /**
     * Sets whether the player has seen the welcome message.
     *
     * @param hasSeenWelcome true if seen
     */
    public void setHasSeenWelcome(boolean hasSeenWelcome) {
      this.hasSeenWelcome = hasSeenWelcome;
    }
  }
}
