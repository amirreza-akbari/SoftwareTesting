package com.example.healthcheckapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SettingsDatabaseHelper extends SQLiteOpenHelper {
    // Database name and version
    private static final String DATABASE_NAME = "settings.db";
    private static final int DATABASE_VERSION = 1;

    // Constructor to initialize the database helper
    public SettingsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Method to create the database table when the database is first created
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a table 'settings' with columns 'id' (primary key) and 'theme' (stores theme preference)
        db.execSQL("CREATE TABLE settings (id INTEGER PRIMARY KEY, theme TEXT)");
    }

    // Method to upgrade the database if the version is changed
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the existing table if it exists and recreate it
        db.execSQL("DROP TABLE IF EXISTS settings");
        onCreate(db); // Recreate the table
    }
}