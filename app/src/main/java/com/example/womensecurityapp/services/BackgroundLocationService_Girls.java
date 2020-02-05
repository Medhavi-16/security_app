package com.example.womensecurityapp.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.womensecurityapp.model.location_model;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BackgroundLocationService_Girls extends Service {

    private static final String TAG = "BackgroundLocationService_Girls";

    private FusedLocationProviderClient mFusedLocationClient;
    private final static long UPDATE_INTERVAL = 4000;
    private final static long FASTEST_INTERVAL = 2000;

    DatabaseReference databaseReference_location;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (Build.VERSION.SDK_INT >= 26) {

            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "My Channel", NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Girl Location Activity")
                    .setContentText("").build();

            startForeground(1, notification);
        }
    }

    @SuppressLint("LongLogTag")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: called.");
        getLocation();
        return START_NOT_STICKY;
    }

    @SuppressLint("LongLogTag")
    private void getLocation() {

        LocationRequest mLocationRequestHighAccuracy = new LocationRequest();
        mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequestHighAccuracy.setInterval(UPDATE_INTERVAL);
        mLocationRequestHighAccuracy.setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getLocationG: stopping the location service.");
            stopSelf();
            return;
        }

        Log.d(TAG, "getLocationG: getting location information.");
        mFusedLocationClient.requestLocationUpdates(mLocationRequestHighAccuracy, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {

                        Log.d(TAG, "onLocationResultG: got location result.");

                        Location currentLocation = locationResult.getLastLocation();

                        if (currentLocation != null) {

                            Log.d(TAG, "getLocationG: " + currentLocation.getLatitude() + "/" + currentLocation.getLongitude());

                            // updating location in firebase
                            location_model location=new location_model();
                            location.setLongitude(String.valueOf(currentLocation.getLongitude()));
                            location.setLatitude(String.valueOf(currentLocation.getLatitude()));

                            databaseReference_location=FirebaseDatabase.getInstance().getReference()
                                    .child("Problem_Record")
                                    .child("1")
                                    .child("Location");

                            databaseReference_location.setValue(location);

                        }
                    }
                },
                Looper.myLooper()); // Looper.myLooper tells this to repeat forever until thread is destroyed
    }

}










