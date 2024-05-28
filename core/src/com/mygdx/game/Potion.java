package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Potion {
    public enum PotionType {
        HEAL_30, HEAL_100
    }

    private PotionType type;
    private transient TextureRegion texture;
    private String textureName;

    public Potion(PotionType type, TextureRegion texture) {
        this.type = type;
        this.texture = texture;
        this.textureName = texture != null ? texture.toString() : null; // Asigna el nombre de la textura
    }

    public PotionType getType() {
        return type;
    }

    public TextureRegion getTexture() {
        return texture;
    }

    public void setTexture(TextureRegion texture) {
        this.texture = texture;
        this.textureName = texture != null ? texture.toString() : null; // Actualiza el nombre de la textura
    }

    public void initialize(TextureAtlas atlas) {
        if (this.textureName != null) {
            this.texture = atlas.findRegion(this.textureName);
        }
    }
}
