package com.shweit.chestsorter.sorting;

/**
 * Enum representing the sort order direction.
 */
public enum SortOrder {
  /** Ascending order (A-Z, 0-9, low to high). */
  ASC,

  /** Descending order (Z-A, 9-0, high to low). */
  DESC;

  /**
   * Get SortOrder from string value.
   *
   * @param value The string representation
   * @return The matching SortOrder, or null if invalid
   */
  public static SortOrder fromString(String value) {
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
