package com.arapa.app.util;

import android.content.Context;
import android.database.MatrixCursor;
import android.os.AsyncTask;
import android.util.Log;

import com.arapa.app.ui.MyCursorAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SearchTask extends AsyncTask<String, Void, MatrixCursor> {

    private Context context;
    private ArrayList<School> schoolArrayList;
    private MyCursorAdapter cursor_adapter;

    public SearchTask(Context context, ArrayList<School> schoolArrayList, MyCursorAdapter cursor_adapter) {
        this.context = context;
        this.schoolArrayList = schoolArrayList;
        this.cursor_adapter = cursor_adapter;
    }
    @Override
    protected MatrixCursor doInBackground(String... params) {
        String newText = params[0];
        ArrayList<School> matchingSchools = new ArrayList<>();
        for (School school : schoolArrayList) {
            if (school.getName().toLowerCase().contains(newText.toLowerCase()) ||
                    school.getAddress().toLowerCase().contains(newText.toLowerCase())) {

                matchingSchools.add(school);
            }
        }



        // sort matchingSchools by distance and then by name
        // so if the user if in the center of the two schools
        // means, the distance is the same
        // sort the two of them by name.
        Collections.sort(matchingSchools, new Comparator<School>() {
            @Override
            public int compare(School school1, School school2) {
                int distanceComparison = Double.compare(school1.getDistance(), school2.getDistance());
                if (distanceComparison != 0) {
                    return distanceComparison;
                } else {
                    return school1.getName().compareTo(school2.getName());
                }
            }
        });

        MatrixCursor cursor = new MatrixCursor(new String[] {"_id", "suggest_school_1", "suggest_school_2", "school_distance", "suggest_school_logo", "suggest_school_id"});

        for (int i = 0; i < matchingSchools.size(); i++) {
            School school = matchingSchools.get(i);
            Log.i("SCHOOL", "DISTANCE: " + school.getDistance());
            cursor.addRow(new Object[] {i, school.getName(), school.getAddress(), school.getDistance(), Utils.getBitmapAsByteArray(Utils.getSchoolLogo(context, school)), school.getSchool_id()});
        }

        return cursor;
    }

    @Override
    protected void onPostExecute(MatrixCursor cursor) {
        cursor_adapter.changeCursor(cursor);
        cursor_adapter.notifyDataSetChanged();
    }
}
