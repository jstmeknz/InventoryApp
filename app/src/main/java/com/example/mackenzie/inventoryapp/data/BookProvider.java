package com.example.mackenzie.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import static com.example.mackenzie.inventoryapp.data.BookContract.BookEntry;
import static com.example.mackenzie.inventoryapp.data.BookContract.CONTENT_AUTHORITY;
import static com.example.mackenzie.inventoryapp.data.BookContract.PATH_BOOKS;

public class BookProvider extends ContentProvider {

    public static final String LOG_TAG = BookProvider.class.getSimpleName();

    private static final int BOOKS = 100;
    private static final int BOOK_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_BOOKS, BOOKS);

        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_BOOKS + "/#", BOOK_ID);
    }

    private BookDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new BookDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        //Holds query result
        Cursor cursor;

        //Find out if Uri matcher can match the Uri to specified code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertBook(Uri uri, ContentValues values) {
        String name = values.getAsString(BookEntry.COLUMN_BOOK_TITLE);

        String author = values.getAsString(BookEntry.COLUMN_BOOK_AUTHOR);

        String supplier = values.getAsString(BookEntry.COLUMN_SUPPLIER);

        String supplierNumber = values.getAsString(BookEntry.COLUMN_SUPPLIER_PHONE);

        Integer quantity = values.getAsInteger(BookEntry.COLUMN_QUANTITY);

        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Requires name");
        }
        if (TextUtils.isEmpty(author)) {
            throw new IllegalArgumentException("Requires author");
        }
        if (TextUtils.isEmpty(supplier)) {
            throw new IllegalArgumentException("Requires supplier name");
        }
        if (TextUtils.isEmpty(supplierNumber)) {
            throw new IllegalArgumentException("Requires supplier phone number");
        }
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Valid quantity required");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(BookEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // Deletes all rows that match the selection and selection args
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOK_ID:
                // Delete a single row given by the ID in the URI
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
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
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, values, selection, selectionArgs);
            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update not supported for " + uri);
        }
    }

    /**
     * @param uri
     * @param values
     * @param selection
     * @param selectionArgs
     * @return
     */
    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(BookEntry.COLUMN_BOOK_TITLE)) {
            String name = values.getAsString(BookEntry.COLUMN_BOOK_TITLE);
            if (name == null) {
                throw new IllegalArgumentException("Book requires title");
            }
        }
        if (values.containsKey(BookEntry.COLUMN_BOOK_AUTHOR)) {
            String author = values.getAsString(BookEntry.COLUMN_BOOK_AUTHOR);
            if (author == null) {
                throw new IllegalArgumentException("Book requires author name");
            }
        }

        if (values.containsKey(BookEntry.COLUMN_BOOK_STYLE)) {
            Integer style = values.getAsInteger(BookEntry.COLUMN_BOOK_STYLE);
            if (style == null || !BookEntry.isValidStyle(style)) {
                throw new IllegalArgumentException("Book requires style");
            }
        }
        if (values.containsKey(BookEntry.COLUMN_QUANTITY)) {
            Integer quantity = values.getAsInteger(BookEntry.COLUMN_QUANTITY);
            if ((quantity == null) && (quantity == 0)) {
                throw new IllegalArgumentException("Book requires valid quantity in stock");
            }
        }
        if (values.containsKey(BookEntry.COLUMN_SUPPLIER)) {
            String supplier = values.getAsString(BookEntry.COLUMN_SUPPLIER);
            if (supplier == null) {
                throw new IllegalArgumentException("Requires supplier name");
            }
        }
        if (values.containsKey(BookEntry.COLUMN_SUPPLIER_PHONE)) {
            String supplierPhone = values.getAsString(BookEntry.COLUMN_SUPPLIER_PHONE);
            if (supplierPhone == null) {
                throw new IllegalArgumentException("Requires supplier phone number");
            }
        }

        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //checking for any changes
        int rowsUpdated = database.update(BookEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            try {
                getContext().getContentResolver().notifyChange(uri, null);
            } catch (NullPointerException nullPointer) {
                Log.e(LOG_TAG, "Notify of change failed! " + nullPointer);
            }
        }
        return rowsUpdated;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

}
