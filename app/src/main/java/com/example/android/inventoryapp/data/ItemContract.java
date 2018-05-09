package com.example.android.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

// API Contract for the Inventory app.
public final class ItemContract {

    // Empty constructor to prevent someone from accidentally instantiate the contract class.
    private ItemContract() {
    }

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //Possible path (appended to base content URI for possible URI's).
    public static final String PATH_ITEMS = "items";

    // Inner class defining constant values for every single item in the database.
    public static final class ItemEntry implements BaseColumns {

        // The content URI for accessing the item data.
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS);

        // The MIME type of the {@link #CONTENT_URI} for a list of items.
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        // The MIME type of the {@link #CONTENT_URI} for a single item.
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        // Name of database table.
        public static final String TABLE_NAME = "items";

        // Unique ID number for the item. Type INTEGER.
        public final static String _ID = BaseColumns._ID;

        // Name of the item. Type STRING.
        public final static String COLUMN_PRODUCT_NAME = "name";

        // Item price. Type INTEGER.
        public final static String COLUMN_PRICE = "price";

        // Item quantity. Type INTEGER.
        public final static String COLUMN_QUANTITY = "quantity";

        // Supplier name. Type TEXT.
        public final static String COLUMN_SUPPLIER_NAME = "supplier";

        // Supplier phone number. Type INTEGER.
        public static final String COLUMN_PHONE_NUMBER = "phone";

        // Description of the item. Type TEXT.
        public static final String COLUMN_DESCRIPTION = "description";
    }
}
