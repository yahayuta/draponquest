package com.draponquest;

import javafx.scene.image.Image;

public class Monster {
    Image image;
    String name;
    int maxHP;
    int attack;
    int defense;
    int xpValue;
    int goldValue;

    public Monster(Image image, String name, int maxHP, int attack, int defense, int xpValue, int goldValue) {
        this.image = image;
        this.name = name;
        this.maxHP = maxHP;
        this.attack = attack;
        this.defense = defense;
        this.xpValue = xpValue;
        this.goldValue = goldValue;
    }
}
