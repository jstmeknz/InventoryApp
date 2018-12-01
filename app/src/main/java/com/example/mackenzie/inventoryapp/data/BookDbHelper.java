package com.example.mackenzie.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.mackenzie.inventoryapp.data.BookContract.BookEntry.COLUMN_BOOK_AUTHOR;
import static com.example.mackenzie.inventoryapp.data.BookContract.BookEntry.COLUMN_BOOK_STYLE;
import static com.example.mackenzie.inventoryapp.data.BookContract.BookEntry.COLUMN_BOOK_TITLE;
import static com.example.mackenzie.inventoryapp.data.BookContract.BookEntry.COLUMN_PRICE;
import static com.example.mackenzie.inventoryapp.data.BookContract.BookEntry.COLUMN_QUANTITY;
import static com.example.mackenzie.inventoryapp.data.BookContract.BookEntry.COLUMN_SUPPLIER;
import static com.example.mackenzie.inventoryapp.data.BookContract.BookEntry.COLUMN_SUPPLIER_PHONE;
import static com.example.mackenzie.inventoryapp.data.BookContract.BookEntry.TABLE_NAME;
import static com.example.mackenzie.inventoryapp.data.BookContract.BookEntry._ID;

public class BookDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "books.db";
    public static final int DATABASE_VERSION = 1;

    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //sets up table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_BOOK_TITLE + " TEXT NOT NULL, "
                + COLUMN_BOOK_AUTHOR + " TEXT NOT NULL, "
                + COLUMN_BOOK_STYLE + " INTEGER, "
                + COLUMN_PRICE + " TEXT DEFAULT 0.00, "
                + COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 1, "
                + COLUMN_SUPPLIER + " TEXT, "
                + COLUMN_SUPPLIER_PHONE + " TEXT);";

        db.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    //(address this in upcoming code improvements)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
