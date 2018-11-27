package com.example.mackenzie.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.mackenzie.inventoryapp.data.BookContract.BookEntry;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    //log tag
    public static final String LOG_TAG = EditorActivity.class.getSimpleName();

    //initializing the following:
    private static final int EXISTING_BOOK_LOADER = 0;
    int quantity = 1;
    private Uri mCurrentBookUri;
    private EditText mBookTitleEditText;
    private EditText mBookAuthorEditText;
    private EditText mPriceEditText;
    private EditText mSupplier;
    private EditText mSupplierPhone;
    private TextView mCurrentQuantityView;
    private Button mAddButton;
    private Button mSubtractButton;
    private Button mCallButton;
    private Spinner mBookStyleSpinner;
    private int mBookStyle = BookEntry.STYLE_HARDBACK;

    private boolean mBookHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        final Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        //set title text on whether edit activity is accessed from an existing item or new.
        //Sets reorder button in the same way
        mCallButton = (Button) findViewById(R.id.call_button);
        if (mCurrentBookUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_book));
            invalidateOptionsMenu();
            mCallButton.setVisibility(View.GONE);
        } else {
            setTitle(getString(R.string.edit_book_title));
            mCallButton.setVisibility(View.VISIBLE);

            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        //find views to populate with data from user input
        mBookTitleEditText = (EditText) findViewById(R.id.edit_book_title);
        mBookAuthorEditText = (EditText) findViewById(R.id.edit_book_author);
        mBookStyleSpinner = (Spinner) findViewById(R.id.spinner_style);
        mPriceEditText = (EditText) findViewById(R.id.edit_book_price);
        mCurrentQuantityView = (TextView) findViewById(R.id.quantity);
        mSupplier = (EditText) findViewById(R.id.edit_supplier_name);
        mSupplierPhone = (EditText) findViewById(R.id.edit_supplier_phone);

        //set listeners
        mBookTitleEditText.setOnTouchListener(mTouchListener);
        mBookAuthorEditText.setOnTouchListener(mTouchListener);
        mBookStyleSpinner.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);

        setupSpinner();

        //initializing buttons
        mAddButton = (Button) findViewById(R.id.add_button);
        mSubtractButton = (Button) findViewById(R.id.subtract_button);
        mCallButton = (Button) findViewById(R.id.call_button);

        //increment/decrement buttons
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentQuantityView.setText(incrementCount());
            }
        });
        mSubtractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentQuantityView.setText(decrementCount());
            }
        });

        //Listener for reorder button in editor activity. using action_dial since it doesn't require permissions
        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneString = mSupplierPhone.getText().toString().trim();
                Intent supPhone = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneString, null));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(supPhone);
                }
            }
        });
    }

    private void setupSpinner() {
        ArrayAdapter styleSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.book_style_options, android.R.layout.simple_spinner_item);

        styleSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mBookStyleSpinner.setAdapter(styleSpinnerAdapter);

        mBookStyleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            //sets listener to each option in drop down
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.book_style_hardback))) {
                        mBookStyle = BookEntry.STYLE_HARDBACK;
                    } else if (selection.equals(getString(R.string.book_style_paperback))) {
                        mBookStyle = BookEntry.STYLE_PAPERBACK;
                    } else if (selection.equals(getString(R.string.book_style_audio))) {
                        mBookStyle = BookEntry.STYLE_AUDIO;
                    } else if (selection.equals(getString(R.string.book_style_download))) {
                        mBookStyle = BookEntry.STYLE_DOWNLOAD;
                    }
                }
            }

            //sets style to hardback if nothing is selected
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mBookStyle = BookEntry.STYLE_HARDBACK;
            }
        });
    }

    //when add button is clicked
    public String incrementCount() {
        int quantityCount;
        String bookQuantity = mCurrentQuantityView.getText().toString();
        if (bookQuantity.equalsIgnoreCase("")) {
            quantityCount = quantity + 1;
        } else {
            quantityCount = Integer.parseInt(bookQuantity);
            quantityCount++;
        }
        return String.valueOf(quantityCount);
    }

    //when subtract button is clicked, will not go below 0
    public String decrementCount() {
        int quantityCount;
        String bookQuantity = mCurrentQuantityView.getText().toString();
        if (TextUtils.isEmpty(bookQuantity)) {
            Toast.makeText(this, "Quantity cannot be negative.", Toast.LENGTH_SHORT).show();
            return bookQuantity;
        } else {
            quantityCount = Integer.parseInt(bookQuantity);
            if (quantityCount > 0)
                quantityCount--;
        }
        return String.valueOf(quantityCount);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new book the "Delete" menu item will be hidden
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save to database
                saveBook();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the book hasn't changed, continue with navigating up to parent activity
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Get user input from editor and save into db
     */
    private void saveBook() {
        //Read from input fields, use trim to eliminate leading or trailing white space
        String titleString = mBookTitleEditText.getText().toString().trim();
        String authorString = mBookAuthorEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String currentQuantity = mCurrentQuantityView.getText().toString();
        String supplierString = mSupplier.getText().toString().trim();
        String supplierPhoneString = mSupplierPhone.getText().toString().trim();

        //check if this is a new book entry and make sure all fields are blank in editor
        if (mCurrentBookUri == null &&
                TextUtils.isEmpty(titleString) &&
                TextUtils.isEmpty(authorString) &&
                TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(currentQuantity) &&
                TextUtils.isEmpty(supplierString) &&
                TextUtils.isEmpty(supplierPhoneString) &&
                mBookStyle == (BookEntry.STYLE_HARDBACK) &&
                quantity == 1) {
            return;
        }

        //Create a ContentValues object where column names are keys and
        //attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_TITLE, titleString);
        values.put(BookEntry.COLUMN_BOOK_AUTHOR, authorString);
        values.put(BookEntry.COLUMN_BOOK_STYLE, mBookStyle);
        values.put(BookEntry.COLUMN_PRICE, priceString);
        values.put(BookEntry.COLUMN_QUANTITY, currentQuantity);
        values.put(BookEntry.COLUMN_SUPPLIER, supplierString);
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE, supplierPhoneString);

        if (mCurrentBookUri == null) {
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

            //show toast depending on whether insertion was successful or not
            if (newUri == null) {
                //if new URI is null there was an error
                Toast.makeText(this, getString(R.string.editor_insert_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                //show successful toast
                Toast.makeText(this, getString(R.string.editor_book_added),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);

            //shows a toast depending on if update is successful or not
            if (rowsAffected == 0) {
                //no rows affected means error
                Toast.makeText(this, getString(R.string.editor_update_error),
                        Toast.LENGTH_SHORT).show();
            } else {
                //otherwise, show successful toast
                Toast.makeText(this, getString(R.string.editor_update_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Delete confirmation dialog
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked DELETE.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                //User clicked CANCEL, continue editing.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Delete book
     */
    private void deleteBook() {
        // Will only delete book if it already exists
        if (mCurrentBookUri != null) {
            // Call the ContentResolver to delete book at given content URI.
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        // Close the activity
        finish();
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for positive and negative buttons in dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss and keep editing
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        // If the book hasn't changed, continue with handling back button press
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };
        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //Defines projection that creates table with all columns from book table
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_TITLE,
                BookEntry.COLUMN_BOOK_AUTHOR,
                BookEntry.COLUMN_BOOK_STYLE,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_SUPPLIER,
                BookEntry.COLUMN_SUPPLIER_PHONE};

        //Loader-executes ContentProvider's query on background thread
        return new CursorLoader(this, //Parent activity context
                mCurrentBookUri, //Query the content URI for the current book
                projection,//Columns to include in the resulting Cursor
                null, //No selection clause
                null, //No selection arguments
                null); //Default sort order
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //Return early if Cursor is null or there is less that 1 row in Cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        //Find column attributes
        if (cursor.moveToFirst()) {
            int titleColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_TITLE);
            int authorColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_AUTHOR);
            int styleColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_STYLE);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER);
            int phoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE);

            //Extract value from the Cursor for given column index
            String bookTitle = cursor.getString(titleColumnIndex);
            String author = cursor.getString(authorColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            String phone = cursor.getString(phoneColumnIndex);
            int bookStyle = cursor.getInt(styleColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);

            //Update the views with values from database
            mBookTitleEditText.setText(bookTitle);
            mBookAuthorEditText.setText(author);
            mPriceEditText.setText(price);
            mSupplier.setText(supplier);
            mSupplierPhone.setText(phone);
            mCurrentQuantityView.setText(Integer.toString(quantity));

            switch (bookStyle) {
                case BookEntry.STYLE_HARDBACK:
                    mBookStyleSpinner.setSelection(1);
                    break;
                case BookEntry.STYLE_PAPERBACK:
                    mBookStyleSpinner.setSelection(2);
                    break;
                case BookEntry.STYLE_AUDIO:
                    mBookStyleSpinner.setSelection(3);
                    break;
                case BookEntry.STYLE_DOWNLOAD:
                    mBookStyleSpinner.setSelection(4);
                    break;
                default:
                    mBookStyleSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //If loader is invalidated, reset all entries in fields
        mBookTitleEditText.setText("");
        mBookAuthorEditText.setText("");
        mPriceEditText.setText("");
        mCurrentQuantityView.setText("");
        mSupplier.setText("");
        mSupplierPhone.setText("");
        mBookStyleSpinner.setSelection(0);
    }

}
