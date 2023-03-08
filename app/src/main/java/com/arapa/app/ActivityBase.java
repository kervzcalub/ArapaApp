package com.arapa.app;

import static com.arapa.app.util.Utils.jsonFile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.arapa.app.util.School;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.directions.v5.models.RouteOptions;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.navigation.base.internal.route.RouteUrl;
import com.mapbox.navigation.core.MapboxNavigation;
import com.mapbox.navigation.core.directions.session.RoutesRequestCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ActivityBase extends AppCompatActivity {

    public ProgressDialog progressDialog;
    public String route;

    public School school;
    public ArrayList<School> schoolArrayList;

    public void init_school_data() {
        schoolArrayList = new ArrayList<>();
        if (!jsonFile(this).isEmpty()) {
            try {
                JSONArray schoolArray = new JSONArray(jsonFile(ActivityBase.this));

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

                        if (schoolData.has("Facebook")) {
                            school.setFacebook(schoolData.getString("Facebook"));
                        }
                        if (schoolData.has("Instagram")) {
                            school.setInstagram(schoolData.getString("Instagram"));
                        }
                        if (schoolData.has("ContactNo")) {
                            school.setOther_contact(schoolData.getString("ContactNo"));
                        }
                        if (schoolData.has("OtherSites")) {
                            school.setOther_site(schoolData.getString("OtherSites"));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void getRoutes(Context context, School school, double userLongitude, double userLatitude) {
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
                    Toast.makeText(context, "Routes Ready", Toast.LENGTH_LONG).show();
                    route = list.get(0).toJson();
                    Log.d("ARAPA APP", list.get(0).toJson());
                    progressDialog.dismiss();
                    Intent intent = new Intent(context, NavigationActivity.class);
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


    /** DO NOT DELETE
     * show_details function
     * for reference only
     *
     *
     *
     * private void showDetails() {
     *         school_details.setVisibility(View.VISIBLE);
     *         school_logo.setImageBitmap(Utils.getSchoolLogo(this, school));
     *         school_name.setText(school.getName());
     *         adjustTextSize(school_name);
     *         school_address.setText(school.getAddress());
     *         school_contact.setText(school.getContact());
     *         school_description.setText(school.getDescription());
     *         school_website.setText(school.getWeb());
     *
     *         courses = school.getCourses_list();
     *         ArrayAdapter<String> adapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, courses);
     *         courses_listView.setAdapter(adapter);
     *
     *         SchoolImages schoolImages = new SchoolImages();
     *
     *         imagesView = findViewById(R.id.school_image_view);
     *         imageAdapter = new ImageAdapter(this, schoolImages);
     *
     *         // Create a new GridLayoutManager with 3 columns
     *         GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
     *         // Set the GridLayoutManager as the layout manager for your RecyclerView
     *         imagesView.setLayoutManager(gridLayoutManager);
     *
     *         imagesView.setAdapter(imageAdapter);
     *
     *         try {
     *             Utils.downloadImages(HomeActivity.this, school, schoolImages, imageAdapter);
     *         } catch (Exception e) {
     *             e.printStackTrace();
     *         }
     *     }
     *
     *
     */


}
