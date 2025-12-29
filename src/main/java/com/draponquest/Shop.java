package com.draponquest;

import java.util.ArrayList;
import java.util.List;

public class Shop {
    private List<Item> itemsForSale;

    public Shop() {
        itemsForSale = new ArrayList<>();
        // Default items, can be configured from DraponQuestFX
        itemsForSale.add(new Item("Potion", "Restores 20 HP", "heal_20", 20));
        itemsForSale.add(new Item("Herb", "Restores 10 HP", "heal_10", 10));
    }

    public List<Item> getItemsForSale() {
        return itemsForSale;
    }

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
