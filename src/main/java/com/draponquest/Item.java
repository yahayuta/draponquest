package com.draponquest;

/**
 * Represents an item in the game, with properties such as name, description, effect, and value.
 */
public class Item {
    /**
     * The name of the item.
     */
    private String name;
    /**
     * A brief description of the item.
     */
    private String description;
    /**
     * Describes the effect of using the item (e.g., "heal_20", "cure_poison").
     */
    private String effect;
    /**
     * The monetary value of the item.
     */
    private int value;

    /**
     * Constructs a new Item instance.
     * @param name The name of the item.
     * @param description A brief description of the item.
     * @param effect Describes the effect of using the item.
     * @param value The monetary value of the item.
     */
    public Item(String name, String description, String effect, int value) {
        this.name = name;
        this.description = description;
        this.effect = effect;
        this.value = value;
    }

    /**
     * Returns the name of the item.
     * @return The item's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a brief description of the item.
     * @return The item's description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the effect description of the item.
     * @return The item's effect.
     */
    public String getEffect() {
        return effect;
    }

    /**
     * Returns the monetary value of the item.
     * @return The item's value.
     */
    public int getValue() {
        return value;
    }
}
