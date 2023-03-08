package com.arapa.app;

import static com.arapa.app.util.Utils.jsonFile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arapa.app.ui.MyAdapter;
import com.arapa.app.util.School;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import androidx.appcompat.*;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Serializable, MyAdapter.ItemSelected, AdapterView.OnItemSelectedListener {

    private String[] categories = {"Select School Level", "Primary", "High School", "Senior High School", "College"};
    private SearchView searchBar;
    private Spinner schoolCategory;
    private MyAdapter adapter;
    private School school;
    private ArrayList<School> schoolArrayList;
    private ArrayList<School> sortedSchool;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        schoolCategory = findViewById(R.id.school_category_spinner);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, categories);
        schoolCategory.setAdapter(categoryAdapter);
        schoolCategory.setOnItemSelectedListener(this);

        schoolArrayList = new ArrayList<>();
        sortedSchool = new ArrayList<>();
        init_data();


        recyclerView = findViewById(R.id.school_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter(this, sortedSchool, this);
        recyclerView.setAdapter(adapter);

        searchBar = findViewById(R.id.search_bar);
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 0) {
                    adapter.isSearching(true, newText);
                    filterSchool(newText);
                } else {
                    adapter.isSearching(false, "");
                    adapter.setData(sortedSchool);
                    adapter.notifyDataSetChanged();
                }
                return false;
            }
        });

    }

    private void filterSchool(String text) {
        ArrayList<School> schoolList = adapter.getData();
        ArrayList<School> filteredSchool = new ArrayList<>();

        for (School school : schoolList) {
            if (school.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredSchool.add(school);
            }
        }

        adapter.setData(filteredSchool);
        adapter.notifyDataSetChanged();
    }

    private void init_data() {
        if (!jsonFile(this).isEmpty()) {
            try {
                JSONArray schoolArray = new JSONArray(jsonFile(MainActivity.this));

                for (int j = 0; j < schoolArray.length(); j++) {
                    JSONObject schoolData = schoolArray.getJSONObject(j);
                    int status = schoolData.getInt("status");
                    // newly added school should be approved first by the admin
                    if (status == 1) { // 1=approved, 0=not approved
                        JSONArray courses = schoolData.getJSONArray("Courses");
                        ArrayList<String> courseList = new ArrayList<>();
                        if (courseList.size() > 0) {
                            courseList.clear();
                        }
                        for (int i = 0; i < courses.length(); i++) {
                            String c = courses.getJSONObject(i).getString("value");
                            courseList.add(c);
                        }
                        String school_id = schoolData.getString("school_id");
                        String name = schoolData.getString("Name");
                        String address = schoolData.getString("Address");
                        String contact = schoolData.getString("Contact");
                        String description = schoolData.getString("Description");
                        String email = schoolData.getString("Email");
                        String type = schoolData.getString("SchoolType");
                        String web = schoolData.getString("Weblink");
                        JSONArray mapData = schoolData.getJSONArray("Map");
                        double latitude = mapData.getJSONObject(0).getDouble("Lat");
                        double longitude = mapData.getJSONObject(0).getDouble("Lng");
                        school = new School(school_id, name, address, contact, description, email, type, web, courseList, latitude, longitude);
                        schoolArrayList.add(school);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void itemSelected(School school) {
        Intent intent = new Intent(this, SchoolActivity.class);
        intent.putExtra("SCHOOL", school);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //"Select School Level" = 0
        // "Primary" = 1
        // "High School" = 2
        // "Senior High School" = 3
        // "College" = 4
        if (sortedSchool.size() > 0) {
            sortedSchool.clear();
        }
        Log.d("SELECTED", "POS: " + i);
        switch (i) {
            case 1:
                for (School schools : schoolArrayList) {
                    if (schools.getType().equals("Primary")) {
                        Log.d("TYPE", "SCHOOL TYPE: " + schools.getType());
                        sortedSchool.add(schools);
                    }
                }
                break;
            case 2:
                for (School schools : schoolArrayList) {
                    if (schools.getType().equals("High School")) {
                        Log.d("TYPE", "SCHOOL TYPE: " + schools.getType());
                        sortedSchool.add(schools);
                    }
                }
                break;
            case 3:
                for (School schools : schoolArrayList) {
                    if (schools.getType().equals("Senior High School")) {
                        Log.d("TYPE", "SCHOOL TYPE: " + schools.getType());
                        sortedSchool.add(schools);
                    }
                }
                break;
            case 4:
                for (School schools : schoolArrayList) {
                    if (schools.getType().equals("College")) {
                        Log.d("TYPE", "SCHOOL TYPE: " + schools.getType());
                        sortedSchool.add(schools);
                    }
                }
                break;
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}