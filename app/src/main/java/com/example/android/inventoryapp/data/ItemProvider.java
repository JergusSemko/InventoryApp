package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import com.example.android.inventoryapp.data.ItemContract.ItemEntry;

// {@link ContentProvider} for item inventory app.
public class ItemProvider extends ContentProvider {

    // Tag for the log messages.
    public static final String LOG_TAG = ItemProvider.class.getSimpleName();

    // URI matcher code for the content URI for the items table.
    private static final int ITEMS = 100;

    // URI matcher code fo the content URI for a single item in the items table.
    private static final int ITEM_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer to run the first time anything is called from this class
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://com.example.android.items/items" will map to the
        // integer code {@link #ITEMS}. This URI is used to provide access to MULTIPLE rows
        // of the items table.
        sUriMatcher.addURI(ItemContract.CONTENT_AUTHORITY, ItemContract.PATH_ITEMS, ITEMS);

        // The content URI of the form "content://com.example.android.items/items/#" will map to the
        // integer code {@link #ITEM_ID}. This URI is used to provide access to ONE single row
        // of the items table.

        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.example.android.items/items/3" matches, but
        // "content://com.example.android.items/items" (without a number at the end) doesn't match.
        sUriMatcher.addURI(ItemContract.CONTENT_AUTHORITY, ItemContract.PATH_ITEMS + "/#", ITEM_ID);
    }

    // Database helper object
    private ItemDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new ItemDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query.
        Cursor cursor;

        //Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                // For the ITEMS code, query the items table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the items table.
                cursor = database.query(ItemEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case ITEM_ID:
                // For the ITEM_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.items/items/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = ItemEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the items table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(ItemEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor to know what content URI the Cursor was created for.
        // If the data at this URI changes, then the Cursor needs to be updated.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return insertItem(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert item into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertItem(Uri uri, ContentValues values) {
        // Check that the product name is not null
        String name = values.getAsString(ItemEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Product requires a name");
        }

        // Check that the price is not null
        Integer price = values.getAsInteger(ItemEntry.COLUMN_PRICE);
        if (price == null) {
            throw new IllegalArgumentException("Product requires price");
        }

        // Check that the quantity is not null
        Integer quantity = values.getAsInteger(ItemEntry.COLUMN_QUANTITY);
        if (quantity == null) {
            throw new IllegalArgumentException("There needs to be at least one item left");
        }

        // Check that there is a supplier name
        String sName = values.getAsString(ItemEntry.COLUMN_SUPPLIER_NAME);
        if (sName == null) {
            throw new IllegalArgumentException("Supplier name required");
        }

        // Check that there is a supplier phone number
        Integer phone = values.getAsInteger(ItemEntry.COLUMN_PHONE_NUMBER);
        if (phone == null) {
            throw new IllegalArgumentException("Supplier phone required");
        }

        // Check that there is a description
        String description = values.getAsString(ItemEntry.COLUMN_DESCRIPTION);
        if (description == null) {
            throw new IllegalArgumentException("Description required");
        }

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new item with the given values
        long id = database.insert(ItemEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the item content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return updateItem(uri, contentValues, selection, selectionArgs);
            case ITEM_ID:
                // For the ITEM_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = ItemEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update items in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more cakes).
     * Return the number of rows that were successfully updated.
     */
    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link ItemEntry#COLUMN_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(ItemEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(ItemEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }

        // If the {@link ItemEntry#COLUMN_PRICE} key is present,
        // check that the value is not null.
        if (values.containsKey(ItemEntry.COLUMN_PRICE)) {
            Integer name = values.getAsInteger(ItemEntry.COLUMN_PRICE);
            if (name == null) {
                throw new IllegalArgumentException("Product requires a price");
            }
        }

        // If the {@link ItemEntry#COLUMN_QUANTITY} key is present,
        // check that the value is not null.
        if (values.containsKey(ItemEntry.COLUMN_QUANTITY)) {
            Integer name = values.getAsInteger(ItemEntry.COLUMN_QUANTITY);
            if (name == null) {
                throw new IllegalArgumentException("Quantity is required");
            }
        }

        // If the {@link ItemEntry#COLUMN_SUPPLIER_NAME} key is present,
        // check that the value is not null.
        if (values.containsKey(ItemEntry.COLUMN_SUPPLIER_NAME)) {
            String name = values.getAsString(ItemEntry.COLUMN_SUPPLIER_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Supplier name required");
            }
        }

        // If the {@link ItemEntry#COLUMN_PHONE_NUMBER} key is present,
        // check that the value is not null.
        if (values.containsKey(ItemEntry.COLUMN_PHONE_NUMBER)) {
            Integer name = values.getAsInteger(ItemEntry.COLUMN_PHONE_NUMBER);
            if (name == null) {
                throw new IllegalArgumentException("Supplier phone required");
            }
        }

        // If the {@link ItemEntry#COLUMN_DESCRIPTION} key is present,
        // check that the value is not null.
        if (values.containsKey(ItemEntry.COLUMN_DESCRIPTION)) {
            String name = values.getAsString(ItemEntry.COLUMN_DESCRIPTION);
            if (name == null) {
                throw new IllegalArgumentException("Description required");
            }
        }

        // In case of nothing to update, just don't...
        if (values.size() == 0) {
            return 0;
        }

        // In other case, get writable database to update the data.
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(ItemEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed.
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writable database.
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted.
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(ItemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ITEM_ID:
                // Delete a single row given by the ID in the URI
                selection = ItemEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(ItemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return ItemEntry.CONTENT_LIST_TYPE;
            case ITEM_ID:
                return ItemEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
