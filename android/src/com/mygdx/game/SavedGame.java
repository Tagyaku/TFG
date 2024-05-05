package com.mygdx.game;

public class SavedGame {
    private int id;
    private String playerName;
    private String saveData;

    public SavedGame(String playerName, String saveData) {
        this.playerName = playerName;
        this.saveData = saveData;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
