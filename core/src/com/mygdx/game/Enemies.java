package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class Enemies {
    private TextureAtlas enemyAtlas;
    private TextureRegion enemyTexture;
    private String textureName;
    private int health;
    private int maxHealth = 50; // Vida máxima del enemigo
    private int damage;

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }
    private boolean shouldRotateEnemy; // Declare here without initializing

    private MyGdxGame game;
    private Combat combat;

    public Enemies(MyGdxGame game, Combat combat) {
        this.game = game;
        this.combat = combat;
        this.enemyAtlas = new TextureAtlas("images/Enemies/Enemies.atlas");
        this.health = maxHealth;
        this.damage = 4; // Daño constante para todos los enemigos
        randomizeEnemyTexture();
    }

    private void randomizeEnemyTexture() {
        // Convertir a un array de AtlasRegion para acceder a los nombres
        AtlasRegion[] regions = enemyAtlas.getRegions().toArray(AtlasRegion.class);
        if (regions.length > 0) {
        AtlasRegion selectedRegion = regions[MathUtils.random(regions.length - 1)];
        this.enemyTexture = selectedRegion;
        this.textureName = selectedRegion.name; // Almacena el nombre de la textura
            this.shouldRotateEnemy = selectedRegion.rotate;  // Set rotation based on the selected region
        }
    }

    public void attackPlayer(Player player) {
        player.receiveDamage(damage);
    }

    public void receiveDamage(int amount) {
        this.health -= amount;
        if (this.health <= 0) {
            this.health = 0;
            die();
        }
    }

    private void die() {
        System.out.println("Enemy has died.");
        combat.showVictoryDialog(); // Llama a la función en Combat para mostrar el diálogo de victoria
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
    public boolean shouldRotate() {
        return shouldRotateEnemy;
    }
}
