package com.draponquest;

public class TreasureChest {
    private Item content;
    private boolean isOpen;
    private int x;
    private int y;
    private int placeID;

    public TreasureChest(Item content, int x, int y, int placeID) {
        this.content = content;
        this.isOpen = false;
        this.x = x;
        this.y = y;
        this.placeID = placeID;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public Item open() {
        if (isOpen) {
            return null; // Already opened
        }
        isOpen = true;
        return content;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getPlaceID() {
        return placeID;
    }
}
