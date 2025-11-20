package com.shweit.chestsorter.sorting;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

/**
 * Handles sorting of inventories based on different criteria.
 */
public class InventorySorter {

  /**
   * Sorts an inventory based on the specified sort type and order.
   *
   * @param inventory The inventory to sort
   * @param sortType The type of sorting to apply
   * @param sortOrder The order to sort in (ASC/DESC)
   */
  public void sortInventory(Inventory inventory, SortType sortType, SortOrder sortOrder) {
    List<ItemStack> items = new ArrayList<>();

    // Collect all non-null items
    ItemStack[] contents = inventory.getContents();
    if (contents == null) {
      return;
    }

    for (ItemStack item : contents) {
      if (item != null && item.getType() != Material.AIR) {
        items.add(item.clone());
      }
    }

    // Sort based on type
    Comparator<ItemStack> comparator = getComparator(sortType);

    // Reverse for descending order
    if (sortOrder == SortOrder.DESC) {
      comparator = comparator.reversed();
    }

    items.sort(comparator);

    // Clear inventory and repopulate
    inventory.clear();
    for (int i = 0; i < items.size() && i < inventory.getSize(); i++) {
      inventory.setItem(i, items.get(i));
    }
  }

  /**
   * Gets the appropriate comparator for the sort type.
   *
   * @param sortType The sort type
   * @return The comparator for that sort type
   */
  private Comparator<ItemStack> getComparator(SortType sortType) {
    return switch (sortType) {
      case NAME -> Comparator.comparing(item -> item.getType().name());
      case QUANTITY -> Comparator.comparingInt(ItemStack::getAmount);
      case MATERIAL_TYPE -> Comparator
          .comparing((ItemStack item) -> getMaterialCategory(item.getType()))
          .thenComparing(item -> item.getType().name());
      case DURABILITY -> Comparator
          .comparingInt(this::getDurabilityValue)
          .thenComparing(item -> item.getType().name());
    };
  }

  /**
   * Gets a category string for a material to group similar items.
   *
   * @param material The material to categorize
   * @return A category string
   */
  private String getMaterialCategory(Material material) {
    if (material.isBlock()) {
      // Group blocks by material properties
      if (isOre(material)) {
        return "A_ORE";
      } else if (isWood(material)) {
        return "B_WOOD";
      } else if (isStone(material)) {
        return "C_STONE";
      } else if (material.name().contains("GLASS")) {
        return "D_GLASS";
      } else if (material.name().contains("WOOL") || material.name().contains("CARPET")) {
        return "E_WOOL";
      } else if (material.name().contains("CONCRETE")) {
        return "F_CONCRETE";
      } else {
        return "G_BLOCK";
      }
    } else if (material.isEdible()) {
      return "H_FOOD";
    } else if (isTool(material)) {
      return "I_TOOL";
    } else if (isArmor(material)) {
      return "J_ARMOR";
    } else if (isWeapon(material)) {
      return "K_WEAPON";
    } else {
      return "Z_OTHER";
    }
  }

  /**
   * Checks if a material is an ore.
   *
   * @param material The material to check
   * @return true if it's an ore
   */
  private boolean isOre(Material material) {
    String name = material.name();
    return name.contains("_ORE") || name.contains("RAW_");
  }

  /**
   * Checks if a material is wood-related.
   *
   * @param material The material to check
   * @return true if it's wood
   */
  private boolean isWood(Material material) {
    String name = material.name();
    return name.contains("_LOG") || name.contains("_WOOD") || name.contains("_PLANKS");
  }

  /**
   * Checks if a material is stone-related.
   *
   * @param material The material to check
   * @return true if it's stone
   */
  private boolean isStone(Material material) {
    String name = material.name();
    return name.contains("STONE") || name.contains("COBBLESTONE")
        || name.contains("ANDESITE") || name.contains("DIORITE") || name.contains("GRANITE");
  }

  /**
   * Checks if a material is a tool.
   *
   * @param material The material to check
   * @return true if it's a tool
   */
  private boolean isTool(Material material) {
    String name = material.name();
    return name.contains("_PICKAXE") || name.contains("_AXE")
        || name.contains("_SHOVEL") || name.contains("_HOE");
  }

  /**
   * Checks if a material is armor.
   *
   * @param material The material to check
   * @return true if it's armor
   */
  private boolean isArmor(Material material) {
    String name = material.name();
    return name.contains("_HELMET") || name.contains("_CHESTPLATE")
        || name.contains("_LEGGINGS") || name.contains("_BOOTS");
  }

  /**
   * Checks if a material is a weapon.
   *
   * @param material The material to check
   * @return true if it's a weapon
   */
  private boolean isWeapon(Material material) {
    String name = material.name();
    return name.contains("_SWORD") || name.equals("BOW")
        || name.equals("CROSSBOW") || name.equals("TRIDENT");
  }

  /**
   * Gets the durability value for an item (for sorting).
   * Items without durability get max value.
   *
   * @param item The item to check
   * @return The remaining durability, or Integer.MAX_VALUE if not applicable
   */
  private int getDurabilityValue(ItemStack item) {
    if (item.getItemMeta() instanceof Damageable damageable) {
      Material type = item.getType();
      int maxDurability = type.getMaxDurability();
      if (maxDurability > 0) {
        return maxDurability - damageable.getDamage();
      }
    }
    return Integer.MAX_VALUE;
  }
}
