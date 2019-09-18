/**
 * Created by mluc on 13-07-2016.
 */

package com.lundwall.beerallover;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class db extends SQLiteOpenHelper {

    private static class DbBitmapUtility {

        // convert from bitmap to byte array
        public static byte[] getBytes(Bitmap bitmap) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
            return stream.toByteArray();
        }

        // convert from byte array to bitmap
        public static Bitmap getImage(byte[] image) {
            return BitmapFactory.decodeByteArray(image, 0, image.length);
        }
    }

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "OlObjDB";
    private static final String TABLE_OlObj = "ol";

    // OlObj Table Columns names
    private static final String KEY_Id = "Id";
    private static final String KEY_Navn = "Navn";
    private static final String KEY_Pct = "Pct";
    private static final String KEY_OlType = "OlType";
    private static final String KEY_Rating = "Rating";
    private static final String KEY_Comment = "Comment";
    private static final String KEY_Billede = "Billede";
    private static final String KEY_Bryggeri = "Bryggeri";

    public db(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase dBase) {
        String CREATE_OlObj_TABLE = "CREATE TABLE " + TABLE_OlObj + " ("
                + KEY_Id + " INTEGER PRIMARY KEY," //  Ikke KEY AUTOINCREMENT NOT NULL
                + KEY_Navn + " TEXT,"
                + KEY_Pct + " REAL,"
                + KEY_OlType + " TEXT,"
                + KEY_Rating + " REAL,"
                + KEY_Comment + " TEXT,"
                + KEY_Billede + " BLOB,"
                + KEY_Bryggeri + " TEXT"
                + ")";
        dBase.execSQL(CREATE_OlObj_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase dBase, int oldVersion, int newVersion) {
        /*
        Drop older table if existed
        dBase.execSQL("DROP TABLE IF EXISTS " + TABLE_OlObj);
        // Create tables again
        onCreate(dBase);
        */
    }

    // Adding new ol
    public Boolean gemOl(int Id, OlObj ol) {
        try {
            SQLiteDatabase dBase = this.getWritableDatabase();
            // Konverter Billede
            /*
            ol.Billede.copyPixelsToBuffer(billedByteBuffer);
            */
            // ByteBuffer billedByteBuffer = ByteBuffer.allocate(ol.Billede.getByteCount());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            byte[] BilledBit = null;
            if (ol.Billede!=null)
            {
                ol.Billede.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                BilledBit = stream.toByteArray();
            }
            ContentValues values = new ContentValues();
            values.put(KEY_Id, Id);
            values.put(KEY_Navn, ol.Navn);
            values.put(KEY_Pct, ol.Pct);
            values.put(KEY_OlType, ol.OlType);
            values.put(KEY_Rating, ol.Rating);
            values.put(KEY_Comment, ol.Comment);
            values.put(KEY_Billede, BilledBit);
            values.put(KEY_Bryggeri, ol.Bryggeri);
            dBase.insert(TABLE_OlObj, null, values);
            dBase.close();
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }

    public OlObj hentOl(int olId) {
        // Henter en Øl
        OlObj ol = null;
        byte[] BilledBit = new byte[60000];

        int    Id = 0;
        String Navn;
        Double Pct;
        String OlType;
        Double Rating;
        String Comment;
        Bitmap Billede;
        String Bryggeri;

        try {
            String selectQuery = "SELECT * FROM " + TABLE_OlObj + " WHERE " + KEY_Id + "=" + olId;

            SQLiteDatabase dBase = this.getReadableDatabase();
            Cursor cursor = dBase.rawQuery(selectQuery, null);
            try {
                Id=cursor.getCount();
                if (cursor.moveToFirst())
                    Id = cursor.getInt(0);
                    Navn = cursor.getString(1);
                    Pct = cursor.getDouble(2);
                    OlType = cursor.getString(3);
                    Rating = cursor.getDouble(4);
                    Comment = cursor.getString(5);
                    BilledBit = cursor.getBlob(6);
                    if (BilledBit!=null)
                        Billede = BitmapFactory.decodeByteArray(BilledBit, 0, BilledBit.length);
                    else
                        Billede = null;
                    Bryggeri = cursor.getString(7);
                    ol = new OlObj(Navn, Pct, OlType, Rating, Comment, Billede, Bryggeri);
                }
            finally {
                cursor.close();
            }
        } catch (Exception e) {
            if (e.getMessage()==Integer.toString(Id))
                return null;
            else
                return ol;
        }
        return ol;
    }

    public ArrayList<ListeObj> hentListe() {
        // Henter alle grundlæggende ølinfo i et land
        ListeObj list = null;
        ArrayList olListe = new ArrayList();
        byte[] BilledBit;

        Integer Id;
        String Navn;
        Double Pct;
        String OlType;
        Double Rating;

        try {
            String selectQuery = "SELECT * FROM " + TABLE_OlObj;

            SQLiteDatabase dBase = this.getReadableDatabase();
            Cursor cursor = dBase.rawQuery(selectQuery, null);
            try {
                if (cursor.moveToFirst()) {
                    do {
                        Id = Integer.parseInt(cursor.getString(0));
                        Navn = cursor.getString(1);
                        Pct = Double.parseDouble(cursor.getString(2));
                        OlType = cursor.getString(3);
                        Rating = cursor.getDouble(4);
                        olListe.add(new ListeObj(Id, Navn, Pct, Rating, OlType));
                    } while (cursor.moveToNext()) ;
                }
                else
                    return null;
            }
            finally {
                cursor.close();
            }
        } catch (Exception e)
        { return null;}
        return olListe;
    }

    public Boolean Toem() {
        try {
            SQLiteDatabase dBase = this.getWritableDatabase();
            dBase.execSQL("DROP TABLE IF EXISTS " + TABLE_OlObj);
            onCreate(dBase);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}