package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class Enemies {
    private TextureAtlas enemyAtlas;
    private TextureRegion enemyTexture;
    private String textureName; // Almacenar el nombre de la textura actual
    private int health;
    private int damage;
    public static final int DEFAULT_HEALTH = 50; // Puntos de vida constantes para todos los enemigos
    private static final int DEFAULT_DAMAGE = 4; // Daño constante para todos los enemigos

    public Enemies() {
        this.enemyAtlas = new TextureAtlas("images/Enemies/Enemies.atlas");
        this.health = DEFAULT_HEALTH;
        this.damage = DEFAULT_DAMAGE;
        randomizeEnemyTexture();
    }

    private void randomizeEnemyTexture() {
        // Convertir a un array de AtlasRegion para acceder a los nombres
        AtlasRegion[] regions = enemyAtlas.getRegions().toArray(AtlasRegion.class);
        AtlasRegion selectedRegion = regions[MathUtils.random(regions.length - 1)];
        this.enemyTexture = selectedRegion;
        this.textureName = selectedRegion.name; // Almacena el nombre de la textura
    }

    public void attackPlayer(Player player) {
        player.receiveDamage(damage);
    }

    public void receiveDamage(int amount) {
        this.health -= amount;
        if (this.health <= 0) {
            die();
        }
    }

    private void die() {
        // Elimina al enemigo de la pantalla, resetea o lo que sea necesario
        System.out.println("Enemy has died.");
    }

    public boolean isAlive() {
        return this.health > 0;
    }

    // Getters para acceso desde otros métodos o clases
    public TextureRegion getEnemyTexture() {
        return enemyTexture;
    }

    public int getHealth() {
        return health;
    }

    public int getDamage() {
        return damage;
    }

    // Nuevo método para obtener el nombre de la textura del enemigo
    public String getTextureName() {
        return textureName;
    }
}
