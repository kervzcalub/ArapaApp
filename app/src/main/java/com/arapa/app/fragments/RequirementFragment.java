package com.arapa.app.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arapa.app.R;
import com.arapa.app.util.School;

public class RequirementFragment extends Fragment {

    private School school;

    public RequirementFragment(School school) {
        this.school = school;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_requirement, container, false);
        TextView requirement = view.findViewById(R.id.requirement);
        requirement.setText(school.getRequirements());

        return view;
    }
}