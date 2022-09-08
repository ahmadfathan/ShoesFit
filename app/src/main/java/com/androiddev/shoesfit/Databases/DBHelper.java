package com.androiddev.shoesfit.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.androiddev.shoesfit.Constants.Literals;
import com.androiddev.shoesfit.Models.Filter;
import com.androiddev.shoesfit.Models.Shoes;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    // penggunaan, celana, harga
    private static final String DATABASE_NAME = "shoes_fit";
    private static final String TABLE_SHOES = "table_shoes";

    // Shoes table coloumns
    private static final String COLOUMN_ID = "coloumn_id";
    private static final String COLOUMN_TYPE = "coloumn_type";
    private static final String COLOUMN_CATEGORY = "coloumn_category";
    private static final String COLOUMN_NAME = "coloumn_name";
    private static final String COLOUMN_COLOR = "coloumn_color";
    private static final String COLOUMN_PRICE = "coloumn_price";
    private static final String COLOUMN_IMAGE1 = "coloumn_image1";
    private static final String COLOUMN_IMAGE2 = "coloumn_image2";
    private static final String COLOUMN_IMAGE3 = "coloumn_image3";
    private static final String COLOUMN_DESCRIPTION = "coloumn_description";
    private static final String COLOUMN_SUITS_WITH = "coloumn_suit_with";

    private static final int DATABASE_VERSION = 1;

    private Context mContext;

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_SHOES + "("
                + COLOUMN_ID + " INTEGER PRIMARY KEY,"
                +  COLOUMN_TYPE  + " TEXT,"
                +  COLOUMN_CATEGORY  + " TEXT,"
                +  COLOUMN_NAME  + " TEXT,"
                +  COLOUMN_COLOR  + " TEXT,"
                +  COLOUMN_PRICE  + " INTEGER,"
                +  COLOUMN_IMAGE1  + " TEXT,"
                +  COLOUMN_IMAGE2  + " TEXT,"
                +  COLOUMN_IMAGE3  + " TEXT,"
                +  COLOUMN_DESCRIPTION  + " TEXT,"
                +  COLOUMN_SUITS_WITH  + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHOES);
        onCreate(db);
    }

    public void addShoes(Shoes shoes){
        SQLiteDatabase db  = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLOUMN_TYPE, shoes.getType());
        values.put(COLOUMN_CATEGORY, shoes.getCategory());
        values.put(COLOUMN_NAME, shoes.getName());
        values.put(COLOUMN_COLOR, shoes.getColor());
        values.put(COLOUMN_PRICE, shoes.getPrice());
        values.put(COLOUMN_IMAGE1, shoes.getImage1());
        values.put(COLOUMN_IMAGE2, shoes.getImage2());
        values.put(COLOUMN_IMAGE3, shoes.getImage3());
        values.put(COLOUMN_DESCRIPTION, shoes.getDescription());
        values.put(COLOUMN_SUITS_WITH, shoes.getSuits_with());

        db.insert(TABLE_SHOES, null, values);
        db.close();
    }

    public List<Shoes> getListOfShoes(Filter filter){
        List<Shoes> shoesList = new ArrayList<>();
        // Select All Query
        String kebutuhan, celana;
        kebutuhan = Literals.KEBUTUHAN[filter.getKebutuhan().getValue()];
        celana = Literals.CELANA[filter.getCelana().getValue()];

        String harga = "";
        switch (filter.getHarga()){
            case DI_ATAS_300K:
                harga = " > 300000";
                break;
            case DI_BAWAH_200K:
                harga = " < 200000";
                break;
            case ANTARA_200K_SAMPAI_300K:
                harga = " >= 200000 and " + COLOUMN_PRICE + " <= 300000";
                break;
        }

        String selectQuery = "SELECT  * FROM " + TABLE_SHOES
                + " WHERE (" + COLOUMN_PRICE + harga + ") and ("
                + COLOUMN_TYPE + " = '" + kebutuhan + "') and ("
                + COLOUMN_SUITS_WITH + " LIKE '%" + celana + "%')";

        //Toast.makeText(mContext, "" + selectQuery, Toast.LENGTH_SHORT).show();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Shoes shoes = new Shoes(
                        cursor.getInt(cursor.getColumnIndex(COLOUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(COLOUMN_TYPE)),
                        cursor.getString(cursor.getColumnIndex(COLOUMN_CATEGORY)),
                        cursor.getString(cursor.getColumnIndex(COLOUMN_NAME)),
                        cursor.getString(cursor.getColumnIndex(COLOUMN_COLOR)),
                        cursor.getInt(cursor.getColumnIndex(COLOUMN_PRICE)),
                        cursor.getString(cursor.getColumnIndex(COLOUMN_IMAGE1)),
                        cursor.getString(cursor.getColumnIndex(COLOUMN_IMAGE2)),
                        cursor.getString(cursor.getColumnIndex(COLOUMN_IMAGE3)),
                        cursor.getString(cursor.getColumnIndex(COLOUMN_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndex(COLOUMN_SUITS_WITH))
                );
                shoesList.add(shoes);
            } while (cursor.moveToNext());
        }else {
            return null;
        }

        // return contact list
        return shoesList;
    }

    public List<Shoes> getAllOfShoes(){
        List<Shoes> shoesList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_SHOES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Shoes shoes = new Shoes(
                        cursor.getInt(cursor.getColumnIndex(COLOUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(COLOUMN_TYPE)),
                        cursor.getString(cursor.getColumnIndex(COLOUMN_CATEGORY)),
                        cursor.getString(cursor.getColumnIndex(COLOUMN_NAME)),
                        cursor.getString(cursor.getColumnIndex(COLOUMN_COLOR)),
                        cursor.getInt(cursor.getColumnIndex(COLOUMN_PRICE)),
                        cursor.getString(cursor.getColumnIndex(COLOUMN_IMAGE1)),
                        cursor.getString(cursor.getColumnIndex(COLOUMN_IMAGE2)),
                        cursor.getString(cursor.getColumnIndex(COLOUMN_IMAGE3)),
                        cursor.getString(cursor.getColumnIndex(COLOUMN_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndex(COLOUMN_SUITS_WITH))
                );
                shoesList.add(shoes);
            } while (cursor.moveToNext());
        }else {
            return null;
        }

        // return contact list
        return shoesList;
    }
}
