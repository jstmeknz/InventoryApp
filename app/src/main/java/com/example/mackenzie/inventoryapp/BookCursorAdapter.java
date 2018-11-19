package com.example.mackenzie.inventoryapp;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mackenzie.inventoryapp.data.BookContract;

import static com.example.mackenzie.inventoryapp.data.BookContract.*;

public class BookCursorAdapter extends CursorAdapter {

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    public static final String LOG_TAG = BookCursorAdapter.class.getSimpleName();

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
        TextView supplierPhoneView = (TextView) view.findViewById(R.id.supplier_phone);
        ImageButton saleButton = (ImageButton) view.findViewById(R.id.sale_button);
        ImageButton editButton = (ImageButton) view.findViewById(R.id.edit_button);


        int titleColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_TITLE);
        int authorColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_AUTHOR);
        int styleColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_STYLE);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
        final int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);
        int supplierColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER);
        int phoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE);
        final int idIndex = cursor.getColumnIndex(BookEntry._ID);

        String bookTitle = cursor.getString(titleColumnIndex);
        String author = cursor.getString(authorColumnIndex);
        String price = cursor.getString(priceColumnIndex);
        String supplier = cursor.getString(supplierColumnIndex);
        String phone = cursor.getString(phoneColumnIndex);
        String bookStyle = cursor.getString(styleColumnIndex);
        final String quantity = cursor.getString(quantityColumnIndex);
        final String bookId = cursor.getString(idIndex);

            if (TextUtils.isEmpty(bookStyle)) {
                bookStyle = context.getString(R.string.book_style_hardback);
            }

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookLog Activity = (BookLog) context;
                Activity.saleQuantityCount(Integer.valueOf(bookId), Integer.valueOf(quantity));
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), EditorActivity.class);

                Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, Long.parseLong(bookId));

                intent.setData(currentBookUri);

                context.startActivity(intent);
            }
        });

//            addButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    EditorActivity Activity = (EditorActivity)context;
//                    Activity.increment(Integer.valueOf(bookId), Integer.valueOf(quantity));
//                }
//            });
//
//            subtractButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    EditorActivity Activity = (EditorActivity)context;
//                    Activity.decrement(Integer.valueOf(bookId), Integer.valueOf(quantity));
//                }
//            });

        titleView.setText(bookTitle);
        authorView.setText(author);
        styleView.setText(bookStyle);
        priceView.setText(price);
        quantityView.setText(quantity);
        supplierView.setText(supplier);
        supplierPhoneView.setText(phone);
    }
}


