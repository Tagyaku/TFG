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

    public void addPotion(Potion.PotionType type, int quantity) {
        Integer currentQuantity = potions.get(type);
        if (currentQuantity == null) {
            currentQuantity = 0;
        }
        potions.put(type, currentQuantity + quantity);
    }

    public int getPotionQuantity(Potion.PotionType type) {
        Integer quantity = potions.get(type);
        return quantity != null ? quantity : 0;
    }

    public boolean usePotion(Potion.PotionType type) {
        Integer currentQuantity = potions.get(type);
        if (currentQuantity != null && currentQuantity > 0) {
            potions.put(type, currentQuantity - 1);
            return true;
        }
        return false;
    }

    public Map<Potion.PotionType, Integer> getPotions() {
        return potions;
    }

    public List<Equipment> getEquipment() {
        return equipment;
    }

    public void removeEquipment(Equipment item) {
        equipment.remove(item);
    }

    public void removePotion(Potion.PotionType type, int quantity) {
        Integer currentQuantity = potions.get(type);
        if (currentQuantity != null && currentQuantity >= quantity) {
            potions.put(type, currentQuantity - quantity);
        }
    }

    public List<Equipment> getEquipmentByType(Equipment.Type type) {
        List<Equipment> filteredEquipment = new ArrayList<>();
        for (Equipment item : equipment) {
            if (item.getType() == type) {
                filteredEquipment.add(item);
            }
        }
        return filteredEquipment;
    }

    public boolean hasPotion(Potion.PotionType type) {
        Integer quantity = potions.get(type);
        return quantity != null && quantity > 0;
    }
}
