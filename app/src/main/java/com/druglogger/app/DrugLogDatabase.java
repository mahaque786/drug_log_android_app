package com.druglogger.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DrugLogDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "drug_log.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_ENTRIES = "entries";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DRUG_NAME = "drug_name";
    private static final String COLUMN_DOSAGE = "dosage";
    private static final String COLUMN_NOTES = "notes";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    public DrugLogDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_ENTRIES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DRUG_NAME + " TEXT NOT NULL, " +
                COLUMN_DOSAGE + " TEXT, " +
                COLUMN_NOTES + " TEXT, " +
                COLUMN_TIMESTAMP + " INTEGER NOT NULL)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENTRIES);
        onCreate(db);
    }

    public long addEntry(DrugEntry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DRUG_NAME, entry.getDrugName());
        values.put(COLUMN_DOSAGE, entry.getDosage());
        values.put(COLUMN_NOTES, entry.getNotes());
        values.put(COLUMN_TIMESTAMP, entry.getTimestamp());

        long id = db.insert(TABLE_ENTRIES, null, values);
        db.close();
        return id;
    }

    public List<DrugEntry> getAllEntries() {
        List<DrugEntry> entries = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ENTRIES + " ORDER BY " + COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                DrugEntry entry = new DrugEntry(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DRUG_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOSAGE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP))
                );
                entries.add(entry);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return entries;
    }

    public void deleteEntry(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ENTRIES, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteAllEntries() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ENTRIES, null, null);
        db.close();
    }
}
