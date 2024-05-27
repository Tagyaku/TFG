package com.mygdx.game;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class SavedGameDAO {

    private SQLiteDatabase database;

    public SavedGameDAO(Context context) {
        GameDatabase dbHelper = new GameDatabase(context);
        database = dbHelper.getWritableDatabase();
    }

    public long insertSavedGame(SavedGame savedGame) {
        ContentValues values = new ContentValues();
        values.put("player_name", savedGame.getPlayerName());
        values.put("save_data", savedGame.getSaveData());
        values.put("slot_number", savedGame.getSlotNumber());
        values.put("current_text_index", savedGame.getCurrentTextIndex()); // Ajuste aquí
        return database.insertWithOnConflict("SavedGames", null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public List<SavedGame> getAllSavedGames() {
        List<SavedGame> savedGames = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM SavedGames ORDER BY slot_number", null);
        if (cursor.moveToFirst()) {
            do {
                SavedGame savedGame = new SavedGame(
                        cursor.getString(cursor.getColumnIndexOrThrow("player_name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("save_data")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("slot_number")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("current_text_index")) // Ajuste aquí
                );
                savedGames.add(savedGame);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return savedGames;
    }

    public void deleteSavedGame(int slotNumber) {
        database.delete("SavedGames", "slot_number = ?", new String[]{String.valueOf(slotNumber)});
    }
}
