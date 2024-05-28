package com.mygdx.game;

public class SavedGame {
    private String playerName;
    private String saveData;
    private int slotNumber;
    private int currentTextIndex;
    private int currentCombatIndex;

    public SavedGame(String playerName, String saveData, int slotNumber, int currentTextIndex, int currentCombatIndex) {
        this.playerName = playerName;
        this.saveData = saveData;
        this.slotNumber = slotNumber;
        this.currentTextIndex = currentTextIndex;
        this.currentCombatIndex = currentCombatIndex;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getSaveData() {
        return saveData;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public int getCurrentTextIndex() {
        return currentTextIndex;
    }

    public int getCurrentCombatIndex() {
        return currentCombatIndex;
    }
}
