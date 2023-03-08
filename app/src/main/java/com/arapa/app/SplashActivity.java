package com.arapa.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.arapa.app.util.Database;
import com.arapa.app.util.RequestListener;
import com.arapa.app.util.Utils;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {

    private final String TAG = "com.arapa.app.TAG";

    private Database database = new Database();
    private boolean permissionGranted = false;
    private boolean dataRetrieved = false;

//    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        //getSupportActionBar().hide();
//        FirebaseApp.initializeApp(/*context=*/ this);
//        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
//        firebaseAppCheck.installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance());
//
//        firebaseAuth = FirebaseAuth.getInstance();
//
//        firebaseAuth.signInAnonymously()
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // User is signed in anonymously
//                            FirebaseUser user = firebaseAuth.getCurrentUser();
//                            // Proceed with accessing Firebase Storage
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInAnonymously:failure", task.getException());
//                        }
//                    }
//                });
        requestPermission();
        if (isConnectedToInternet()) {
            connect_to_server();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Couldn't connect to server")
                    .setMessage("Please check your internet connection and try again.")
                    .setPositiveButton("Retry", null)
                    .show();
            Log.d(TAG, "INTERNET CONNECTING");
        }

    }

    private void connect_to_server() {
        database.retrieveSchools(new RequestListener.OnSuccessListener() {
            @SuppressLint("NewApi")
            @Override
            public void onSuccess(Task<QuerySnapshot> task) {
                try {
                    if (task.isSuccessful()) {
                       JSONArray schoolArray = new JSONArray();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            JSONObject schoolObj = new JSONObject(document.getData());
                            schoolObj.put("school_id", document.getId());
                            schoolArray.put(schoolObj);
                        }

                        // check if logo exists
                        int logo_count = 0;
                        boolean logo_ready = false;
                        for (int j = 0; j < schoolArray.length(); j++) {
                            JSONObject object = schoolArray.getJSONObject(j);
                            String url = object.getString("Logo");
                            String school_id = object.getString("school_id");
                            if (Utils.logo_exist(SplashActivity.this, school_id, url) == true) {
                                logo_count += 1;
                                Log.d("LOGO", "LOGO FOR " + school_id + " EXISTS");
                            } else {
                                Utils.downloadLogo(SplashActivity.this, url, school_id);
                            }

                            Log.d("LOGO", "COUNTER: " + logo_count + ", LENGTH: " + schoolArray.length());

                            if (j == schoolArray.length() - 1) {
                                // this is the last item in the list
                                logo_ready = true;
                            }
                        }

                        if (logo_count == schoolArray.length()) {
                            logo_ready = true;
                        }


                        if (schoolArray.length() != 0 && logo_ready == true) {
                            Utils.writeToFile(SplashActivity.this, schoolArray.toString(2));
                            dataRetrieved = true;
                            lockAndLoad();
                        } else {
                            connect_to_server();
                        }
                    }
                } catch (Exception e) {
                    Log.d("ARAPA APP", e.getMessage());
                    e.printStackTrace();
                }
            }
        }, new RequestListener.OnErrorListener() {
            @Override
            public void onError(Exception e) {
                Log.e(TAG, e.getMessage());
            }
        });
    }

    private void lockAndLoad() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                finish();
            }
        }, 1500);
    }

    public boolean isConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        if (cm == null)
            return false;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @SuppressLint("NewApi")
    private void requestPermission() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        } else {
            permissionGranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 101: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionGranted = true;
                    if (dataRetrieved) {
                        lockAndLoad();
                    }
                }
                else {
                    new AlertDialog.Builder(this)
                            .setTitle("Location Services")
                            .setMessage("Location Permission is needed for this app to be fully functional as this app has a map & navigate feature. Disabling the permission request may result to errors. Please allow this app to use location services. If you don't want to use the navigation feature, you are free to click the continue button below. Thank you")
                            .setPositiveButton("Request Again", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    requestPermission();
                                }
                            })
                            .setNegativeButton("Continue", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (dataRetrieved) {
                                        lockAndLoad();
                                    }
                                }
                            })
                            .create()
                            .show();
                }
                return;
            }

        }
    }

}
