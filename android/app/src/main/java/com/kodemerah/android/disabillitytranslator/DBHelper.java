package com.kodemerah.android.disabillitytranslator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tersandung on 5/20/16.
 */
public class DBHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "DT.db";
    public static final String TABLE_NAME = "KATA_DASAR";
    public static final String COL_ID = "ID";
    public static final String COL_KATA = "KATA";
    public static final String COL_LAFAL = "LAFAL";
    public static final String COL_DESKRIPSI = "DESKRIPSI";
    public static final String COL_VIDEO = "VIDEO";

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ( " +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_KATA + " TEXT, " +
                COL_LAFAL + " TEXT, " +
                COL_DESKRIPSI + " TEXT, " +
                COL_VIDEO + " TEXT " +
                ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DATABASE", oldVersion + " to "+newVersion);
        String query = "DROP TABLE IF EXISTS "+TABLE_NAME;
        db.execSQL(query);
        onCreate(db);
    }

    public void insertKata(Kata kata){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_KATA, kata.getKata());
        values.put(COL_LAFAL, kata.getLafal());
        values.put(COL_DESKRIPSI, kata.getDeskripsi());
        values.put(COL_VIDEO, kata.getVideo());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public Kata getKata(int kata) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[] { COL_KATA,
                        COL_LAFAL, COL_DESKRIPSI, COL_DESKRIPSI }, COL_KATA + "=?",
                new String[] { String.valueOf(kata) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Kata contact = new Kata(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));

        return contact;
    }

    public List<Kata> getAllKatas() {
        List<Kata> kataList = new ArrayList<Kata>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Kata kata = new Kata();
                kata.setKata(cursor.getString(1));
                kata.setLafal(cursor.getString(2));
                kata.setDeskripsi(cursor.getString(3));
                kata.setVideo(cursor.getString(4));

                kataList.add(kata);
                Log.d("DATABASE", cursor.getString(0) +" - "+ kata.getKata() + " - " + kata.getLafal() + " - " + kata.getDeskripsi() + " - " + kata.getVideo());
            } while (cursor.moveToNext());
        }

        return kataList;
    }

    public int getKatasCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }

}
