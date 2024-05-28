package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Equipment implements Json.Serializable {
    public enum Type {
        WEAPON, ARMOR, ACCESSORY
    }

    private String name;


    private transient AtlasRegion texture;  // Usar AtlasRegion para obtener el nombre de la textura
    private Type type;
    private int vitalityBonus;
    private int strengthBonus;
    private int enduranceBonus;
    private int dexterityBonus;
    private int luckBonus;
    private transient String textureName; // Almacenar el nombre de la textura temporalmente

    // Constructor sin argumentos necesario para la deserialización
    public Equipment() {
        // Este constructor debe existir pero no necesita hacer nada
    }

    public Equipment(String name, AtlasRegion texture, Type type,
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

    // Método de inicialización para asignar texturas después de la deserialización
    public void initialize(TextureAtlas atlas) {
        if (this.textureName != null) {
            this.texture = atlas.findRegion(this.textureName);
            if (this.texture == null) {
                Gdx.app.error("Equipment", "Texture region not found: " + this.textureName);
            }
            this.textureName = null; // Limpiar el nombre de la textura después de usarlo
        }
    }

    // Getters y setters
    public String getName() {
        return name;
    }

    public AtlasRegion getTexture() {
        return texture;
    }
    public void setTexture(AtlasRegion texture) {
        this.texture = texture;
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

    @Override
    public String toString() {
        return "Equipment{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", vitalityBonus=" + vitalityBonus +
                ", strengthBonus=" + strengthBonus +
                ", enduranceBonus=" + enduranceBonus +
                ", dexterityBonus=" + dexterityBonus +
                ", luckBonus=" + luckBonus +
                '}';
    }

    @Override
    public void write(Json json) {
        json.writeValue("name", name);
        json.writeValue("type", type);
        json.writeValue("vitalityBonus", vitalityBonus);
        json.writeValue("strengthBonus", strengthBonus);
        json.writeValue("enduranceBonus", enduranceBonus);
        json.writeValue("dexterityBonus", dexterityBonus);
        json.writeValue("luckBonus", luckBonus);
        // Guardar el nombre de la región de la textura
        if (texture != null) {
            json.writeValue("textureName", texture.name);
        } else {
            json.writeValue("textureName", (Object) null);
        }
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        name = json.readValue("name", String.class, jsonData);
        type = json.readValue("type", Type.class, jsonData);
        if (vitalityBonus == 0) vitalityBonus = 0;
        vitalityBonus = json.readValue("vitalityBonus", int.class, jsonData);
        if (strengthBonus == 0) strengthBonus = 0;
        strengthBonus = json.readValue("strengthBonus", int.class, jsonData);
        if (enduranceBonus == 0) enduranceBonus = 0;
        enduranceBonus = json.readValue("enduranceBonus", int.class, jsonData);
        if (dexterityBonus == 0) dexterityBonus = 0;
        dexterityBonus = json.readValue("dexterityBonus", int.class, jsonData);
        if (luckBonus == 0) luckBonus = 0;
        luckBonus = json.readValue("luckBonus", int.class, jsonData);
        // Leer el nombre de la región de la textura
        textureName = json.readValue("textureName", String.class, jsonData);
    }
}
