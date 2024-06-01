package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;
import java.util.List;

public class Enemies {
    private TextureAtlas enemyAtlas;
    private TextureRegion enemyTexture;
    private String textureName;
    private int health;
    private int maxHealth; // Vida máxima del enemigo
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
    private boolean shouldRotateEnemy;
    private List<String> enemyTextures;
    private MyGdxGame game;
    private Combat combat;

    public Enemies(MyGdxGame game, Combat combat, int contCombat) {
        this.game = game;
        this.combat = combat;
        this.enemyAtlas = new TextureAtlas("images/Enemies/Enemies.atlas");
        this.maxHealth=50+contCombat*130;
        this.health = 50+contCombat*130;
        this.damage = 4+contCombat*10;
        setEnemyTexture(contCombat);
    }
public void enemyTextures() {

}
    private void setEnemyTexture(int contCombat) {
        // Convertir a un array de AtlasRegion para acceder a los nombres
        //AtlasRegion[] regions = enemyAtlas.getRegions().toArray(AtlasRegion.class);
        enemyTextures = new ArrayList<>();
        enemyTextures.add("Zodiac Leo");
        enemyTextures.add("Halloween Stein's Monster MK2");
        enemyTextures.add("Earth Gemstone Golem");
        enemyTextures.add("Boss Astral Lich");
        enemyTextures.add("Darkness Lord Knight");

        //AtlasRegion selectedRegion = regions[MathUtils.random(regions.length - 1)];
        TextureRegion selectedRegion = enemyAtlas.findRegion(enemyTextures.get(contCombat));
        this.enemyTexture = selectedRegion;
        this.textureName = ((AtlasRegion) selectedRegion).name; // Almacena el nombre de la textura
        this.shouldRotateEnemy = ((AtlasRegion) selectedRegion).rotate;  // Set rotation based on the selected region

    }
/*    public void setEnemyTexture(String textureName) {
        AtlasRegion region = enemyAtlas.findRegion(textureName);
        if (region != null) {
            this.enemyTexture = region;
            this.textureName = textureName;
            this.shouldRotateEnemy = region.rotate;
        }
    }*/
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

    public TextureRegion getEnemyTexture() {
        return enemyTexture;
    }

    public int getHealth() {
        return health;
    }

    public int getDamage() {
        return damage;
    }

    public String getTextureName() {
        return textureName;
    }
    public boolean shouldRotate() {
        return shouldRotateEnemy;
    }
}
