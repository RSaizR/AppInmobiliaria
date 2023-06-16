package com.example.contactsaccess.Asset;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class AssetsDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "assets.db";
    private static final int DATABASE_VERSION = 2;
    public static final String TABLE_NAME = "assets";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_SQUARE_METERS = "square_meters";

    public AssetsDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TYPE + " TEXT, "
                + COLUMN_ADDRESS + " TEXT, "
                + COLUMN_PRICE + " REAL, "
                + COLUMN_SQUARE_METERS + " REAL)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    public long saveAsset(String type, String address, double price, double squareMeters) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TYPE, type);
        values.put(COLUMN_ADDRESS, address);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_SQUARE_METERS, squareMeters);

        long newRowId = db.insert(TABLE_NAME, null, values);

        db.close();

        return newRowId;
    }

    public void listAllAssets() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                @SuppressLint("Range") String type = cursor.getString(cursor.getColumnIndex(COLUMN_TYPE));
                @SuppressLint("Range") String address = cursor.getString(cursor.getColumnIndex(COLUMN_ADDRESS));
                @SuppressLint("Range") double price = cursor.getDouble(cursor.getColumnIndex(COLUMN_PRICE));
                @SuppressLint("Range") double squareMeters = cursor.getDouble(cursor.getColumnIndex(COLUMN_SQUARE_METERS));

                Log.d("AssetsDatabase", "ID: " + id);
                Log.d("AssetsDatabase", "Type: " + type);
                Log.d("AssetsDatabase", "Address: " + address);
                Log.d("AssetsDatabase", "Price: " + price);
                Log.d("AssetsDatabase", "Square Meters: " + squareMeters);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
    }
    public void deleteAllAssets() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

    public List<Asset> getAllAssets() {
        List<Asset> assetsList = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                @SuppressLint("Range") String type = cursor.getString(cursor.getColumnIndex(COLUMN_TYPE));
                @SuppressLint("Range") String address = cursor.getString(cursor.getColumnIndex(COLUMN_ADDRESS));
                @SuppressLint("Range") double price = cursor.getDouble(cursor.getColumnIndex(COLUMN_PRICE));
                @SuppressLint("Range") double squareMeters = cursor.getDouble(cursor.getColumnIndex(COLUMN_SQUARE_METERS));

                Asset asset = new Asset(id, type, address, price, squareMeters);
                assetsList.add(asset);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();

        return assetsList;
    }
}
