package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Equipment {
    public enum Type {
        WEAPON, ARMOR, ACCESSORY
    }

    private String name;
    private TextureRegion texture;
    private Type type;
    private int vitalityBonus;
    private int strengthBonus;
    private int enduranceBonus;
    private int dexterityBonus;
    private int luckBonus;

    public Equipment(String name, TextureRegion texture, Type type,
                     int vitalityBonus, int strengthBonus, int enduranceBonus,
                     int dexterityBonus, int luckBonus) {
        this.name = name;
        this.texture = texture;
        this.type = type;
        this.vitalityBonus = vitalityBonus;
        this.strengthBonus = strengthBonus;
        this.enduranceBonus = enduranceBonus;
        this.dexterityBonus = dexterityBonus;
        this.luckBonus = luckBonus;
    }

    // Getters y setters
    public String getName() {
        return name;
    }

    public TextureRegion getTexture() {
        return texture;
    }

    public Type getType() {
        return type;
    }

    public int getVitalityBonus() {
        return vitalityBonus;
    }

    public int getStrengthBonus() {
        return strengthBonus;
    }

    public int getEnduranceBonus() {
        return enduranceBonus;
    }

    public int getDexterityBonus() {
        return dexterityBonus;
    }

    public int getLuckBonus() {
        return luckBonus;
    }


}
