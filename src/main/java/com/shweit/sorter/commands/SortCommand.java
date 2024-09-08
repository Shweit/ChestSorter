package com.shweit.sorter.commands;

import com.shweit.sorter.Sorter;
import com.shweit.sorter.util.Translator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
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

public final class SortCommand implements CommandExecutor, TabExecutor {
    private static final String[] SORT_TYPES = {"alphabetical", "amount", "category", "random"};

    @Override
    public boolean onCommand(@NotNull final CommandSender commandSender, @NotNull final Command command, @NotNull final String s, @NotNull final String[] args) {
        if (args.length == 0) {
            if (!(commandSender instanceof Player player)) {
                commandSender.sendMessage(ChatColor.RED + Translator.getTranslation("player_only_command"));
                commandSender.sendMessage(ChatColor.RED + Translator.getTranslation("console_usage"));
                return true;
            }

            sortPlayerInventory(player.getUniqueId());
        }

        if (args.length == 1) {
            if (!args[0].equals(commandSender.getName())) {
                if (!commandSender.hasPermission("chestsorter.sort.others")) {
                    commandSender.sendMessage(ChatColor.RED + Translator.getTranslation("no_permission_sort_others"));
                    return true;
                }
            }

            Player target = commandSender.getServer().getPlayer(args[0]);
            if (target == null) {
                commandSender.sendMessage(ChatColor.RED + Translator.getTranslation("player_not_found"));
                return true;
            }

            sortPlayerInventory(target.getUniqueId());
        }

        if (args.length == 2) {
            if (!(commandSender instanceof Player player)) {
                commandSender.sendMessage(ChatColor.RED + Translator.getTranslation("player_only_command"));
                commandSender.sendMessage(ChatColor.RED + Translator.getTranslation("console_usage"));
                return true;
            }

            sortPlayerInventory(player.getUniqueId(), args[1]);
        }

        if (args.length == 3) {
            int x;
            int y;
            int z;

            try {
                x = Integer.parseInt(args[0]);
                y = Integer.parseInt(args[1]);
                z = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                commandSender.sendMessage(ChatColor.RED + Translator.getTranslation("invalid_coordinates"));
                return true;
            }

            sortBlockInventory(commandSender, x, y, z);
        }

        if (args.length == 4) {
            int x;
            int y;
            int z;

            try {
                x = Integer.parseInt(args[0]);
                y = Integer.parseInt(args[1]);
                z = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                commandSender.sendMessage(ChatColor.RED + Translator.getTranslation("invalid_coordinates"));
                return true;
            }

            sortBlockInventory(commandSender, x, y, z, args[3]);
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull final CommandSender commandSender, @NotNull final Command command, @NotNull final String s, @NotNull final String[] args) {
        if (args.length == 1) {
            ArrayList<String> target = new ArrayList<>();

            if (commandSender instanceof Player) {
                target.add(commandSender.getName());
            }

            if (commandSender.hasPermission("chestsorter.sort.others")) {
                for (Player player : commandSender.getServer().getOnlinePlayers()) {
                    target.add(player.getName());
                }
            }

            if (commandSender instanceof Player player) {
                target.add(player.getTargetBlock(null, 5).getX() + " " + player.getTargetBlock(null, 5).getY() + " " + player.getTargetBlock(null, 5).getZ());
            }

            return target;
        }

        // Player sort
        if (args.length == 2) {
            ArrayList<String> sortTypes = new ArrayList<>();
            for (String sortType : SORT_TYPES) {
                if (sortType.startsWith(args[1])) {
                    sortTypes.add(sortType);
                }
            }

            return sortTypes;
        }

        // Block sort
        if (args.length == 4) {
            ArrayList<String> sortTypes = new ArrayList<>();
            for (String sortType : SORT_TYPES) {
                if (sortType.startsWith(args[3])) {
                    sortTypes.add(sortType);
                }
            }

            return sortTypes;
        }

        return null;
    }

    private void sortPlayerInventory(final UUID playerUUID) {
        sortPlayerInventory(playerUUID, Sorter.config.getString("default_sort_type", "alphabetical"));
    }

    private void sortPlayerInventory(final UUID playerUUID, final String sortType) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;

        PlayerInventory inventory = player.getInventory();
        ItemStack[] items = inventory.getContents();

        // List for non-empty items
        List<ItemStack> itemList = new ArrayList<>();
        for (ItemStack item : items) {
            if (item != null && item.getType() != org.bukkit.Material.AIR) {
                itemList.add(item);
            }
        }

        // Map for the total amount of each item type
        Map<org.bukkit.Material, Integer> itemCountMap = new HashMap<>();

        switch (sortType) {
            case "alphabetical":
                itemList.sort(Comparator.comparing(item -> item.getType().name())); // Alphabetical sorting
                break;

            case "amount":
                // Count the total amount of each item type
                for (ItemStack item : itemList) {
                    itemCountMap.put(item.getType(), itemCountMap.getOrDefault(item.getType(), 0) + item.getAmount());
                }

                // Clear the list to refill it
                itemList.clear();  // Clear the list to refill it
                itemCountMap.entrySet().stream()
                        .sorted(Map.Entry.<org.bukkit.Material, Integer>comparingByValue().reversed())
                        .forEach(entry -> {
                            List<ItemStack> stacks = createItemStacks(entry.getKey(), entry.getValue());
                            itemList.addAll(stacks);  // Add the stacks to the list
                        });
                break;

            case "category":
                itemList.sort(Comparator.comparing(item -> item.getType().getCreativeCategory().name())); // Sort by category
                break;

            case "random":
                Collections.shuffle(itemList); // Random sorting
                break;

            default:
                itemList.sort(Comparator.comparing(item -> item.getType().name())); // Alphabetical sorting
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
    private List<ItemStack> createItemStacks(final org.bukkit.Material material, final int totalAmount) {
        List<ItemStack> stacks = new ArrayList<>();
        int maxStackSize = material.getMaxStackSize();  // Maximum stack size for the material
        int finalTotalAmount = totalAmount;

        // Create stacks with maximum stack size
        while (finalTotalAmount > 0) {
            int stackSize = Math.min(totalAmount, maxStackSize);  // Calculate stack size
            ItemStack stack = new ItemStack(material, stackSize);
            stacks.add(stack);
            finalTotalAmount -= stackSize;
        }

        return stacks;
    }

    private void sortBlockInventory(final CommandSender commandSender, final int x, final int y, final int z) {
        sortBlockInventory(commandSender, x, y, z, Sorter.config.getString("default_sort_type", "alphabetical"));
    }

    private void sortBlockInventory(final CommandSender commandSender, final int x, final int y, final int z, final String sortType) {
        // Check if the commandSender is a player and get the World
        org.bukkit.World world = commandSender instanceof Player player ? player.getWorld() : Bukkit.getWorlds().get(0);

        // Get the block at the specified coordinates
        Block block = world.getBlockAt(x, y, z);

        // Check if the block exists
        if (block.getType() == org.bukkit.Material.AIR) {
            commandSender.sendMessage(ChatColor.RED + Translator.getTranslation("block_not_found"));
            return;
        }

        // Check if the block is an enderchest
        if (block.getType() == org.bukkit.Material.ENDER_CHEST) {
            sortEnderChest(commandSender, sortType);
            return;
        }

        // Check if the block is a container
        if (!(block.getState() instanceof org.bukkit.block.Container container)) {
            commandSender.sendMessage(ChatColor.RED + Translator.getTranslation("not_container"));
            return;
        }

        // Get the inventory of the container
        org.bukkit.inventory.Inventory inventory = container.getInventory();
        ItemStack[] items = inventory.getContents();

        // List for non-empty items
        List<ItemStack> itemList = new ArrayList<>();
        for (ItemStack item : items) {
            if (item != null && item.getType() != org.bukkit.Material.AIR) {
                itemList.add(item);
            }
        }

        // Map for the total amount of each item type
        Map<org.bukkit.Material, Integer> itemCountMap = new HashMap<>();

        switch (sortType) {
            case "alphabetical":
                itemList.sort(Comparator.comparing(item -> item.getType().name())); // Alphabetical sorting
                break;

            case "amount":
                // Count the total amount of each item type
                for (ItemStack item : itemList) {
                    itemCountMap.put(item.getType(), itemCountMap.getOrDefault(item.getType(), 0) + item.getAmount());
                }

                // Clear the list and refill it with sorted stacks
                itemList.clear();
                itemCountMap.entrySet().stream()
                        .sorted(Map.Entry.<org.bukkit.Material, Integer>comparingByValue().reversed())
                        .forEach(entry -> {
                            List<ItemStack> stacks = createItemStacks(entry.getKey(), entry.getValue());
                            itemList.addAll(stacks); // Add the stacks to the list
                        });
                break;

            case "category":
                // Sort by category, fallback to alphabetical if no category is available
                itemList.sort(Comparator.comparing(item -> {
                    if (item.getType().getCreativeCategory() != null) {
                        return item.getType().getCreativeCategory().name();
                    } else {
                        return item.getType().name(); // Fallback to name if no category is available
                    }
                }));
                break;

            case "random":
                Collections.shuffle(itemList); // Random sorting
                break;

            default:
                itemList.sort(Comparator.comparing(item -> item.getType().name())); // Default to alphabetical sorting
        }

        // Fill the inventory with the sorted items by creating a new array of ItemStack
        ItemStack[] sortedItems = new ItemStack[inventory.getSize()];

        for (int i = 0; i < sortedItems.length; i++) {
            if (i < itemList.size()) {
                sortedItems[i] = itemList.get(i); // Fill the inventory slots with sorted items
            } else {
                sortedItems[i] = null; // Clear remaining slots
            }
        }

        // Update the entire inventory in one go
        inventory.setContents(sortedItems);

        commandSender.sendMessage(ChatColor.GREEN + Translator.getTranslation("container_sorted"));
    }

    private void sortEnderChest(final CommandSender commandSender, final String sortType) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(ChatColor.RED + Translator.getTranslation("enderchest_console_error"));
            return;
        }

        // Get the inventory of the container
        org.bukkit.inventory.Inventory inventory = player.getEnderChest();
        ItemStack[] items = inventory.getContents();

        // List for non-empty items
        List<ItemStack> itemList = new ArrayList<>();
        for (ItemStack item : items) {
            if (item != null && item.getType() != org.bukkit.Material.AIR) {
                itemList.add(item);
            }
        }

        // Map for the total amount of each item type
        Map<org.bukkit.Material, Integer> itemCountMap = new HashMap<>();

        switch (sortType) {
            case "alphabetical":
                itemList.sort(Comparator.comparing(item -> item.getType().name())); // Alphabetical sorting
                break;

            case "amount":
                // Count the total amount of each item type
                for (ItemStack item : itemList) {
                    itemCountMap.put(item.getType(), itemCountMap.getOrDefault(item.getType(), 0) + item.getAmount());
                }

                // Clear the list and refill it with sorted stacks
                itemList.clear();
                itemCountMap.entrySet().stream()
                        .sorted(Map.Entry.<org.bukkit.Material, Integer>comparingByValue().reversed())
                        .forEach(entry -> {
                            List<ItemStack> stacks = createItemStacks(entry.getKey(), entry.getValue());
                            itemList.addAll(stacks); // Add the stacks to the list
                        });
                break;

            case "category":
                // Sort by category, fallback to alphabetical if no category is available
                itemList.sort(Comparator.comparing(item -> {
                    if (item.getType().getCreativeCategory() != null) {
                        return item.getType().getCreativeCategory().name();
                    } else {
                        return item.getType().name(); // Fallback to name if no category is available
                    }
                }));
                break;

            case "random":
                Collections.shuffle(itemList); // Random sorting
                break;

            default:
                itemList.sort(Comparator.comparing(item -> item.getType().name())); // Default to alphabetical sorting
        }

        // Fill the inventory with the sorted items by creating a new array of ItemStack
        ItemStack[] sortedItems = new ItemStack[inventory.getSize()];

        for (int i = 0; i < sortedItems.length; i++) {
            if (i < itemList.size()) {
                sortedItems[i] = itemList.get(i); // Fill the inventory slots with sorted items
            } else {
                sortedItems[i] = null; // Clear remaining slots
            }
        }

        // Update the entire inventory in one go
        inventory.setContents(sortedItems);
        player.sendMessage(ChatColor.GREEN + Translator.getTranslation("enderchest_sorted"));
    }
}
