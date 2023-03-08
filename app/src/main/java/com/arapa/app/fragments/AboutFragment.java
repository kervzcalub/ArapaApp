package com.arapa.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arapa.app.R;
import com.arapa.app.ui.ImageAdapter;
import com.arapa.app.util.School;
import com.arapa.app.util.SchoolImages;
import com.arapa.app.util.Utils;

public class AboutFragment extends Fragment {
    private School school;

    private TextView school_description;
    private RecyclerView imagesView;
    private ImageAdapter imageAdapter;
    public AboutFragment(School school) {
        this.school = school;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        school_description = view.findViewById(R.id.description);

        SchoolImages schoolImages = new SchoolImages();

        imagesView = view.findViewById(R.id.school_image_view);
        imageAdapter = new ImageAdapter(getContext(), schoolImages);

        // Create a new GridLayoutManager with 3 columns
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        // Set the GridLayoutManager as the layout manager for your RecyclerView
        imagesView.setLayoutManager(gridLayoutManager);

        imagesView.setAdapter(imageAdapter);

        try {
            Utils.downloadImages(getContext(), school, schoolImages, imageAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        school_description.setText(school.getDescription());
        return view;
    }
}