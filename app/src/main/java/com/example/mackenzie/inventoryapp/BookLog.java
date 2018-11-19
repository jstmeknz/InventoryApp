package com.example.mackenzie.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mackenzie.inventoryapp.data.BookContract.BookEntry;

import static com.example.mackenzie.inventoryapp.data.BookContract.BookEntry._ID;

public class BookLog extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BOOK_LOADER = 0;
    BookCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_log);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookLog.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with data
        final ListView bookListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of data in the Cursor.
        mCursorAdapter = new BookCursorAdapter(this, null);
        bookListView.setAdapter(mCursorAdapter);

        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(BookLog.this, EditorActivity.class);

                Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);

                intent.setData(currentBookUri);

                startActivity(intent);
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }

    //Add menu to the app bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_log_menu, menu);
        return true;
    }

    //inserts dummy data into table to show that everything is working properly when first case is tapped.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertBook();
                return true;
            case R.id.action_delete:
                clearBooks();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //dummy data entered into table
    //((for price, when making UI insert localized money symbol ($..)))
    private void insertBook() {
        ContentValues cv = new ContentValues();
        cv.put(BookEntry.COLUMN_BOOK_TITLE, " The Giver");
        cv.put(BookEntry.COLUMN_BOOK_AUTHOR, " Lois Lowry");
        cv.put(BookEntry.COLUMN_BOOK_STYLE, " Paperback");
        cv.put(BookEntry.COLUMN_PRICE, " 5.00");
        cv.put(BookEntry.COLUMN_QUANTITY, 3);
        cv.put(BookEntry.COLUMN_SUPPLIER, " The Book Guy");
        cv.put(BookEntry.COLUMN_SUPPLIER_PHONE, " 555-555-5555");

        Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, cv);
    }

    //Deletes all data while preserving table schema
    //Resets _ID that is displayed back to "0"
    private void clearBooks() {
        int rowsDeleted = getContentResolver().delete(BookEntry.CONTENT_URI, null, null);
        Log.v("BookLog", rowsDeleted + " rows deleted from book database");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //Defines a projection that specifies the columns from table
        String[] projection = {
                _ID,
                BookEntry.COLUMN_BOOK_TITLE,
                BookEntry.COLUMN_BOOK_AUTHOR,
                BookEntry.COLUMN_BOOK_STYLE,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_SUPPLIER,
                BookEntry.COLUMN_SUPPLIER_PHONE
        };
        return new CursorLoader(this,   // Parent activity context
                BookEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link BookCursorAdapter} with this new cursor containing updated data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }

    public void saleQuantityCount(int id, int quantity) {
        quantity = quantity - 1;
        if (quantity >= 0) {
            ContentValues values = new ContentValues();
            values.put(BookEntry.COLUMN_QUANTITY, quantity);
            Uri updateUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
            int newQuantity = getContentResolver().update(updateUri, values, null, null);
            Toast.makeText(this, "Book count decreased", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Stock count has reached 0, please reorder", Toast.LENGTH_SHORT).show();

        }
    }
}

//
//    Intent intent = new Intent(Intent.ACTION_SENDTO);
//        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
//                intent.putExtra(Intent.EXTRA_SUBJECT, "Just Java Order for " + name);
//                intent.putExtra(Intent.EXTRA_TEXT, priceMessage);
//                if (intent.resolveActivity(getPackageManager()) != null) {
//                startActivity(intent);
//                }


// mAddButton = (Button) findViewById(R.id.add_button);
//         mSubtractButton = (Button) findViewById(R.id.subtract_button);
//         mAddButton.setOnClickListener(new View.OnClickListener() {
//@Override
//public void onClick(View view) {
//        int currentQuantity = Integer.parseInt(mQuantity.getText().toString());
//        int increase;
//        increase = currentQuantity + 1;
//        mQuantity.setText(String.valueOf(increase));
//        }
//        });
//
//        mSubtractButton.setOnClickListener(new View.OnClickListener() {
//@Override
//public void onClick(View view) {
//        int currentQuantity = Integer.parseInt(mQuantity.getText().toString());
//        if (currentQuantity > 0) {
//        int subtractQuantity = currentQuantity - 1;
//        mQuantity.setText(String.valueOf(subtractQuantity));
//        }
//        }
//        });
