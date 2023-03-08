package com.arapa.app.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.arapa.app.R;
import com.arapa.app.util.School;

import java.util.ArrayList;

public class CoursesFragment extends Fragment {

    private School school;
    private ListView courses_listView;
    private ArrayList<String> courses;

    public CoursesFragment(School school) {
        this.school = school;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_courses, container, false);

        courses_listView = view.findViewById(R.id.courseList);
        courses = school.getCourses_list();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, courses);
        courses_listView.setAdapter(adapter);
        courses_listView.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        view.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                view.onTouchEvent(motionEvent);
                return true;
            }
        });
        return view;
    }
}