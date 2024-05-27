package com.mygdx.game;

public class SavedGame {
    private String playerName;
    private String saveData;
    private int slotNumber;
    private int currentTextIndex; // Renombrado desde progress si son lo mismo

    public SavedGame(String playerName, String saveData, int slotNumber, int currentTextIndex) {
        this.playerName = playerName;
        this.saveData = saveData;
        this.slotNumber = slotNumber;
        this.currentTextIndex = currentTextIndex;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getSaveData() {
        return saveData;
    }

    public void setSaveData(String saveData) {
        this.saveData = saveData;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public int getCurrentTextIndex() {
        return currentTextIndex;
    }

    public void setCurrentTextIndex(int currentTextIndex) {
        this.currentTextIndex = currentTextIndex;
    }
}
