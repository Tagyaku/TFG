package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

import java.util.HashMap;
import java.util.Map;

public class EquipableItems {
    private TextureAtlas itemsAtlas;
    private Map<Equipment.Type, String[]> nameMap;

    public EquipableItems() {
        itemsAtlas = new TextureAtlas("items.atlas");
        initializeNameMap();
    }

    private void initializeNameMap() {
        nameMap = new HashMap<>();
        nameMap.put(Equipment.Type.WEAPON, new String[]{"Sword", "Axe", "Spear"});
        nameMap.put(Equipment.Type.ARMOR, new String[]{"Plate Armor", "Leather Armor", "Cloth Armor"});
        nameMap.put(Equipment.Type.ACCESSORY, new String[]{"Ring", "Necklace", "Belt"});
    }

    public Equipment createRandomItem() {
        Equipment.Type type = Equipment.Type.values()[MathUtils.random(Equipment.Type.values().length - 1)];
        String[] possibleNames = nameMap.get(type);
        String itemName = possibleNames[MathUtils.random(0, possibleNames.length - 1)];
        String regionName = itemName.toLowerCase().replace(" ", "_") + MathUtils.random(1, 5); // Construct the region name

        TextureRegion texture = itemsAtlas.findRegion(regionName);
        int vitality = MathUtils.random(0, 10);
        int strength = MathUtils.random(0, 10);
        int endurance = MathUtils.random(0, 10);
        int dexterity = MathUtils.random(0, 10);
        int luck = MathUtils.random(0, 10);

        return new Equipment(itemName, itemsAtlas, regionName, type, vitality, strength, endurance, dexterity, luck);
    }



    // Additional methods as needed...
}
