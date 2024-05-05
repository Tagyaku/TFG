package com.mygdx.game;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private List<Equipment> equipment;
    private List<Potion> potions;

    public Inventory() {
        equipment = new ArrayList<>();
        potions = new ArrayList<>();
    }

    public void addEquipment(Equipment item) {
        equipment.add(item);
    }

    public void addPotion(Potion potion) {
        potions.add(potion);
    }

    public List<Equipment> getEquipment() {
        return equipment;
    }

    public List<Potion> getPotions() {
        return potions;
    }
}
