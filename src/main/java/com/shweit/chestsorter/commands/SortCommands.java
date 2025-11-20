package com.shweit.chestsorter.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.shweit.chestsorter.ChestSorter;
import com.shweit.chestsorter.PlayerData;
import com.shweit.chestsorter.sorting.SortHelper;
import com.shweit.chestsorter.sorting.SortOrder;
import com.shweit.chestsorter.sorting.SortType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.BlockPositionResolver;
import io.papermc.paper.math.BlockPosition;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Brigadier command builders for ChestSorter.
 */
public class SortCommands {

  private static final SuggestionProvider<CommandSourceStack> SORT_TYPE_SUGGESTIONS =
      (context, builder) -> {
        builder.suggest("NAME");
        builder.suggest("QUANTITY");
        builder.suggest("MATERIAL_TYPE");
        builder.suggest("DURABILITY");
        return builder.buildFuture();
      };

  private static final SuggestionProvider<CommandSourceStack> SORT_ORDER_SUGGESTIONS =
      (context, builder) -> {
        builder.suggest("ASC");
        builder.suggest("DESC");
        return builder.buildFuture();
      };

  private static final SuggestionProvider<CommandSourceStack> PLAYER_SUGGESTIONS =
      (context, builder) -> {
        Bukkit.getOnlinePlayers().forEach(p -> builder.suggest(p.getName()));
        return builder.buildFuture();
      };

  /**
   * Creates the /sortcontainer command.
   *
   * @param plugin The plugin instance
   * @return The command builder
   */
  public static LiteralArgumentBuilder<CommandSourceStack> createSortContainerCommand(
      ChestSorter plugin) {
    return Commands.literal("sortcontainer")
        .requires(ctx -> ctx.getSender() instanceof Player
            && ctx.getSender().hasPermission("chestsorter.sort.container"))

        // Path 1: /sortcontainer (uses raytracing with defaults)
        .executes(ctx -> executeSortContainerRaytrace(ctx, plugin))

        // Path 2: /sortcontainer <pos> [sortType] [sortOrder]
        .then(Commands.argument("containerPosition", ArgumentTypes.blockPosition())
            .executes(ctx -> executeSortContainerWithPosition(ctx, plugin, null, null))
            .then(Commands.argument("sortType", StringArgumentType.word())
                .suggests(SORT_TYPE_SUGGESTIONS)
                .executes(ctx -> executeSortContainerWithPosition(ctx, plugin,
                    StringArgumentType.getString(ctx, "sortType"), null))
                .then(Commands.argument("sortOrder", StringArgumentType.word())
                    .suggests(SORT_ORDER_SUGGESTIONS)
                    .executes(ctx -> executeSortContainerWithPosition(ctx, plugin,
                        StringArgumentType.getString(ctx, "sortType"),
                        StringArgumentType.getString(ctx, "sortOrder")))
                )
            )
        );
  }

  /**
   * Creates the /sortplayer command.
   *
   * @param plugin The plugin instance
   * @return The command builder
   */
  public static LiteralArgumentBuilder<CommandSourceStack> createSortPlayerCommand(
      ChestSorter plugin) {
    return Commands.literal("sortplayer")
        .requires(ctx -> ctx.getSender() instanceof Player
            && ctx.getSender().hasPermission("chestsorter.sort.player.self"))

        // Path 1: /sortplayer (sorts own inventory with defaults)
        .executes(ctx -> executeSortPlayerSelf(ctx, plugin))

        // Path 2: /sortplayer <player> [sortType] [sortOrder]
        .then(Commands.argument("player", StringArgumentType.word())
            .suggests(PLAYER_SUGGESTIONS)
            .requires(ctx -> ctx.getSender().hasPermission("chestsorter.sort.player.others"))
            .executes(ctx -> executeSortPlayerOther(ctx, plugin,
                StringArgumentType.getString(ctx, "player"), null, null))
            .then(Commands.argument("sortType", StringArgumentType.word())
                .suggests(SORT_TYPE_SUGGESTIONS)
                .executes(ctx -> executeSortPlayerOther(ctx, plugin,
                    StringArgumentType.getString(ctx, "player"),
                    StringArgumentType.getString(ctx, "sortType"), null))
                .then(Commands.argument("sortOrder", StringArgumentType.word())
                    .suggests(SORT_ORDER_SUGGESTIONS)
                    .executes(ctx -> executeSortPlayerOther(ctx, plugin,
                        StringArgumentType.getString(ctx, "player"),
                        StringArgumentType.getString(ctx, "sortType"),
                        StringArgumentType.getString(ctx, "sortOrder")))
                )
            )
        );
  }

  /**
   * Creates the /sortprefs command.
   *
   * @param plugin The plugin instance
   * @return The command builder
   */
  public static LiteralArgumentBuilder<CommandSourceStack> createSortPrefsCommand(
      ChestSorter plugin) {
    return Commands.literal("sortprefs")
        .requires(ctx -> ctx.getSender() instanceof Player)

        // Path 1: /sortprefs (shows current settings)
        .executes(ctx -> executeSortPrefsShow(ctx, plugin))

        // Path 2: /sortprefs toggle
        .then(Commands.literal("toggle")
            .executes(ctx -> executeSortPrefsToggle(ctx, plugin))
        )

        // Path 3: /sortprefs type <sortType>
        .then(Commands.literal("type")
            .then(Commands.argument("sortType", StringArgumentType.word())
                .suggests(SORT_TYPE_SUGGESTIONS)
                .executes(ctx -> executeSortPrefsType(ctx, plugin,
                    StringArgumentType.getString(ctx, "sortType")))
            )
        )

        // Path 4: /sortprefs order <sortOrder>
        .then(Commands.literal("order")
            .then(Commands.argument("sortOrder", StringArgumentType.word())
                .suggests(SORT_ORDER_SUGGESTIONS)
                .executes(ctx -> executeSortPrefsOrder(ctx, plugin,
                    StringArgumentType.getString(ctx, "sortOrder")))
            )
        );
  }

  private static int executeSortContainerRaytrace(CommandContext<CommandSourceStack> ctx,
      ChestSorter plugin) {
    Player player = (Player) ctx.getSource().getSender();
    SortHelper helper = plugin.getSortHelper();
    PlayerData playerData = plugin.getPlayerData();
    PlayerData.PlayerSettings settings = playerData.getSettings(player);

    // Use raytracing with player's preferences
    boolean success = helper.sortContainerByRayTrace(player,
        settings.getDefaultSortType(), settings.getDefaultSortOrder());
    return success ? 1 : 0;
  }

  private static int executeSortContainerWithPosition(CommandContext<CommandSourceStack> ctx,
      ChestSorter plugin, String sortTypeStr, String sortOrderStr) {
    Player player = (Player) ctx.getSource().getSender();
    SortHelper helper = plugin.getSortHelper();
    PlayerData playerData = plugin.getPlayerData();
    PlayerData.PlayerSettings settings = playerData.getSettings(player);

    try {
      // Get block position argument
      BlockPositionResolver posResolver = ctx.getArgument("containerPosition",
          BlockPositionResolver.class);
      BlockPosition blockPos = posResolver.resolve(ctx.getSource());

      // Convert BlockPosition to Location
      Location location = new Location(
          ctx.getSource().getLocation().getWorld(),
          blockPos.blockX(),
          blockPos.blockY(),
          blockPos.blockZ()
      );

      // Parse sort settings (use player preferences if not specified)
      SortType type = sortTypeStr != null ? SortType.fromString(sortTypeStr)
          : settings.getDefaultSortType();
      SortOrder order = sortOrderStr != null ? SortOrder.fromString(sortOrderStr)
          : settings.getDefaultSortOrder();

      // Use helper to sort
      boolean success = helper.sortContainer(player, location, type, order);
      return success ? 1 : 0;
    } catch (CommandSyntaxException e) {
      String message = Objects.requireNonNullElse(
          plugin.getConfig().getString("messages.invalid-location",
              "Invalid location specified."),
          "Invalid location specified.");
      player.sendActionBar(Component.text(message, NamedTextColor.RED));
      return 0;
    }
  }

  private static int executeSortPlayerSelf(CommandContext<CommandSourceStack> ctx,
      ChestSorter plugin) {
    Player player = (Player) ctx.getSource().getSender();
    SortHelper helper = plugin.getSortHelper();
    PlayerData playerData = plugin.getPlayerData();
    PlayerData.PlayerSettings settings = playerData.getSettings(player);

    // Sort own inventory with player's preferences
    boolean success = helper.sortPlayer(player, player,
        settings.getDefaultSortType(), settings.getDefaultSortOrder());
    return success ? 1 : 0;
  }

  private static int executeSortPlayerOther(CommandContext<CommandSourceStack> ctx,
      ChestSorter plugin, String targetName, String sortTypeStr, String sortOrderStr) {
    Player player = (Player) ctx.getSource().getSender();
    SortHelper helper = plugin.getSortHelper();
    PlayerData playerData = plugin.getPlayerData();
    PlayerData.PlayerSettings settings = playerData.getSettings(player);

    // Get target player from arguments
    Player target = Bukkit.getPlayer(targetName);
    if (target == null) {
      String message = Objects.requireNonNullElse(
          plugin.getConfig().getString("messages.player-not-found",
              "Player not found."),
          "Player not found.");
      player.sendActionBar(Component.text(message, NamedTextColor.RED));
      return 0;
    }

    // Parse sort settings from arguments (use executor's preferences if not specified)
    SortType type = sortTypeStr != null ? SortType.fromString(sortTypeStr)
        : settings.getDefaultSortType();
    SortOrder order = sortOrderStr != null ? SortOrder.fromString(sortOrderStr)
        : settings.getDefaultSortOrder();

    // Use helper to sort
    boolean success = helper.sortPlayer(player, target, type, order);
    return success ? 1 : 0;
  }

  private static int executeSortPrefsShow(CommandContext<CommandSourceStack> ctx,
      ChestSorter plugin) {
    Player player = (Player) ctx.getSource().getSender();
    PlayerData playerData = plugin.getPlayerData();
    PlayerData.PlayerSettings settings = playerData.getSettings(player);

    // Get message template from config
    String message = Objects.requireNonNullElse(
        plugin.getConfig().getString("messages.prefs-info",
            "&7ChestSorter settings: &e%enabled% &7| Type: &e%type% &7| Order: &e%order%"),
        "&7ChestSorter settings: &e%enabled% &7| Type: &e%type% &7| Order: &e%order%");

    // Replace placeholders
    message = message.replace("%enabled%", settings.isEnabled() ? "ENABLED" : "DISABLED")
        .replace("%type%", settings.getDefaultSortType().name())
        .replace("%order%", settings.getDefaultSortOrder().name());

    // Send message
    Component component = LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    player.sendMessage(component);

    return 1;
  }

  private static int executeSortPrefsToggle(CommandContext<CommandSourceStack> ctx,
      ChestSorter plugin) {
    Player player = (Player) ctx.getSource().getSender();
    PlayerData playerData = plugin.getPlayerData();
    PlayerData.PlayerSettings settings = playerData.getSettings(player);

    // Toggle the enabled state
    settings.setEnabled(!settings.isEnabled());
    playerData.saveSettings(player.getUniqueId(), settings);

    // Send appropriate message
    String messageKey = settings.isEnabled() ? "messages.prefs-enabled" : "messages.prefs-disabled";
    String defaultMsg = settings.isEnabled()
        ? "&aChestSorter enabled!"
        : "&cChestSorter disabled! Use &e/sortprefs toggle &cto re-enable.";
    String message = Objects.requireNonNullElse(
        plugin.getConfig().getString(messageKey, defaultMsg),
        defaultMsg);

    Component component = LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    player.sendMessage(component);

    return 1;
  }

  private static int executeSortPrefsType(CommandContext<CommandSourceStack> ctx,
      ChestSorter plugin, String sortTypeStr) {
    Player player = (Player) ctx.getSource().getSender();
    PlayerData playerData = plugin.getPlayerData();
    PlayerData.PlayerSettings settings = playerData.getSettings(player);

    // Parse sort type
    SortType sortType = SortType.fromString(sortTypeStr);
    if (sortType == null) {
      String message = Objects.requireNonNullElse(
          plugin.getConfig().getString("messages.invalid-sort-type",
              "Invalid sort type. Available: NAME, QUANTITY, MATERIAL_TYPE, DURABILITY"),
          "Invalid sort type. Available: NAME, QUANTITY, MATERIAL_TYPE, DURABILITY");
      player.sendActionBar(Component.text(message, NamedTextColor.RED));
      return 0;
    }

    // Update settings
    settings.setDefaultSortType(sortType);
    playerData.saveSettings(player.getUniqueId(), settings);

    // Send success message
    String message = Objects.requireNonNullElse(
        plugin.getConfig().getString("messages.prefs-updated",
            "&aSort preferences updated!"),
        "&aSort preferences updated!");
    Component component = LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    player.sendMessage(component);

    return 1;
  }

  private static int executeSortPrefsOrder(CommandContext<CommandSourceStack> ctx,
      ChestSorter plugin, String sortOrderStr) {
    Player player = (Player) ctx.getSource().getSender();
    PlayerData playerData = plugin.getPlayerData();
    PlayerData.PlayerSettings settings = playerData.getSettings(player);

    // Parse sort order
    SortOrder sortOrder = SortOrder.fromString(sortOrderStr);
    if (sortOrder == null) {
      String message = Objects.requireNonNullElse(
          plugin.getConfig().getString("messages.invalid-sort-order",
              "Invalid sort order. Use ASC or DESC"),
          "Invalid sort order. Use ASC or DESC");
      player.sendActionBar(Component.text(message, NamedTextColor.RED));
      return 0;
    }

    // Update settings
    settings.setDefaultSortOrder(sortOrder);
    playerData.saveSettings(player.getUniqueId(), settings);

    // Send success message
    String message = Objects.requireNonNullElse(
        plugin.getConfig().getString("messages.prefs-updated",
            "&aSort preferences updated!"),
        "&aSort preferences updated!");
    Component component = LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    player.sendMessage(component);

    return 1;
  }
}
