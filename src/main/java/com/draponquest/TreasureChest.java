package com.draponquest;

/**
 * Represents a treasure chest in the game, which can hold an item and be opened by the player.
 */
public class TreasureChest {
    /**
     * The item contained within this treasure chest.
     */
    private Item content;
    /**
     * Flag indicating whether the treasure chest has already been opened.
     */
    private boolean isOpen;
    /**
     * The X-coordinate of the treasure chest's location on the map.
     */
    private int x;
    /**
     * The Y-coordinate of the treasure chest's location on the map.
     */
    private int y;
    /**
     * The ID of the place (e.g., field, cave) where this treasure chest is located.
     */
    private int placeID;

    /**
     * Constructs a new TreasureChest.
     * @param content The Item contained within the chest.
     * @param x The X-coordinate of the chest.
     * @param y The Y-coordinate of the chest.
     * @param placeID The ID of the place where the chest is located.
     */
    public TreasureChest(Item content, int x, int y, int placeID) {
        this.content = content;
        this.isOpen = false;
        this.x = x;
        this.y = y;
        this.placeID = placeID;
    }

    /**
     * Checks if the treasure chest has already been opened.
     * @return True if the chest is open, false otherwise.
     */
    public boolean isOpen() {
        return isOpen;
    }

    /**
     * Opens the treasure chest, marks it as opened, and returns its content.
     * If the chest is already open, it returns null.
     * @return The Item contained in the chest, or null if already opened.
     */
    public Item open() {
        if (isOpen) {
            return null; // Already opened
        }
        isOpen = true;
        return content;
    }

    /**
     * Returns the X-coordinate of the treasure chest.
     * @return The X-coordinate.
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the Y-coordinate of the treasure chest.
     * @return The Y-coordinate.
     */
    public int getY() {
        return y;
    }

    /**
     * Returns the place ID where the treasure chest is located.
     * @return The place ID.
     */
    public int getPlaceID() {
        return placeID;
    }
}
