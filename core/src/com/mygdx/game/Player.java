package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Player {
    private static Player instance;
    private String playerName;
    private int level;
    private int experience;
    private int vitality;
    private int strength;
    private int endurance;
    private int dexterity;
    private int luck;
    private int hitPoints;
    private double criticalChance = 5.0; // 5% base critical chance
    private double criticalDamage = 50.0; // 50% base critical damage
    private Equipment weapon;
    private Equipment[] accessories = new Equipment[4];
    private Equipment armor;
    private TextureAtlas playerAtlas;
    private boolean isDefending = false;
    private boolean shouldRotatePlayer;
    private Inventory inventory;  // Añadir inventario

    private Player() {
        this.playerName = "Default Hero";
        this.level = 1;
        this.experience = 5;
        this.vitality = 5;
        this.strength = 5;
        this.endurance = 5;
        this.dexterity = 5;
        this.luck = 5;
        this.hitPoints = 10 + this.vitality * 2;
        this.playerAtlas = new TextureAtlas("images/Main_Character/main_Character.atlas");
        this.inventory = new Inventory();  // Inicializar inventario
        updateStats();
        setRotation();
    }

    private void setRotation() {
        if (!playerAtlas.getRegions().isEmpty()) {
            this.shouldRotatePlayer = playerAtlas.getRegions().first().rotate;
        }
    }

    public static synchronized Player getInstance() {
        if (instance == null) {
            instance = new Player();
        }
        return instance;
    }

    public void gainExperience(int amount) {
        this.experience += amount;
        checkLevelUp();
    }

    private void checkLevelUp() {
        while (this.experience >= 100 * this.level) {
            this.experience -= 100 * this.level;
            this.level++;
            this.vitality += 5;
            this.strength += 5;
            this.endurance += 5;
            this.dexterity += 5;
            this.luck += 1;
            updateStats();
        }
    }

    public void equip(Equipment item) {
        unequip(item); // Ensure the item is unequipped first
        switch (item.getType()) { // Ensure Equipment has getType() method
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
                        updateStats();
                        return;
                    }
                }
                // If all accessory slots are filled, replace the first one
                unequip(this.accessories[0]);
                this.accessories[0] = item;
                break;
        }
        updateStats();
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
    }

    private void updateStats() {
        this.vitality = 5;
        this.strength = 5;
        this.endurance = 5;
        this.dexterity = 5;
        this.luck = 5;
        applyEquipmentStats();
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
            // Recalculate hit points, critical stats after equipment change
            this.hitPoints = 10 + this.vitality * 2;
            this.criticalChance = 5.0 + this.luck * 1.0;
            this.criticalDamage = 50.0 + this.dexterity * 2.0;
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
        if (Math.random() * 100 < criticalChance) {
            return (int) (baseDamage * (1 + criticalDamage / 100));
        }
        return (int) baseDamage;
    }

    public void receiveDamage(int damage) {
        if (isDefending) {
            damage *= 0.6;  // Apply 40% damage reduction
            isDefending = false;  // Reset defending status after receiving the attack
        }
        int finalDamage = damage - (int) (endurance * 0.5);
        finalDamage = Math.max(0, finalDamage);  // Ensure damage is not negative
        hitPoints -= finalDamage;
        checkDeath();
    }

    private void checkDeath() {
        if (hitPoints <= 0) {
            System.out.println(playerName + " has died.");
            // Handle death (e.g., restart level, game over screen)
        }
    }

    public void heal(float amount) {
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
}

