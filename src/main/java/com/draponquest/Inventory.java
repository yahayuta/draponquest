package com.draponquest;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the player's inventory, allowing them to add, remove, and view items.
 */
public class Inventory {
    /**
     * The list of items currently held in the inventory.
     */
    private List<Item> items;

    /**
     * Constructs an empty inventory.
     */
    public Inventory() {
        items = new ArrayList<>();
    }

    /**
     * Adds an item to the inventory.
     * @param item The Item to be added.
     */
    public void addItem(Item item) {
        items.add(item);
    }

    /**
     * Removes a specific item from the inventory.
     * @param item The Item to be removed.
     */
    public void removeItem(Item item) {
        items.remove(item);
    }

    /**
     * Returns the list of items in the inventory.
     * @return A List of Item objects in the inventory.
     */
    public List<Item> getItems() {
        return items;
    }

    @Override
    /**
     * Returns a string representation of the inventory, listing all items and their descriptions.
     * @return A formatted string showing the contents of the inventory.
     */
    public String toString() {
        if (items.isEmpty()) {
            return "Inventory is empty.";
        }
        StringBuilder sb = new StringBuilder("Inventory:\n");
        for (Item item : items) {
            sb.append("- ").append(item.getName()).append(": ").append(item.getDescription()).append("\n");
        }
        return sb.toString();
    }
}
