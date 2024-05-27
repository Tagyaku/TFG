package com.mygdx.game;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GameDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "mi_base_de_datos.db";
    private static final int DATABASE_VERSION = 2; // Incrementa la versión de la base de datos

    public GameDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS SavedGames (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "player_name TEXT," +
                "save_data TEXT," +
                "slot_number INTEGER UNIQUE," +
                "current_text_index INTEGER" + // Asegúrate de incluir esta columna
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS SavedGames");
        onCreate(db);
    }
}
