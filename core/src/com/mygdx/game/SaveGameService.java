package com.mygdx.game;

import java.util.List;

public interface SaveGameService {
    void saveGame(SavedGame savedGame);
    List<SavedGame> loadAllSavedGames();
}
