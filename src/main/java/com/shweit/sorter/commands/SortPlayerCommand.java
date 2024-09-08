package com.shweit.sorter.commands;

import com.shweit.sorter.util.Translator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

// /sortplayer <player> [sortType] [sortOrder]
public final class SortPlayerCommand implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(
        @NotNull final CommandSender commandSender,
        @NotNull final Command command,
        @NotNull final String s,
        @NotNull final String[] args
    ) {
        UUID targetUUID = null;
        if (commandSender instanceof Player player) {
            targetUUID = player.getUniqueId();
        }

        if (args.length >= 1) {
            if (commandSender instanceof Player) {
                if (!commandSender.hasPermission("sorter.sortothers")) {
                    if (!args[0].equalsIgnoreCase(commandSender.getName())) {
                        commandSender.sendMessage(ChatColor.RED + Translator.getTranslation("no_permission_sort_others"));
                        return true;
                    }
                }
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                commandSender.sendMessage(ChatColor.RED + Translator.getTranslation("player_not_found"));
                return true;
            }

            targetUUID = target.getUniqueId();
        } else {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(ChatColor.RED + Translator.getTranslation("invalid_usage_console"));
                return true;
            }
        }

        String sortType = args.length >= 2 ? args[1].toLowerCase() : "alphabetical";
        String sortOrder = args.length >= 3 ? args[2].toLowerCase() : "asc";

        if (!sortType.equals("alphabetical") && !sortType.equals("quantity") && !sortType.equals("category") && !sortType.equals("random")) {
            commandSender.sendMessage(ChatColor.RED + Translator.getTranslation("invalid_sort_type"));
            return false;
        }

        if (!sortOrder.equals("asc") && !sortOrder.equals("desc")) {
            commandSender.sendMessage(ChatColor.RED + Translator.getTranslation("invalid_sort_order"));
            return false;
        }

        sortPlayerInventory(targetUUID, sortType, sortOrder);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(
        @NotNull final CommandSender commandSender,
        @NotNull final Command command,
        @NotNull final String s,
        @NotNull final String[] args
    ) {
        if (args.length == 1) {
            if (commandSender.hasPermission("sorter.sortothers")) {
                return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.startsWith(args[0]))
                    .toList();
            }
        } else if (args.length == 2) {
            return List.of("alphabetical", "quantity", "category", "random");
        } else if (args.length == 3) {
            if (!args[2].equals("random")) {
                return List.of("ASC", "DESC");
            }
        }

        return List.of();
    }

    private void sortPlayerInventory(final UUID playerUUID, final String sortType, final String sortOrder) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;

        PlayerInventory inventory = player.getInventory();
        ItemStack[] items = inventory.getContents();

        // List for non-empty items
        List<ItemStack> itemList = new ArrayList<>();
        for (ItemStack item : items) {
            if (item != null && item.getType() != Material.AIR) {
                itemList.add(item);
            }
        }

        // Determine if sorting should be ascending or descending
        boolean ascending = sortOrder.equalsIgnoreCase("asc");

        // Map for the total amount of each item type
        Map<Material, Integer> itemCountMap = new HashMap<>();

        switch (sortType.toLowerCase()) {
            case "alphabetical":
                itemList.sort(Comparator.comparing(item -> item.getType().name()));
                if (!ascending) {
                    Collections.reverse(itemList);  // Reverse for descending order
                }
                break;

            case "quantity":
                // Count the total amount of each item type
                for (ItemStack item : itemList) {
                    itemCountMap.put(item.getType(), itemCountMap.getOrDefault(item.getType(), 0) + item.getAmount());
                }

                // Clear the list and refill it with sorted stacks
                itemList.clear();
                itemCountMap.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue())  // Default to ascending
                        .forEach(entry -> {
                            List<ItemStack> stacks = createItemStacks(entry.getKey(), entry.getValue());
                            itemList.addAll(stacks);  // Add the stacks to the list
                        });

                if (!ascending) {
                    Collections.reverse(itemList);  // Reverse for descending order
                }
                break;

            case "category":
                itemList.sort(Comparator.comparing(item -> {
                    if (item.getType().getCreativeCategory() != null) {
                        return item.getType().getCreativeCategory().name();
                    } else {
                        return item.getType().name();
                    }
                }));
                if (!ascending) {
                    Collections.reverse(itemList);  // Reverse for descending order
                }
                break;

            case "random":
                Collections.shuffle(itemList);  // Random sorting
                break;

            default:
                itemList.sort(Comparator.comparing(item -> item.getType().name()));  // Default to alphabetical sorting
                if (!ascending) {
                    Collections.reverse(itemList);  // Reverse for descending order
                }
                break;
        }

        // Fill the inventory with the sorted items
        for (int i = 0; i < inventory.getSize(); i++) {
            if (i < itemList.size()) {
                inventory.setItem(i, itemList.get(i));
            } else {
                inventory.setItem(i, null);  // Clear the remaining slots
            }
        }

        player.updateInventory();  // Update the player's inventory
        player.sendMessage(ChatColor.GREEN + Translator.getTranslation("inventory_sorted"));
    }

    // Helper method to create ItemStacks based on total amount
    private List<ItemStack> createItemStacks(final Material material, final int totalAmount) {
        List<ItemStack> stacks = new ArrayList<>();
        int maxStackSize = material.getMaxStackSize();  // Maximum stack size for the material
        int remainingAmount = totalAmount;

        // Create stacks with maximum stack size
        while (remainingAmount > 0) {
            int stackSize = Math.min(remainingAmount, maxStackSize);  // Calculate stack size
            ItemStack stack = new ItemStack(material, stackSize);
            stacks.add(stack);
            remainingAmount -= stackSize;
        }

        return stacks;
    }

}
