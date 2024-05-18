package com.mygdx.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Inventory {
    private List<Equipment> equipment;
    private Map<Potion.PotionType, Integer> potions;

    public Inventory() {
        equipment = new ArrayList<>();
        potions = new HashMap<>();
    }

    public void addEquipment(Equipment item) {
        equipment.add(item);
    }

    @SuppressWarnings("NewApi")
    public void addPotion(Potion.PotionType type, int quantity) {
        potions.put(type, potions.getOrDefault(type, 0) + quantity);
    }

    @SuppressWarnings("NewApi")
    public int getPotionQuantity(Potion.PotionType type) {
        return potions.getOrDefault(type, 0);
    }

    public void usePotion(Potion.PotionType type) {
        @SuppressWarnings("NewApi") int quantity = potions.getOrDefault(type, 0);
        if (quantity > 0) {
            potions.put(type, quantity - 1);
        }
    }

    public Map<Potion.PotionType, Integer> getPotions() {
        return potions;
    }
}
