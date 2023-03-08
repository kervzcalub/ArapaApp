package com.arapa.app;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.arapa.app.ui.PagerAdapter;
import com.arapa.app.util.School;
import com.arapa.app.util.Utils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.directions.v5.models.RouteOptions;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.navigation.base.internal.route.RouteUrl;
import com.mapbox.navigation.core.MapboxNavigation;
import com.mapbox.navigation.core.directions.session.RoutesRequestCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SchoolActivity extends AppCompatActivity implements Serializable, OnMapReadyCallback {

    private School school;
    private TextView school_name;
    private TextView school_address;
    private ImageView school_logo;


    private MapView mapView;
    private MapboxMap mapboxMap;
    private MaterialButton navigateBtn;

    private LocationComponent locationComponent;

    private LocationComponentActivationOptions locationComponentActivationOptions;
    private String route;

    private ProgressDialog progressDialog;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_school);
        school = (School) getIntent().getSerializableExtra("SCHOOL");
        school_logo = findViewById(R.id.logo);
        school_name = findViewById(R.id.name);
        school_address = findViewById(R.id.address);

        mapView = findViewById(R.id.mapView);

        navigateBtn = findViewById(R.id.navigateBtn);

        school_logo.setImageBitmap(Utils.getSchoolLogo(this, school));
        school_name.setText(school.getName());
        adjustTextSize();
        school_address.setText(school.getAddress());


        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        navigateBtn.setOnClickListener(view -> {
            if (permissionGranted()) {
                locationComponent = mapboxMap.getLocationComponent();
                locationComponent.activateLocationComponent(locationComponentActivationOptions);
                locationComponent.setLocationComponentEnabled(true);
                progressDialog = new ProgressDialog(this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Getting routes. Please wait");
                progressDialog.create();
                progressDialog.show();
                double userLongitude = locationComponent.getLastKnownLocation().getLongitude();
                double userLatitude = locationComponent.getLastKnownLocation().getLatitude();
                getRoutes(userLongitude, userLatitude);
            }
        });


        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager2 viewPager = findViewById(R.id.view_pager);

        PagerAdapter pagerAdapter = new PagerAdapter(this, school);

        viewPager.setAdapter(pagerAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                Log.i("ARAPA", "TAB POS: " + tab.getPosition() + " " + tab.getText());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });
    }


    private void adjustTextSize() {
        // Set the initial text size of the TextView
        school_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);

        // Get the bounds of the TextView
        Rect bounds = new Rect();
        school_name.getPaint().getTextBounds(school_name.getText().toString(), 0, school_name.getText().length(), bounds);

        // If the text is too long to fit within the bounds of the TextView, decrease the text size until it fits
        while (bounds.width() > school_name.getWidth() || bounds.height() > school_name.getHeight()) {
            // Decrease the text size by 1 sp
            float newTextSize = school_name.getTextSize() - 1;

            // If the new text size is below the minimum text size limit, stop adjusting the text size
            if (newTextSize < 12) {
                break;
            }

            // If the new text size is above the maximum text size limit, stop adjusting the text size
            if (newTextSize > 22) {
                break;
            }

            // Set the new text size
            school_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, newTextSize);

            // Get the new bounds of the text
            school_name.getPaint().getTextBounds(school_name.getText().toString(), 0, school_name.getText().length(), bounds);
        }

    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                addDestinationIconSymbolLayer(style);
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(school.getLatitude(), school.getLongitude()))
                        .zoom(15)
                        .tilt(20)
                        .build();

                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000);

                pointOfSChool(school.getLatitude(), school.getLongitude());

                locationComponentActivationOptions = LocationComponentActivationOptions
                        .builder(SchoolActivity.this, style)
                        .build();

            }
        });
    }


    private void getRoutes(double userLongitude, double userLatitude) {
        Point userPoint = Point.fromLngLat(userLongitude, userLatitude);
        Point schoolPoint = Point.fromLngLat(school.getLongitude(), school.getLatitude());
        List<Point> pointList = new ArrayList<>();
        pointList.add(userPoint);
        pointList.add(schoolPoint);
        for (Point p : pointList) {
            Log.d("ARAPA APP", p.toString());
        }

        MapboxNavigation navigation = new MapboxNavigation(MapboxNavigation.defaultNavigationOptionsBuilder(this,
                getString(R.string.mapbox_access_token)).build());

        navigation.requestRoutes(RouteOptions.builder()
                .coordinates(pointList)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .geometries(RouteUrl.GEOMETRY_POLYLINE6)
                .requestUuid(UUID.randomUUID().toString())
                .user("mapbox")
                .baseUrl("https://api.mapbox.com/")
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .accessToken(Mapbox.getAccessToken())
                .alternatives(true)
                .build(), new RoutesRequestCallback() {
            @Override
            public void onRoutesReady(@NonNull List<? extends DirectionsRoute> list) {
                if (!list.isEmpty()) {
                    Toast.makeText(SchoolActivity.this, "Routes Ready", Toast.LENGTH_LONG).show();
//                    GeoJsonSource source = mapboxMap.getStyle().getSourceAs("ROUTE_LINE_SOURCE_ID");
//                    LineString lineString = LineString.fromPolyline(list.get(0).geometry(), 6);
//                    source.setGeoJson(lineString);
                    route = list.get(0).toJson();
                    Log.d("ARAPA APP", list.get(0).toJson());
                    progressDialog.dismiss();
                    Intent intent = new Intent(SchoolActivity.this, NavigationActivity.class);
                    intent.putExtra("route", route);
                    startActivity(intent);
                } else {
                    Log.d("ARAPA APP", "No routes");
                }
            }

            @Override
            public void onRoutesRequestFailure(@NonNull Throwable throwable, @NonNull RouteOptions routeOptions) {
                Log.d("ARAPA APP", throwable.getMessage() + ":FAILURE");
            }

            @Override
            public void onRoutesRequestCanceled(@NonNull RouteOptions routeOptions) {
                Log.d("ARAPA APP", "Route Cancelled");
            }
        });
    }

    private void addDestinationIconSymbolLayer(@NonNull Style loadedMapStyle) {
        int marker = com.mapbox.mapboxsdk.R.drawable.mapbox_marker_icon_default;
        String type = school.getType();
        switch (type) {
            case "Primary":
                marker = R.drawable.primary_marker;
                break;
            case "High School":
                marker = R.drawable.highschool_marker;
                break;
            case "Senior High School":
                marker = R.drawable.senior_high_marker;
                break;
            case "College":
                marker = R.drawable.college_marker;
                break;
        }
        loadedMapStyle.addImage("destination-icon-id",
                BitmapFactory.decodeResource(this.getResources(), marker));
        GeoJsonSource geoJsonSource = new GeoJsonSource("destination-source-id");
        loadedMapStyle.addSource(geoJsonSource);
        SymbolLayer destinationSymbolLayer = new SymbolLayer("destination-symbol-layer-id", "destination-source-id");
        destinationSymbolLayer.withProperties(
                iconImage("destination-icon-id"),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        );
        loadedMapStyle.addLayer(destinationSymbolLayer);
    }

    private void pointOfSChool(double latitude, double longitude) {
        Point schoolPoint = Point.fromLngLat(longitude, latitude);

        GeoJsonSource source = mapboxMap.getStyle().getSourceAs("destination-source-id");
        if (source != null) {
            source.setGeoJson(Feature.fromGeometry(schoolPoint));
        }
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
}