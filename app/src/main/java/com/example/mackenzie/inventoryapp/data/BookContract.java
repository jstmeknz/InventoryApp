package com.example.mackenzie.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class BookContract {
    //Content Authority is the name for the entire content provider
    public static final String CONTENT_AUTHORITY = "com.example.mackenzie.inventoryapp";
    //Use CONTENT_AUTHORITY to create the base of all the URI's which app will use to contact the content provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    //Possible path to append to base Uri
    public static final String PATH_BOOKS = "books";

    private BookContract() {
    }

    public static final class BookEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);
        public final static String TABLE_NAME = "books";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_BOOK_TITLE = "title";
        public final static String COLUMN_BOOK_AUTHOR = "author";
        public final static String COLUMN_BOOK_STYLE = "style";
        public final static String COLUMN_PRICE = "price";
        public final static String COLUMN_QUANTITY = "quantity";
        public final static String COLUMN_SUPPLIER = "supplier";
        public final static String COLUMN_SUPPLIER_PHONE = "phone";

        public static final int STYLE_HARDBACK = 0;
        public static final int STYLE_PAPERBACK = 1;
        public static final int STYLE_AUDIO = 2;
        public static final int STYLE_DOWNLOAD = 3;

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        public final static boolean isValidStyle(int style) {
            return style == STYLE_HARDBACK ||
                    style == STYLE_PAPERBACK ||
                    style == STYLE_AUDIO ||
                    style == STYLE_DOWNLOAD;
        }
    }
}

