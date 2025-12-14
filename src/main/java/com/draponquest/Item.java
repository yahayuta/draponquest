package com.draponquest;

public class Item {
    private String name;
    private String description;
    private String effect;
    private int value;

    public Item(String name, String description, String effect, int value) {
        this.name = name;
        this.description = description;
        this.effect = effect;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getEffect() {
        return effect;
    }

    public int getValue() {
        return value;
    }
}
