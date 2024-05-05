package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
    private String specialAbility;

    public Equipment(String name, TextureAtlas atlas, String regionName, Type type,
                     int vitalityBonus, int strengthBonus, int enduranceBonus,
                     int dexterityBonus, int luckBonus, String specialAbility) {
        this.name = name;
        this.texture = atlas.findRegion(regionName);
        this.type = type;
        this.vitalityBonus = vitalityBonus;
        this.strengthBonus = strengthBonus;
        this.enduranceBonus = enduranceBonus;
        this.dexterityBonus = dexterityBonus;
        this.luckBonus = luckBonus;
        this.specialAbility = specialAbility;
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

    public String getSpecialAbility() {
        return specialAbility;
    }
}
