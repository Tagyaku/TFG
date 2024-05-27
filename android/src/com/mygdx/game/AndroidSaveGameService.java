package com.mygdx.game;

import android.content.Context;
import java.util.List;

public class AndroidSaveGameService implements SaveGameService {
    private SavedGameDAO savedGameDAO;

    public AndroidSaveGameService(Context context) {
        this.savedGameDAO = new SavedGameDAO(context);
    }

    @Override
    public void saveGame(SavedGame savedGame) {
        savedGameDAO.insertSavedGame(savedGame);
    }

    @Override
    public List<SavedGame> loadAllSavedGames() {
        return savedGameDAO.getAllSavedGames();
    }

    public void deleteSavedGame(int slotNumber) {
        savedGameDAO.deleteSavedGame(slotNumber);
    }
}
