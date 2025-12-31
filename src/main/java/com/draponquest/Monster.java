package com.draponquest;

import javafx.scene.image.Image;

/**
 * Represents a monster entity in the game, including its stats, image, and drops.
 */
public class Monster {
    /**
     * The visual representation (image) of the monster.
     */
    Image image;
    /**
     * The name of the monster.
     */
    String name;
    /**
     * The maximum hit points of the monster.
     */
    int maxHP;
    /**
     * The attack power of the monster.
     */
    int attack;
    /**
     * The defense power of the monster.
     */
    int defense;
    /**
     * The experience points awarded to the player for defeating this monster.
     */
    int xpValue;
    /**
     * The amount of gold dropped by the monster when defeated.
     */
    int goldValue;
    /**
     * The item that this monster might drop.
     */
    Item itemDrop;
    /**
     * The probability (0.0 to 1.0) of the monster dropping its itemDrop.
     */
    double dropChance;

    /**
     * Constructs a new Monster instance.
     * @param image The image representing the monster.
     * @param name The name of the monster.
     * @param maxHP The maximum hit points of the monster.
     * @param attack The attack power of the monster.
     * @param defense The defense power of the monster.
     * @param xpValue The experience points awarded for defeating this monster.
     * @param goldValue The amount of gold dropped by this monster.
     * @param itemDrop The item that this monster might drop.
     * @param dropChance The probability (0.0 to 1.0) of the itemDrop.
     */
    public Monster(Image image, String name, int maxHP, int attack, int defense, int xpValue, int goldValue, Item itemDrop, double dropChance) {
        this.image = image;
        this.name = name;
        this.maxHP = maxHP;
        this.attack = attack;
        this.defense = defense;
        this.xpValue = xpValue;
        this.goldValue = goldValue;
        this.itemDrop = itemDrop;
        this.dropChance = dropChance;
    }
}
