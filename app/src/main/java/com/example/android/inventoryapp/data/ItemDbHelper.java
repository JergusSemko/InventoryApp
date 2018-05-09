package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventoryapp.data.ItemContract.ItemEntry;

// Database helper for Inventory app. Build to manage DB creation and version management.
public class ItemDbHelper extends SQLiteOpenHelper{

    public static final String LOG_TAG = ItemDbHelper.class.getSimpleName();

    // Database file name.
    private static final String DATABASE_NAME = "items.db";

    // Database version
    private static final int DATABASE_VERSION = 1;

    /**
     * Construct of a new instance of {@link ItemDbHelper}
     * @param context of the app.
     */
    public ItemDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Behold! The first creation of the sacred database!
    @Override
    public void onCreate(SQLiteDatabase db) {
        // This String contains the SQL statement for the items table.
        String SQL_CREATE_ITEMS_TABLE = "CREATE TABLE " + ItemEntry.TABLE_NAME + " ("
                + ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ItemEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + ItemEntry.COLUMN_PRICE + " INTEGER NOT NULL, "
                + ItemEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + ItemEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + ItemEntry.COLUMN_PHONE_NUMBER + " INTEGER NOT NULL,"
                + ItemEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL);";

        // Execute the SQL statement.
        db.execSQL(SQL_CREATE_ITEMS_TABLE);
    }

    // Lastly, this will be called in case of need to upgrade the database.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This is just a blank space, ready to be used when in need.
    }
}
