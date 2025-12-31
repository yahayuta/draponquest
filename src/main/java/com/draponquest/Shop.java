package com.draponquest;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a shop where the player can buy and sell items.
 */
public class Shop {
    /**
     * A list of items currently available for purchase in the shop.
     */
    private List<Item> itemsForSale;

    /**
     * Constructs a new Shop instance and initializes its inventory with default items.
     */
    public Shop() {
        itemsForSale = new ArrayList<>();
        // Default items, can be configured from DraponQuestFX
        itemsForSale.add(new Item("Potion", "Restores 20 HP", "heal_20", 20));
        itemsForSale.add(new Item("Herb", "Restores 10 HP", "heal_10", 10));
    }

    /**
     * Returns the list of items currently available for sale in the shop.
     * @return A List of Item objects for sale.
     */
    public List<Item> getItemsForSale() {
        return itemsForSale;
    }

    /**
     * Attempts to buy an item from the shop.
     * The player's gold is checked, and if sufficient, the item is added to their inventory.
     * @param item The Item the player wishes to buy.
     * @param game The main game instance, providing access to player's gold and inventory.
     * @return A String message indicating the result of the purchase attempt.
     */
    public String buyItem(Item item, DraponQuestFX game) {
        if (item == null) {
            return "No item selected to buy.";
        }
        if (game.playerGold >= item.getValue()) {
            game.playerGold -= item.getValue();
            game.getInventory().addItem(item);
            return "You bought a " + item.getName() + ".";
        } else {
            return "Not enough gold.";
        }
    }

    /**
     * Allows the player to sell an item to the shop.
     * The item is removed from the player's inventory, and half its value is added to their gold.
     * @param item The Item the player wishes to sell.
     * @param game The main game instance, providing access to player's gold and inventory.
     * @return A String message indicating the result of the sale.
     */
    public String sellItem(Item item, DraponQuestFX game) {
        if (item == null) {
            return "No item selected to sell.";
        }
        int sellPrice = item.getValue() / 2;
        game.playerGold += sellPrice;
        game.getInventory().removeItem(item);
        return "You sold the " + item.getName() + " for " + sellPrice + " gold.";
    }
}
