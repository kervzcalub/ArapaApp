package com.arapa.app.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cursoradapter.widget.CursorAdapter;

import com.arapa.app.R;

import java.text.DecimalFormat;


public class MyCursorAdapter extends CursorAdapter {

    public MyCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate the layout for the list item view
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.suggestion_layout, parent, false);
        return view;
    }

    @Override
    @SuppressLint("Range")
    public void bindView(View view, Context context, Cursor cursor) {
        // Get the data from the cursor and set it on the view
        String name_suggestion = cursor.getString(cursor.getColumnIndex("suggest_school_1"));
        String address_suggestion = cursor.getString(cursor.getColumnIndex("suggest_school_2"));
        double distance_km = cursor.getDouble(cursor.getColumnIndex("school_distance"));
        byte[] logo_byte_array = cursor.getBlob(cursor.getColumnIndex("suggest_school_logo"));
        Bitmap logo_bitmap = BitmapFactory.decodeByteArray(logo_byte_array, 0, logo_byte_array.length);

        DecimalFormat df = new DecimalFormat("#.##");
        String distance_format = df.format(distance_km);

        ImageView logo = view.findViewById(R.id.school_logo_suggest);
        logo.setImageBitmap(logo_bitmap);
        TextView name = view.findViewById(R.id.school_name_suggest);
        TextView address = view.findViewById(R.id.school_address_suggest);
        TextView distance = view.findViewById(R.id.school_distance_suggest);

        name.setText(name_suggestion);
        address.setText(address_suggestion);
        distance.setText("Distance: " + distance_format + "KM");
    }
}
