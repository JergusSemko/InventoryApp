package com.example.android.inventoryapp.data;

import android.provider.BaseColumns;

// API Contract for the Inventory app.
public final class ItemContract {

    // Empty constructor to prevent someone from accidentally instantiate the contract class.
    private ItemContract() {}

     // Inner class defining constant values for every single item in the database.
     public static final class ItemEntry implements BaseColumns {

        // Name of database table.
         public static final String TABLE_NAME = "items";

         // Unique ID number for the item. Type INTEGER.
         public final static String _ID = BaseColumns._ID;

         // Name of the item. Type STRING.
         public final static String COLUMN_PRODUCT_NAME ="product name";

         // Item price. Type INTEGER.
         public final static String COLUMN_PRICE = "price";

         // Item quantity. Type INTEGER.
         public final static String COLUMN_QUANTITY = "quantity";

         // Supplier name. Type TEXT.
         public final static String COLUMN_SUPPLIER_NAME = "supplier name";

         // Supplier phone number. Type INTEGER.
         public static final String COLUMN_SUPPLIER_PHONE_NUMBER = "supplier phone number";
     }
}
