package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;

public class Player {
    private static Player instance;
    private String playerName;
    private int level;
    private int experience;
    private int baseVitality;
    private int baseStrength;
    private int baseEndurance;
    private int baseDexterity;
    private int baseLuck;
    private int hitPoints;
    private double criticalChance;
    private double criticalDamage;
    private Equipment weapon;
    private Equipment[] accessories = new Equipment[4];
    private Equipment armor;
    private transient TextureAtlas playerAtlas;
    private boolean isDefending = false;
    private boolean shouldRotatePlayer;
    private Inventory inventory;
    private int vitality;
    private int strength;
    private int endurance;
    private int dexterity;
    private int luck;
    private int currentTextIndex;
    private transient MyGdxGame game;

    public Player() {}

    private Player(MyGdxGame game) {
        this.game = game;
        this.playerName = "Default Hero";
        this.level = 1;
        this.experience = 5;
        this.baseVitality = 0;
        this.baseStrength = 5;
        this.baseEndurance = 5;
        this.baseDexterity = 5;
        this.baseLuck = 5;
        this.playerAtlas = new TextureAtlas("images/Main_Character/main_Character.atlas");
        this.inventory = new Inventory();
        updateStats();
        this.hitPoints = getMaxHitPoints();
        setRotation();
    }
    public String toJson() {
        Json json = new Json();
        return json.toJson(this);
    }
    public static Player fromJson(String jsonData, MyGdxGame game) {
        Json json = new Json();
        Player player = json.fromJson(Player.class, jsonData);
        player.initialize(game); // Initialize transient fields
        return player;
    }

    private void setRotation() {
        if (!playerAtlas.getRegions().isEmpty()) {
            this.shouldRotatePlayer = playerAtlas.getRegions().first().rotate;
        }
    }
    private void initializeEquipment() {
        if (this.weapon != null) {
            this.weapon.initialize(playerAtlas.findRegion(this.weapon.getTexture().name));
        }
        if (this.armor != null) {
            this.armor.initialize(playerAtlas.findRegion(this.armor.getTexture().name));
        }
        for (int i = 0; i < this.accessories.length; i++) {
            if (this.accessories[i] != null) {
                this.accessories[i].initialize(playerAtlas.findRegion(this.accessories[i].getTexture().name));
            }
        }
    }

    public static synchronized Player getInstance(MyGdxGame game) {
        if (instance == null) {
            instance = new Player(game);
        }
        return instance;
    }

    public void initialize(MyGdxGame game) {
        this.game = game;
        this.playerAtlas = new TextureAtlas("images/Main_Character/main_Character.atlas");
        initializeEquipment();
    }
    public TextureAtlas getPlayerAtlas() {
        return playerAtlas;
    }

    public void gainExperience(int amount) {
        this.experience += amount;
        checkLevelUp();
    }

    private void checkLevelUp() {
        while (this.experience >= 100 * this.level) {
            this.experience -= 100 * this.level;
            this.level++;
            this.baseVitality += 5;
            this.baseStrength += 5;
            this.baseEndurance += 5;
            this.baseDexterity += 5;
            this.baseLuck += 1;
            updateStats();
            adjustCurrentHP();
        }
    }


    public void equip(Equipment item) {
        unequip(item);

        switch (item.getType()) {
            case WEAPON:
                if (this.weapon != null) unequip(this.weapon);
                this.weapon = item;
                break;
            case ARMOR:
                if (this.armor != null) unequip(this.armor);
                this.armor = item;
                break;
            case ACCESSORY:
                for (int i = 0; i < this.accessories.length; i++) {
                    if (this.accessories[i] == null) {
                        this.accessories[i] = item;
                        break;
                    }
                }
                break;
        }
                        updateStats();
        adjustCurrentHP();
    }
    public void reset() {
        this.playerName = "Default Hero";
        this.level = 1;
        this.experience = 0;
        this.baseVitality = 0;
        this.baseStrength = 5;
        this.baseEndurance = 5;
        this.baseDexterity = 5;
        this.baseLuck = 5;
        this.hitPoints = getMaxHitPoints();
        this.weapon = null;
        this.armor = null;
        for (int i = 0; i < this.accessories.length; i++) {
            this.accessories[i] = null;
        }
        this.inventory.clear();
        updateStats();
        this.currentTextIndex = 0;
    }
    public void unequip(Equipment item) {
        if (item == this.weapon) {
            this.weapon = null;
        } else if (item == this.armor) {
            this.armor = null;
        } else {
            for (int i = 0; i < this.accessories.length; i++) {
                if (this.accessories[i] == item) {
                    this.accessories[i] = null;
                    break;
                }
            }
        }
        updateStats();
        adjustCurrentHP();
    }

    private void adjustCurrentHP() {
        int newMaxHP = getMaxHitPoints();
        if (this.hitPoints > newMaxHP) {
            this.hitPoints = newMaxHP;
        }
    }

    private void updateStats() {
        // Reset stats
        this.vitality = this.baseVitality;
        this.strength = this.baseStrength;
        this.endurance = this.baseEndurance;
        this.dexterity = this.baseDexterity;
        this.luck = this.baseLuck;

        // Apply equipment bonuses
        applyEquipmentStats();

        // Recalculate derived stats
        this.criticalChance = 5.0 + this.luck * 1.0;
        this.criticalDamage = 50.0 + this.dexterity * 2.0;
    }


    private void applyEquipmentStats() {
        applyStats(this.weapon);
        applyStats(this.armor);
        for (Equipment accessory : this.accessories) {
            if (accessory != null) applyStats(accessory);
        }
    }
    private void applyStats(Equipment equipment) {
        if (equipment != null) {
            this.vitality += equipment.getVitalityBonus();
            this.strength += equipment.getStrengthBonus();
            this.endurance += equipment.getEnduranceBonus();
            this.dexterity += equipment.getDexterityBonus();
            this.luck += equipment.getLuckBonus();
        }
    }


    public boolean isEquipped(Equipment item) {
        if (item == this.weapon) return true;
        if (item == this.armor) return true;
        for (Equipment accessory : this.accessories) {
            if (item == accessory) return true;
        }
        return false;
    }

    public TextureRegion getPlayerTexture(String state) {
        return playerAtlas.findRegion(state);
    }
    public void defend() {
        this.isDefending = true;
    }
    // Battle mechanics
    public int calculateDamage() {
        double baseDamage = strength * 3;
        if (isCriticalHit()) {
            return (int) (baseDamage * (1 + criticalDamage / 100));
        }
        return (int) baseDamage;
    }

    public boolean isCriticalHit() {
        return Math.random() * 100 < criticalChance;
    }
    public void receiveDamage(int damage) {
        if (isDefending) {
            damage *= 0.6;
            isDefending = false;
        }
        int finalDamage = damage - (int) (endurance * 0.5);
        finalDamage = Math.max(0, finalDamage);
        hitPoints -= finalDamage;
        checkDeath();
    }

    private void checkDeath() {
        if (hitPoints <= 0) {
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    Combat combatScreen = (Combat) game.getScreen();
                    combatScreen.showDeathMessageAndReturnToMenu();
                }
            });
        }
    }


    public void heal(int amount) {
        this.hitPoints += amount;
        if (this.hitPoints > this.getMaxHitPoints()) {
            this.hitPoints = this.getMaxHitPoints();
        }
    }

    public int getMaxHitPoints() {
        return 10 + this.vitality * 2;
    }

    // Getters and Setters
    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }

    public int getVitality() {
        return vitality;
    }

    public int getStrength() {
        return strength;
    }

    public int getEndurance() {
        return endurance;
    }

    public int getDexterity() {
        return dexterity;
    }

    public int getLuck() {
        return luck;
    }
    public int getBaseVitality() {
        return baseVitality;
    }

    public int getBaseStrength() {
        return baseStrength;
    }

    public int getBaseEndurance() {
        return baseEndurance;
    }

    public int getBaseDexterity() {
        return baseDexterity;
    }

    public int getBaseLuck() {
        return baseLuck;
    }

    public Equipment getWeapon() {
        return weapon;
    }

    public void setWeapon(Equipment weapon) {
        this.weapon = weapon;
    }

    public Equipment[] getAccessories() {
        return accessories;
    }

    public void setAccessories(Equipment[] accessories) {
        this.accessories = accessories;
    }

    public Equipment getArmor() {
        return armor;
    }

    public void setArmor(Equipment armor) {
        this.armor = armor;
    }

    public int getHitPoints() {
        return hitPoints;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public boolean shouldRotate() {
        return shouldRotatePlayer;
    }

    public int getCurrentTextIndex() {
        return currentTextIndex;
    }

    public void setCurrentTextIndex(int currentTextIndex) {
        this.currentTextIndex = currentTextIndex;
    }

    // Método para copiar datos de otro jugador
    public void copyFrom(Player other) {
        this.playerName = other.playerName;
        this.level = other.level;
        this.experience = other.experience;
        this.baseVitality = other.baseVitality;
        this.baseStrength = other.baseStrength;
        this.baseEndurance = other.baseEndurance;
        this.baseDexterity = other.baseDexterity;
        this.baseLuck = other.baseLuck;
        this.hitPoints = other.hitPoints;
        this.criticalChance = other.criticalChance;
        this.criticalDamage = other.criticalDamage;
        this.weapon = other.weapon;
        this.accessories = other.accessories;
        this.armor = other.armor;
        this.inventory = other.inventory;
        updateStats();
}
}
