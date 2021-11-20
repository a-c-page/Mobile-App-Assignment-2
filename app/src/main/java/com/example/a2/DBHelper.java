package com.example.a2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper
{

    Geocoder geocoder;

    public DBHelper(Context context)
    {
        super(context, "locations.db", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase DB)
    {
        DB.execSQL("create Table locations(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE," +
                "address TEXT," +
                "latitude REAL," +
                "longitude REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int i1)
    {
        DB.execSQL("drop Table if exists locations.db");
    }

    public Boolean addAddress(String address, double latitude, double longitude)
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("address", address);
        contentValues.put("latitude", latitude);
        contentValues.put("longitude", longitude);
        long result = DB.insert("locations", null, contentValues);
        return result != -1;
    }

    public Cursor query(String query)
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery(query, null);
        return cursor;
    }

    public void delete(String ID){
        SQLiteDatabase DB = this.getWritableDatabase();
        DB.delete("locations","ID=?",new String[]{ID});
    }

    public void update(String ID, String address, Context c){
        SQLiteDatabase DB = this.getWritableDatabase();

        geocoder = new Geocoder(c);

        try {
            List<Address> results = geocoder.getFromLocationName(address, 1);
            double latitude = results.get(0).getLatitude();
            double longitude = results.get(0).getLongitude();
            String addy = results.get(0).getAddressLine(0);

            ContentValues cv = new ContentValues();
            cv.put("id", ID);
            cv.put("address", addy);
            cv.put("latitude", Double.toString(latitude));
            cv.put("longitude", Double.toString(longitude));

            DB.update("locations", cv, "id = ?", new String[]{ID});

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}