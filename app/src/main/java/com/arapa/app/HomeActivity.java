package com.arapa.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arapa.app.ui.ImageAdapter;
import com.arapa.app.ui.MyAdapter;
import com.arapa.app.ui.MyCursorAdapter;
import com.arapa.app.ui.SchoolViewDialog;
import com.arapa.app.util.School;
import com.arapa.app.util.SchoolImages;
import com.arapa.app.util.SearchTask;
import com.arapa.app.util.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.OnLocationClickListener;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.turf.TurfMeasurement;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends ActivityBase implements OnMapReadyCallback, SearchView.OnQueryTextListener, SearchView.OnSuggestionListener, CompoundButton.OnCheckedChangeListener {

    private SearchView searchView;


    //private ConstraintLayout details_holder;

    private MyCursorAdapter cursor_adapter;

    private MapView mapView;
    private MapboxMap mapboxMap;
    private LocationComponent locationComponent;
    private LocationComponentActivationOptions locationComponentActivationOptions;

    private SearchTask searchtask;

    private MaterialCheckBox primary_checkbox, highschool_checkbox, seniorhigh_checkbox, college_checkbox;

    private RecyclerView schoolListView;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        permissionGranted();
        init_school_data();
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_home);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        searchView = findViewById(R.id.search_bar);
        searchView.setOnQueryTextListener(this);
        searchView.setOnSuggestionListener(this);

        primary_checkbox = findViewById(R.id.primary_checkbox);
        highschool_checkbox = findViewById(R.id.highschool_checkbox);
        seniorhigh_checkbox = findViewById(R.id.seniorhigh_checkbox);
        college_checkbox = findViewById(R.id.college_checkbox);
        primary_checkbox.setOnCheckedChangeListener(this);
        highschool_checkbox.setOnCheckedChangeListener(this);
        seniorhigh_checkbox.setOnCheckedChangeListener(this);
        college_checkbox.setOnCheckedChangeListener(this);



        schoolListView = findViewById(R.id.school_list);
        schoolListView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new MyAdapter(this, sortedSchool, this);
        schoolListView.setAdapter(myAdapter);
    }

    private void showDetailsDialog() {
        new SchoolViewDialog(this, school).show(getSupportFragmentManager(), "SCHOOL VIEW DIALOG");
    }

    private void setup_search_cursor() {
        String[] columns = {"_id", "suggest_school_1", "suggest_school_2", "school_distance", "suggest_school_logo", "suggest_school_id"};
        MatrixCursor cursor = new MatrixCursor(columns);
        for (int i = 0; i < schoolArrayList.size(); i++) {
            School school = schoolArrayList.get(i);
            cursor.addRow(new Object[]{i, school.getName(), school.getAddress(), school.getDistance(), Utils.getBitmapAsByteArray(Utils.getSchoolLogo(this, school)), school.getSchool_id()});
        }
        cursor_adapter = new MyCursorAdapter(this, cursor);
        searchView.setSuggestionsAdapter(cursor_adapter);
        cursor_adapter.changeCursor(cursor);
    }

    private void move_camera(School school) {
        double longitude = school.getLongitude();
        double latitude = school.getLatitude();
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .zoom(15)
                .tilt(20)
                .build();
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000);

    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @SuppressLint({"MissingPermission", "ResourceAsColor"})
            @Override
            public void onStyleLoaded(@NonNull Style style) {

                locationComponentActivationOptions = LocationComponentActivationOptions
                        .builder(HomeActivity.this, style)
                        .build();
                locationComponent = mapboxMap.getLocationComponent();
                locationComponent.activateLocationComponent(locationComponentActivationOptions);
                locationComponent.setLocationComponentEnabled(true);


                double userLongitude = locationComponent.getLastKnownLocation().getLongitude();
                double userLatitude = locationComponent.getLastKnownLocation().getLatitude();

                user_camera(userLatitude, userLongitude);

                setup_markers(userLongitude, userLatitude);


                locationComponent.addOnLocationClickListener(new OnLocationClickListener() {
                    @Override
                    public void onLocationComponentClick() {
                        user_camera(userLatitude, userLongitude);
                    }
                });


                mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        //Toast.makeText(HomeActivity.this, marker.getTitle(), Toast.LENGTH_LONG).show();
                        String school_name = marker.getTitle();
                        for (School school1 : schoolArrayList) {
                            if (school1.getName().equals(school_name)) {
                                school = school1;
                            }
                        }

                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(marker.getPosition())
                                .zoom(15)
                                .tilt(20)
                                .build();
                        showDetailsDialog();
                        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000);
                        return false;
                    }
                });

                setup_search_cursor();

                List<Marker> markerList = mapboxMap.getMarkers();
                for (int m = 0; m < markerList.size(); m++) {
                    String title = markerList.get(m).getTitle();
                    String snippet = markerList.get(m).getSnippet();
                    Log.d("ARAPA APP", "Title: " + title + " Snippet: " + snippet);
                }
            }
        });
    }

    private void setup_markers(double userLongitude, double userLatitude) {
        for (int i = 0; i < schoolArrayList.size(); i++) {

            String name = schoolArrayList.get(i).getName();
            double longitude = schoolArrayList.get(i).getLongitude();
            double latitude = schoolArrayList.get(i).getLatitude();
            String type = schoolArrayList.get(i).getType();

            //"Primary", "High School", "Senior High School", "College"
            calculate_distance(schoolArrayList.get(i), userLongitude, userLatitude);
            switch (type) {
                case "Primary":
                    add_marker(name, type, R.drawable.primary_marker, latitude, longitude);
                    break;
                case "High School":
                    add_marker(name, type, R.drawable.highschool_marker, latitude, longitude);
                    break;
                case "Senior High School":
                    add_marker(name, type, R.drawable.senior_high_marker, latitude, longitude);
                    break;
                case "College":
                    add_marker(name, type, R.drawable.college_marker, latitude, longitude);
                    break;
            }

        }
    }

    private void calculate_distance(School school1, double userLongitude, double userLatitude) {
        List<Point> pointList = new ArrayList<>();
        pointList.add(Point.fromLngLat(school1.getLongitude(), school1.getLatitude()));
        pointList.add(Point.fromLngLat(userLongitude, userLatitude));

        int pointListSize = pointList.size();

        double distance = TurfMeasurement.distance(
                pointList.get(pointListSize - 2), pointList.get(pointListSize - 1));

        school1.setDistance(distance);
    }

    private void add_marker(String name, String type, int drawable, double latitude, double longitude) {
        IconFactory iconFactory = IconFactory.getInstance(this);
        Icon icon = iconFactory.fromResource(drawable);
        mapboxMap.addMarker(new MarkerOptions()
                .setTitle(name)
                .setSnippet(type)
                .setPosition(new LatLng(latitude, longitude))
                .setIcon(icon));
    }

    private void user_camera(double userLatitude, double userLongitude) {

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(userLatitude, userLongitude))
                .zoom(15)
                .tilt(20)
                .build();
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000);

    }

    @SuppressLint("NewApi")
    private boolean permissionGranted() {
        boolean permissionGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (!permissionGranted) {
            new AlertDialog.Builder(this)
                    .setMessage("Access to Location Services is needed for this feature. Please allow to access location and try again. Thank you")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
                        }
                    })
                    .setNegativeButton("No", null)
                    .setCancelable(false)
                    .create().show();
        }
        return permissionGranted;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
        Toast.makeText(this, "Memory run-out error", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        searchtask = (SearchTask) new SearchTask(HomeActivity.this, schoolArrayList, cursor_adapter)
                .execute(newText);
//        if (newText.equals("")) {
//            details_holder.setVisibility(View.GONE);
//        }
        return false;
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        // Get the cursor adapter
        if (searchtask != null) {
            searchtask.cancel(true);
        } else {
            Log.i("ARAPA", "SEARCH TASK IS NULL");
        }
        CursorAdapter cursorAdapter = searchView.getSuggestionsAdapter();
        Cursor cursor = cursorAdapter.getCursor();

        cursor.moveToPosition(position);

        @SuppressLint("Range")
        String selected_suggestion = cursor.getString(cursor.getColumnIndex("suggest_school_id"));

        for (int i = 0; i < schoolArrayList.size(); i++) {
            if (selected_suggestion.equals(schoolArrayList.get(i).getSchool_id())) {
                school = schoolArrayList.get(i);
                showDetailsDialog();
                move_camera(school);
                break;
            }
        }
        return true;
    }

    private boolean[] selectedTypes = new boolean[4];
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        int id = compoundButton.getId();
        mapboxMap.clear();
        switch (id) {
            case R.id.primary_checkbox:
                selectedTypes[0] = b;
                break;
            case R.id.highschool_checkbox:
                selectedTypes[1] = b;
                break;
            case R.id.seniorhigh_checkbox:
                selectedTypes[2] = b;
                break;
            case R.id.college_checkbox:
                selectedTypes[3] = b;
                break;
        }
        boolean noCheckboxChecked = true;
        for (int i = 0; i < selectedTypes.length; i++) {
            if (selectedTypes[i]) {
                noCheckboxChecked = false;
                break;
            }
        }
        // If no checkbox is checked, show all schools
        if (noCheckboxChecked) {
            double userLongitude = locationComponent.getLastKnownLocation().getLongitude();
            double userLatitude = locationComponent.getLastKnownLocation().getLatitude();
            setup_markers(userLongitude, userLatitude);
            return;
        }
        ArrayList<School> filteredList = filterByType(selectedTypes);
        for (int i = 0; i < filteredList.size(); i++) {

            String name = filteredList.get(i).getName();
            double longitude = filteredList.get(i).getLongitude();
            double latitude = filteredList.get(i).getLatitude();
            String type = filteredList.get(i).getType();
            Log.d("ARAPA", "FILTERED: " + type);

            //"Primary", "High School", "Senior High School", "College"
            switch (type) {
                case "Primary":
                    add_marker(name, type, R.drawable.primary_marker, latitude, longitude);
                    break;
                case "High School":
                    add_marker(name, type, R.drawable.highschool_marker, latitude, longitude);
                    break;
                case "Senior High School":
                    add_marker(name, type, R.drawable.senior_high_marker, latitude, longitude);
                    break;
                case "College":
                    add_marker(name, type, R.drawable.college_marker, latitude, longitude);
                    break;
            }

        }
    }

    private ArrayList<School> filterByType(boolean[] selectedTypes) {
        ArrayList<School> filteredList = new ArrayList<>();
        String[] types = {"Primary", "High School", "Senior High School", "College"};
        for (School school : schoolArrayList) {
            // Check if the school type matches any of the selected types
            for (int i = 0; i < selectedTypes.length; i++) {
                Log.d("ARAPA", "TYPE: " + selectedTypes[i]);
                if (selectedTypes[i] && school.getType().equals(types[i])) {
                    filteredList.add(school);
                }
            }
        }
        return filteredList;
    }



    private void add_filtered_marker(int id, boolean isChecked) {
        mapboxMap.clear();
        IconFactory iconFactory = IconFactory.getInstance(this);
        Icon icon = null;
    }

    private void remove_markers(String type) {
        List<Marker> markerList = mapboxMap.getMarkers();
        for (int i = 0; i < markerList.size(); i++) {
            Marker marker = markerList.get(i);
            if (marker.getSnippet().equals(type)) {
                mapboxMap.removeMarker(marker);
            }
        }
    }
}