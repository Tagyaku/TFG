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
        itemsAtlas = new TextureAtlas("images/items/items.atlas");
        initializeNameMap();
    }

    private void initializeNameMap() {
        nameMap = new HashMap<>();
        nameMap.put(Equipment.Type.WEAPON, new String[]{"weapon_axe", "weapon_club", "weapon_spear", "weapon_sword", "weapon_wand"});
        nameMap.put(Equipment.Type.ARMOR, new String[]{"armor_cloth", "armor_leather", "armor_plate"});
        nameMap.put(Equipment.Type.ACCESSORY, new String[]{"accessory_belt", "accessory_cape", "accessory_earring", "accessory_necklace", "accessory_orb", "accessory_ring"});
    }

    public Equipment createRandomItem() {
        Equipment.Type type = Equipment.Type.values()[MathUtils.random(Equipment.Type.values().length - 1)];
        String[] possibleNames = nameMap.get(type);
        String baseName = possibleNames[MathUtils.random(0, possibleNames.length - 1)];
        String regionName = generateRegionName(baseName);

        TextureRegion texture = itemsAtlas.findRegion(regionName);
        if (texture == null) {
            // If the texture is not found, return null or try another random item
            return null; // Or handle this case appropriately
        }

        int vitality = MathUtils.random(0, 10);
        int strength = MathUtils.random(0, 10);
        int endurance = MathUtils.random(0, 10);
        int dexterity = MathUtils.random(0, 10);
        int luck = MathUtils.random(0, 10);

        return new Equipment(baseName, texture, type, vitality, strength, endurance, dexterity, luck); // Use texture directly
    }

    private String generateRegionName(String baseName) {
        // Attempt to find a valid texture region
        String regionName;
        for (int i = 1; i <= 5; i++) {
            regionName = baseName + i;
            if (itemsAtlas.findRegion(regionName) != null) {
                return regionName;
            }
        }
        // If no valid region is found, return a default or handle the error
        return baseName + "1"; // Default to the first if none are found (though this shouldn't happen)
    }
}
