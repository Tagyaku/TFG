package com.mygdx.game;

import com.badlogic.gdx.Gdx;

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
    Gdx.app.log("Inventory", "Added PotionType: " + type + " New Quantity: " + (currentQuantity + quantity));
    }

    public int getPotionQuantity(Potion.PotionType type) {
        initializePotions(); // Asegúrate de que el mapa se inicialice
        Integer quantity = potions.get(type);
        Gdx.app.log("Inventory", "PotionType: " + type + " Quantity: " + quantity);return quantity != null ? quantity : 0;
    }

    public void clear() {
        equipment.clear();
        potions.clear();
    }

    public boolean usePotion(Potion.PotionType type) {
        initializePotions(); // Asegúrate de que el mapa se inicialice
        Integer currentQuantity = potions.get(type);
        if (currentQuantity != null && currentQuantity > 0) {
            potions.put(type, currentQuantity - 1);
            return true;
        }
        return false;
    }

    public Map<Potion.PotionType, Integer> getPotions() {
        initializePotions(); // Asegúrate de que el mapa se inicialice
        return potions;
    }

    public List<Equipment> getEquipment() {
        return equipment;
    }

    public void removeEquipment(Equipment item) {
        equipment.remove(item);
    }

    public void removePotion(Potion.PotionType type, int quantity) {
        initializePotions(); // Asegúrate de que el mapa se inicialice
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
        initializePotions(); // Asegúrate de que el mapa se inicialice
        Integer quantity = potions.get(type);
        return quantity != null && quantity > 0;
    }

    // Método para inicializar el HashMap después de la deserialización
    public void initializePotions() {
        if (potions == null) {
            potions = new HashMap<>();
        }
    }
    // Método para normalizar las claves del HashMap después de la deserialización
    public void normalizePotions() {
        Map<Potion.PotionType, Integer> normalizedPotions = new HashMap<>();
        for (Map.Entry<?, Integer> entry : potions.entrySet()) {
            Potion.PotionType normalizedType = Potion.PotionType.valueOf(entry.getKey().toString());
            normalizedPotions.put(normalizedType, entry.getValue());
        }
        this.potions = normalizedPotions;
    }
    // Método para copiar datos de otro inventario
    public void copyFrom(Inventory other) {
        this.equipment = new ArrayList<>(other.equipment);
        this.potions = new HashMap<>(other.potions);
        initializePotions(); // Inicializar después de copiar
        normalizePotions();  // Normalizar después de copiar
    }
}
