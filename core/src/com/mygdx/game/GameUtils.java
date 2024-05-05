package com.mygdx.game;

public class GameUtils {

    private static GameUtils instance;
    private String playerName;

    private GameUtils() {}

    public static synchronized GameUtils getInstance() {
        if (instance == null) {
            instance = new GameUtils();
        }
        return instance;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String name) {
        this.playerName = name;
    }
}
