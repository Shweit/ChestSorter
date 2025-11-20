package com.shweit.chestsorter.sorting;

/**
 * Enum representing different sorting types for inventory items.
 */
public enum SortType {
  /** Sort items alphabetically by material name. */
  NAME,

  /** Sort items by stack quantity. */
  QUANTITY,

  /** Sort items by material type grouping. */
  MATERIAL_TYPE,

  /** Sort items by durability (for tools/armor). */
  DURABILITY;

  /**
   * Get SortType from string value.
   *
   * @param value The string representation
   * @return The matching SortType, or null if invalid
   */
  public static SortType fromString(String value) {
    if (value == null) {
      return null;
    }
    try {
      return valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      return null;
    }
  }
}
