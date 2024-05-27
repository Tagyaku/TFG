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
        nameMap.put(Equipment.Type.WEAPON, new String[]{"weapon_axe1","weapon_axe2","weapon_axe3","weapon_axe4","weapon_axe5", "weapon_club1", "weapon_club2",  "weapon_club3",  "weapon_club4",  "weapon_club5",  "weapon_spear1",  "weapon_spear2",  "weapon_spear3",  "weapon_spear4",  "weapon_spear5", "weapon_sword1", "weapon_sword2", "weapon_sword3", "weapon_sword4", "weapon_sword5", "weapon_wand1", "weapon_wand2", "weapon_wand3", "weapon_wand4", "weapon_wand5"});
        nameMap.put(Equipment.Type.ARMOR, new String[]{"armor_cloth1","armor_cloth2","armor_cloth3","armor_cloth4","armor_cloth5", "armor_leather1", "armor_leather2", "armor_leather3", "armor_leather4", "armor_leather5", "armor_plate1", "armor_plate2", "armor_plate3", "armor_plate4", "armor_plate5"});
        nameMap.put(Equipment.Type.ACCESSORY, new String[]{"Accessory_belt1","Accessory_belt2", "Accessory_cape1", "Accessory_cape2", "Accessory_earring1", "Accessory_earring2", "Accessory_earring3", "Accessory_necklace1", "Accessory_necklace2", "Accessory_necklace3", "Accessory_orb1", "Accessory_orb2", "Accessory_ring1", "Accessory_ring2", "Accessory_ring3"});
    }

    public Equipment createRandomItem() {
        Equipment.Type type = Equipment.Type.values()[MathUtils.random(Equipment.Type.values().length - 1)];
        String[] possibleNames = nameMap.get(type);
        String baseName = possibleNames[MathUtils.random(0, possibleNames.length - 1)];

    // Imprimir para depuración
    System.out.println("Generando item de tipo: " + type + " con nombre base: " + baseName);

    TextureRegion texture = itemsAtlas.findRegion(baseName);
        if (texture == null) {
        System.out.println("No se encontró la textura para: " + baseName);
        return null;
        }

        int vitality = MathUtils.random(0, 10);
        int strength = MathUtils.random(0, 10);
        int endurance = MathUtils.random(0, 10);
        int dexterity = MathUtils.random(0, 10);
        int luck = MathUtils.random(0, 10);

    return new Equipment(baseName, (TextureAtlas.AtlasRegion) texture, type, vitality, strength, endurance, dexterity, luck);
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
