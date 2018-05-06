package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.inventoryapp.data.ItemContract;
import com.example.android.inventoryapp.data.ItemDbHelper;

// Displays list of items that were entered and stored in the app.
public class CatalogActivity extends AppCompatActivity {

    // DB helper that provides us access to the database.
    private ItemDbHelper DbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Instantiating subclass of SQLiteOpenHelper in order to access our database
        // and pass the context, which is the current activity.
        DbHelper = new ItemDbHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    // Method to display info about the state of the database.
    private void displayDatabaseInfo() {
        // Create and/or open a database to read from it.
        SQLiteDatabase db = DbHelper.getReadableDatabase();

        // Defining projection that specifies which database columns will be used after this query
        String[] projection = {
                ItemContract.ItemEntry._ID,
                ItemContract.ItemEntry.COLUMN_PRICE,
                ItemContract.ItemEntry.COLUMN_PRODUCT_NAME,
                ItemContract.ItemEntry.COLUMN_QUANTITY,
                ItemContract.ItemEntry.COLUMN_SUPPLIER_NAME,
                ItemContract.ItemEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

        // Perform a query on the items table
        Cursor cursor = db.query(
                ItemContract.ItemEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        TextView displayView = findViewById(R.id.text_view_item);

        try {
            // Create a header in the Text View that looks like this:
            //
            // The items table contains <number of rows in Cursor> items.
            // _id - price - product name - quantity - supplier name - supplier phone number
            //
            // In the while loop below, iterate through the rows of the cursor and display
            // the information from each column in this order.
            displayView.setText("The items table contains " + cursor.getCount() + " items.\n\n");
            displayView.append(ItemContract.ItemEntry._ID + " - " +
                    ItemContract.ItemEntry.COLUMN_PRICE + " - " +
                    ItemContract.ItemEntry.COLUMN_PRODUCT_NAME + " - " +
                    ItemContract.ItemEntry.COLUMN_QUANTITY + " - " +
                    ItemContract.ItemEntry.COLUMN_SUPPLIER_NAME + " - " +
                    ItemContract.ItemEntry.COLUMN_SUPPLIER_PHONE_NUMBER + "\n");

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry._ID);
            int priceColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_PRICE);
            int productNameColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_PRODUCT_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            // Iterate through all the returned rows in the cursor.
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                int currentPrice = cursor.getInt(priceColumnIndex);
                String currentProdName = cursor.getString(productNameColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentSupName = cursor.getString(supplierNameColumnIndex);
                int currentSupPhone = cursor.getInt(supplierPhoneColumnIndex);
                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append(("\n" + currentID + " - " +
                        currentPrice + " - " +
                        currentProdName + " - " +
                        currentQuantity + " - " +
                        currentSupName + " - " +
                        currentSupPhone));
            }
        } finally {
            // Closing cursor. As requested and advised.
            cursor.close();
        }
    }

    // This method inserts hardcoded data into the database for the app purposes.
    private void insertItem() {
        SQLiteDatabase db = DbHelper.getWritableDatabase();

        // Create a ContentValues object with column names as the keys and made up
        // attributes as the values.
        ContentValues values = new ContentValues();
        values.put(ItemContract.ItemEntry.COLUMN_PRICE, 100);
        values.put(ItemContract.ItemEntry.COLUMN_PRODUCT_NAME, "Serenity");
        values.put(ItemContract.ItemEntry.COLUMN_QUANTITY, 1);
        values.put(ItemContract.ItemEntry.COLUMN_SUPPLIER_NAME, "The Almighty");
        values.put(ItemContract.ItemEntry.COLUMN_SUPPLIER_PHONE_NUMBER, 356894);

        // Insert a new row for item in the database, returning the ID of that new row.
        // The first argument for db.insert() is the items table name.
        // The second argument provides the name of a column in which the framework
        // can insert NULL in the event that the ContentValues is empty (if
        // this is set to "null", then the framework will not insert a row when
        // there are no values).
        // The third argument is the ContentValues object containing the info for item.
        long newRowId = db.insert(ItemContract.ItemEntry.TABLE_NAME, null, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // This method adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option.
        switch (item.getItemId()) {
            // Respond to a click on the "Insert fake data".
            case R.id.action_insert_fake_data:
                insertItem();
                displayDatabaseInfo();
                return true;
            //Respond to a click on the "Delete all entries".
            case R.id.action_delete_everything:
                // Do nothing for now.
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
