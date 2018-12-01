package com.example.mackenzie.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import static com.example.mackenzie.inventoryapp.data.BookContract.BookEntry;

public class BookCursorAdapter extends CursorAdapter {

    public static final String LOG_TAG = BookCursorAdapter.class.getSimpleName();

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        TextView titleView = (TextView) view.findViewById(R.id.book_name);
        TextView authorView = (TextView) view.findViewById(R.id.author);
        TextView styleView = (TextView) view.findViewById(R.id.book_style);
        TextView priceView = (TextView) view.findViewById(R.id.book_price);
        TextView quantityView = (TextView) view.findViewById(R.id.quantity);
        TextView supplierView = (TextView) view.findViewById(R.id.supplier);
        final TextView supplierPhoneView = (TextView) view.findViewById(R.id.supplier_phone);
        Button saleButton = (Button) view.findViewById(R.id.sale_button);

        int titleColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_TITLE);
        int authorColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_AUTHOR);
        int styleColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_STYLE);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
        final int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);
        int supplierColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER);
        final int phoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE);
        final int idIndex = cursor.getColumnIndex(BookEntry._ID);

        String bookTitle = cursor.getString(titleColumnIndex);
        final String author = cursor.getString(authorColumnIndex);
        String price = cursor.getString(priceColumnIndex);
        String supplier = cursor.getString(supplierColumnIndex);
        final String phone = cursor.getString(phoneColumnIndex);
        String bookStyle = cursor.getString(styleColumnIndex);
        final String quantity = cursor.getString(quantityColumnIndex);
        final String bookId = cursor.getString(idIndex);

        if (TextUtils.isEmpty(bookStyle)) {
            bookStyle = context.getString(R.string.book_style_unknown);
        }

        //sale decrement button listener
        saleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                BookLog Activity = (BookLog) context;
                Activity.saleQuantityCount(Integer.valueOf(bookId), Integer.valueOf(quantity));
            }
        });

        //sets text for views
        titleView.setText(bookTitle);
        authorView.setText(author);
        styleView.setText(bookStyle);
        priceView.setText(price);
        quantityView.setText(quantity);
        supplierView.setText(supplier);
        supplierPhoneView.setText(phone);
    }
}
