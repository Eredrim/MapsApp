package com.example.gilles.mapsapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gilles on 05/05/15.
 */
public class SQLiteDatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MPhotosDB";
    private static final String TABLE_NAME = "MapsPhotos";
    private static final String[] COLONNES = { "id", "nom", "commentaire", "pdate", "latitude", "longitude", "orientation", "filepath" };

    public SQLiteDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase arg0) {

        String CREATION_TABLE = "CREATE TABLE IF NOT EXISTS MapsPhotos (id INTEGER PRIMARY KEY AUTOINCREMENT, nom TEXT, commentaire TEXT, pdate TEXT, latitude REAL, longitude REAL, orientation REAL, filepath TEXT);";

        arg0.execSQL(CREATION_TABLE);
        Log.i("SQLite DB", "Creation");

    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

        arg0.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(arg0);
        Log.i("SQLite DB", "Upgrade");


    }

    public void delete(MPhoto photo){

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "id = ?", new String[]{String.valueOf(photo.getId())});
        db.close();

    }

    public void deleteAll(){

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE from " + TABLE_NAME);
        db.close();

    }

    public MPhoto get(long id) throws ParseException {
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, // a. table
                COLONNES, // b. column names
                " id = ?", // c. selections (where)
                new String[] { String.valueOf(id) }, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        if (cursor != null)
            cursor.moveToFirst();
        MPhoto photo = new MPhoto(cursor.getInt(0), cursor.getString(1), cursor.getString(2), df.parse(cursor.getString(3)), cursor.getDouble(4), cursor.getDouble(5), cursor.getFloat(6), cursor.getString(7));
        db.close();

        return photo;
    }

    public List<MPhoto> getAll() throws ParseException {

        List<MPhoto> lstPhotos = new ArrayList<>();
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");

        String query = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                MPhoto photo = new MPhoto(cursor.getInt(0), cursor.getString(1), cursor.getString(2), df.parse(cursor.getString(3)), cursor.getDouble(4), cursor.getDouble(5), cursor.getFloat(6), cursor.getString(7));
                lstPhotos.add(photo);
            } while (cursor.moveToNext());
        }

        db.close();

        return lstPhotos;
    }

    public long insert(MPhoto photo) {

        SQLiteDatabase db = this.getWritableDatabase();
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");

        ContentValues values = new ContentValues();
        values.put("nom", photo.getNom());
        values.put("commentaire", photo.getCommentaire());
        values.put("pdate", df.format(photo.getDate()));
        values.put("latitude", photo.getLatitude());
        values.put("longitude", photo.getLongitude());
        values.put("orientation", photo.getOrientation());
        values.put("filepath", photo.getFilepath());
        // insertion
        long id = db.insert(TABLE_NAME, null, values);

        db.close();

        return id;
    }

    public int update(MPhoto photo) {

        SQLiteDatabase db = this.getWritableDatabase();
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");

        ContentValues values = new ContentValues();
        values.put("nom", photo.getNom());
        values.put("commentaire", photo.getCommentaire());
        values.put("pdate", df.format(photo.getDate()));
        values.put("latitude", photo.getLatitude());
        values.put("longitude", photo.getLongitude());
        values.put("orientation", photo.getOrientation());
        values.put("filepath", photo.getFilepath());

        int i = db.update(TABLE_NAME, // table
                values, // column/value
                "id = ?", // selections
                new String[] { String.valueOf(photo.getId()) });

        db.close();

        return i;
    }

}
