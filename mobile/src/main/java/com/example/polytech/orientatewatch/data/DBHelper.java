package com.example.polytech.orientatewatch.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.polytech.orientatewatch.data.Contract.Places_Entry;
/**
 * Manages a local database for weather data.
 */
public class DBHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "places.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_PLACES_TABLE = "CREATE TABLE " + Places_Entry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                Places_Entry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                Places_Entry.COLUMN_LOC_KEY + " INTEGER NOT NULL, " +
                Places_Entry.COLUMN_NAME_PLACE + " TEXT NOT NULL, " +
                Places_Entry.COLUMN_RATING_PLACE + "INTEGER NOT NULL"+
                Places_Entry.COLUMN_ADDR + " TEXT NOT NULL";
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Places_Entry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}