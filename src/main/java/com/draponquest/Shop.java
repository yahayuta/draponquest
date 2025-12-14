package com.draponquest;

import java.util.ArrayList;
import java.util.List;

public class Shop {
    private List<Item> items;

    public Shop() {
        items = new ArrayList<>();
        items.add(new Item("Potion", "Restores 20 HP", "heal_20", 10));
    }

    public List<Item> getItems() {
        return items;
    }
}
