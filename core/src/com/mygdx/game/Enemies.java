package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class Enemies {
    private TextureAtlas enemyAtlas;
    private TextureRegion enemyTexture;
    private int health;
    private int damage;
    private static final int DEFAULT_HEALTH = 50; // Puntos de vida constantes para todos los enemigos
    private static final int DEFAULT_DAMAGE = 4; // Daño constante para todos los enemigos

    public Enemies() {
        this.enemyAtlas = new TextureAtlas("images/Enemies/Enemies.atlas");
        this.health = DEFAULT_HEALTH;
        this.damage = DEFAULT_DAMAGE;
        randomizeEnemyTexture();
    }

    private void randomizeEnemyTexture() {
        // Suponiendo que el atlas tiene un array o lista de regiones accesible
        Object[] regions = enemyAtlas.getRegions().toArray();
        this.enemyTexture = (TextureRegion) regions[MathUtils.random(regions.length - 1)];
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
}
