package com.shweit.sorter.commands;

import com.shweit.sorter.util.Translator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

// /sortcontainer <x> <y> <z> [sortType] [sortOrder]
public final class SortContainerCommand implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(
        @NotNull final CommandSender commandSender,
        @NotNull final Command command,
        @NotNull final String s,
        @NotNull final String[] args
    ) {
        if (args.length < 3) {
            commandSender.sendMessage(ChatColor.RED + "Invalid number of arguments");
            commandSender.sendMessage(ChatColor.RED + "Usage: /sortcontainer <x> <y> <z> [sortType] [sortOrder]");
            return false;
        }

        int x;
        int y;
        int z;

        try {
            x = Integer.parseInt(args[0]);
            y = Integer.parseInt(args[1]);
            z = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            commandSender.sendMessage(ChatColor.RED + "Invalid coordinates");
            return false;
        }

        String sortType = args.length > 3 ? args[3].toLowerCase() : "alphabetical";
        String sortOrder = args.length > 4 ? args[4].toLowerCase() : "asc";

        if (!sortType.equals("alphabetical") && !sortType.equals("quantity") && !sortType.equals("category") && !sortType.equals("random")) {
            commandSender.sendMessage(ChatColor.RED + "Invalid sort type");
            return false;
        }

        if (!sortOrder.equals("asc") && !sortOrder.equals("desc")) {
            commandSender.sendMessage(ChatColor.RED + "Invalid sort order");
            return false;
        }

        sortBlockInventory(commandSender, x, y, z, sortType, sortOrder);

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
            if (commandSender instanceof Player player) {
                Block targetBlock = player.getTargetBlock(null, 5);

                return List.of(
                    String.valueOf(targetBlock.getX()),
                    String.valueOf(targetBlock.getX()) + " "
                        + String.valueOf(targetBlock.getY()),
                    String.valueOf(targetBlock.getX()) + " "
                        + String.valueOf(targetBlock.getY()) + " "
                        + String.valueOf(targetBlock.getZ())
                );
            }
        }

        if (args.length == 2) {
            if (commandSender instanceof Player player) {
                Block targetBlock = player.getTargetBlock(null, 5);

                return List.of(
                    String.valueOf(targetBlock.getY()),
                    String.valueOf(targetBlock.getY()) + " " + String.valueOf(targetBlock.getZ())
                );
            }
        }

        if (args.length == 3) {
            if (commandSender instanceof Player player) {
                Block targetBlock = player.getTargetBlock(null, 5);

                return List.of(String.valueOf(targetBlock.getZ()));
            }
        }

        if (args.length == 4) {
            return List.of("alphabetical", "quantity", "category", "random");
        }

        if (args.length == 5) {
            if (!args[4].equals("default") && !args[4].equals("random")) {
                return List.of("ASC", "DESC");
            }
        }

        return List.of();
    }

    private void sortBlockInventory(final CommandSender commandSender, final int x, final int y, final int z, final String sortType, final String sortOrder) {
        // Get the world from the command sender or default to the first world
        org.bukkit.World world = commandSender instanceof Player player ? player.getWorld() : Bukkit.getWorlds().get(0);

        // Get the block at the specified coordinates
        Block block = world.getBlockAt(x, y, z);

        // Check if the block exists
        if (block.getType() == org.bukkit.Material.AIR) {
            commandSender.sendMessage(ChatColor.RED + Translator.getTranslation("block_not_found"));
            return;
        }

        // Check if the block is an ender chest
        if (block.getType() == org.bukkit.Material.ENDER_CHEST) {
            sortEnderChest(commandSender, sortType, sortOrder);
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

        // Create a list of non-empty items
        List<ItemStack> itemList = new ArrayList<>();
        for (ItemStack item : items) {
            if (item != null && item.getType() != org.bukkit.Material.AIR) {
                itemList.add(item);
            }
        }

        // Sort the items based on the provided sortType and sortOrder
        sortItems(itemList, sortType, sortOrder);

        // Fill the inventory with the sorted items
        ItemStack[] sortedItems = new ItemStack[inventory.getSize()];
        for (int i = 0; i < sortedItems.length; i++) {
            if (i < itemList.size()) {
                sortedItems[i] = itemList.get(i);
            } else {
                sortedItems[i] = null;
            }
        }

        // Update the entire inventory
        inventory.setContents(sortedItems);
        commandSender.sendMessage(ChatColor.GREEN + Translator.getTranslation("container_sorted"));
    }

    private void sortEnderChest(final CommandSender commandSender, final String sortType, final String sortOrder) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(ChatColor.RED + Translator.getTranslation("enderchest_console_error"));
            return;
        }

        // Get the Ender Chest inventory
        org.bukkit.inventory.Inventory inventory = player.getEnderChest();
        ItemStack[] items = inventory.getContents();

        // Create a list of non-empty items
        List<ItemStack> itemList = new ArrayList<>();
        for (ItemStack item : items) {
            if (item != null && item.getType() != org.bukkit.Material.AIR) {
                itemList.add(item);
            }
        }

        // Sort the items based on the provided sortType and sortOrder
        sortItems(itemList, sortType, sortOrder);

        // Fill the inventory with the sorted items
        ItemStack[] sortedItems = new ItemStack[inventory.getSize()];
        for (int i = 0; i < sortedItems.length; i++) {
            if (i < itemList.size()) {
                sortedItems[i] = itemList.get(i);
            } else {
                sortedItems[i] = null;
            }
        }

        // Update the Ender Chest inventory
        inventory.setContents(sortedItems);
        player.sendMessage(ChatColor.GREEN + Translator.getTranslation("enderchest_sorted"));
    }

    private void sortItems(final List<ItemStack> itemList, final String sortType, final String sortOrder) {
        boolean ascending = sortOrder.equalsIgnoreCase("asc");

        switch (sortType.toLowerCase()) {
            case "alphabetical":
                itemList.sort(Comparator.comparing(item -> item.getType().name()));
                if (!ascending) {
                    Collections.reverse(itemList);
                }
                break;

            case "quantity":
                Map<Material, Integer> itemCountMap = new HashMap<>();
                for (ItemStack item : itemList) {
                    itemCountMap.put(item.getType(), itemCountMap.getOrDefault(item.getType(), 0) + item.getAmount());
                }

                itemList.clear();
                itemCountMap.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue())
                        .forEach(entry -> {
                            List<ItemStack> stacks = createItemStacks(entry.getKey(), entry.getValue());
                            itemList.addAll(stacks);
                        });

                if (!ascending) {
                    Collections.reverse(itemList);
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
                    Collections.reverse(itemList);
                }
                break;

            case "random":
                Collections.shuffle(itemList);
                break;

            default:
                itemList.sort(Comparator.comparing(item -> item.getType().name()));
                if (!ascending) {
                    Collections.reverse(itemList);
                }
                break;
        }
    }

    // Helper method to create ItemStacks based on total amount
    private List<ItemStack> createItemStacks(final org.bukkit.Material material, final int totalAmount) {
        List<ItemStack> stacks = new ArrayList<>();
        int maxStackSize = material.getMaxStackSize();
        int remainingAmount = totalAmount;

        // Create stacks with maximum stack size
        while (remainingAmount > 0) {
            int stackSize = Math.min(remainingAmount, maxStackSize);
            ItemStack stack = new ItemStack(material, stackSize);
            stacks.add(stack);
            remainingAmount -= stackSize;
        }

        return stacks;
    }

}
