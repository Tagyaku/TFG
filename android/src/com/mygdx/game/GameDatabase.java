package com.mygdx.game;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GameDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "mi_base_de_datos.db";
    private static final int DATABASE_VERSION = 1;

    public GameDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear tabla para guardar partidas guardadas
        db.execSQL("CREATE TABLE IF NOT EXISTS SavedGames (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "player_name TEXT," +
                "save_data TEXT" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Si necesitas realizar alguna actualización de la base de datos, puedes hacerlo aquí
        // Por ejemplo:
        // db.execSQL("DROP TABLE IF EXISTS SavedGames");
        // onCreate(db);
    }
}
