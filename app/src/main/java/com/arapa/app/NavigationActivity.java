package com.arapa.app;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.navigation.base.trip.model.RouteLegProgress;
import com.mapbox.navigation.base.trip.model.RouteProgress;
import com.mapbox.navigation.base.trip.model.RouteProgressState;
import com.mapbox.navigation.core.MapboxNavigation;
import com.mapbox.navigation.core.arrival.ArrivalObserver;
import com.mapbox.navigation.core.fasterroute.FasterRouteObserver;
import com.mapbox.navigation.core.replay.route.ReplayProgressObserver;
import com.mapbox.navigation.ui.NavigationView;
import com.mapbox.navigation.ui.NavigationViewOptions;
import com.mapbox.navigation.ui.OnNavigationReadyCallback;
import com.mapbox.navigation.ui.listeners.NavigationListener;
import com.mapbox.navigation.ui.map.NavigationMapboxMap;

import java.io.Serializable;
import java.util.List;

public class NavigationActivity extends AppCompatActivity implements Serializable, OnNavigationReadyCallback, NavigationListener {

    private NavigationMapboxMap navigationMapboxMap;
    private MapboxNavigation mapboxNavigation;
    private MapboxMap mapboxMap;
    private NavigationView navigationView;
    private String route;
    private DirectionsRoute directionsRoute;

    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        route = getIntent().getExtras().getString("route");
        directionsRoute = DirectionsRoute.fromJson(route);
        setContentView(R.layout.activity_navigation);
        navigationView = findViewById(R.id.navigationView);
        navigationView.onCreate(savedInstanceState);
        navigationView.initialize(this);
    }


    @Override
    public void onNavigationReady(boolean isRunning) {
        if (!isRunning) {
            if (navigationView.retrieveNavigationMapboxMap() != null) {
                this.navigationMapboxMap = navigationView.retrieveNavigationMapboxMap();
                this.mapboxNavigation = navigationView.retrieveMapboxNavigation();
                this.mapboxMap = navigationView.retrieveNavigationMapboxMap().retrieveMap();

                navigationView.startNavigation(NavigationViewOptions.builder(this)
                        .navigationListener(this)
                        .arrivalObserver(arrivalObserver)
                        .directionsRoute(directionsRoute)
                        .build());

            }
        }
    }

    private ArrivalObserver arrivalObserver = new ArrivalObserver() {
        @Override
        public void onNextRouteLegStart(@NonNull RouteLegProgress routeLegProgress) {

        }

        @Override
        public void onFinalDestinationArrival(@NonNull RouteProgress routeProgress) {
            if (routeProgress.getCurrentState() == RouteProgressState.ROUTE_COMPLETE) {
                Toast.makeText(NavigationActivity.this, "You arrived at your destination", Toast.LENGTH_LONG).show();
            }
            mapboxNavigation.detachFasterRouteObserver();
        }
    };

    private FasterRouteObserver fasterRouteObserver = new FasterRouteObserver() {
        @Override
        public long restartAfterMillis() {
            return FasterRouteObserver.Companion.getDEFAULT_INTERVAL_MILLIS();
        }

        @Override
        public void onFasterRoute(@NonNull DirectionsRoute directionsRoute, @NonNull List<? extends DirectionsRoute> list, boolean b) {
            mapboxNavigation.setRoutes(list);
        }
    };


    @Override
    public void onCancelNavigation() {

    }

    @Override
    public void onNavigationFinished() {
        finish();
    }

    @Override
    public void onNavigationRunning() {
        navigationView.updateCameraRouteOverview();
        if (fasterRouteObserver != null && mapboxNavigation != null) {
            mapboxNavigation.attachFasterRouteObserver(fasterRouteObserver);
        } else {
            Log.d("NAVIGATION", "NULL");
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        navigationView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        navigationView.onStop();
        if (fasterRouteObserver != null && mapboxNavigation != null) {
            mapboxNavigation.detachFasterRouteObserver();
        } else {
            Log.d("NAVIGATION", "NULL");
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        navigationView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        navigationView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.onResume();
    }

    @Override
    public void onBackPressed() {
        if (!navigationView.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        navigationView.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        navigationView.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        navigationView.onLowMemory();
    }

}