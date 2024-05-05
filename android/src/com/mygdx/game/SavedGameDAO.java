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
        return database.insert("SavedGames", null, values);
    }

    public List<SavedGame> getAllSavedGames() {
        List<SavedGame> savedGames = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM SavedGames", null);
        if (cursor.moveToFirst()) {
            do {
                SavedGame savedGame = new SavedGame(
                        cursor.getString(cursor.getColumnIndex("player_name")),
                        cursor.getString(cursor.getColumnIndex("save_data"))
                );
                savedGames.add(savedGame);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return savedGames;
    }

    // Otros métodos para actualizar, eliminar, buscar por ID, etc.
}
