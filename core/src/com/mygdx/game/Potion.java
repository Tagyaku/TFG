package com.mygdx.game;

public class Potion {
    public enum PotionType {
        HEAL_30, HEAL_100
    }

    private PotionType type;

    public Potion(PotionType type) {
        this.type = type;
    }

    public PotionType getType() {
        return type;
    }
}
