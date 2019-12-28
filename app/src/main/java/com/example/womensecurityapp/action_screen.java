package com.example.womensecurityapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.womensecurityapp.model.location_model;
import com.example.womensecurityapp.services.SMS;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class action_screen extends AppCompatActivity implements LocationListener, OnMapReadyCallback {

    private static final String TAG = "MainActivity";
    private static final int FINE_LOCATION_PERMISSION_REQUEST_CODE = 0;
    private static final int SEND_SMS_PERMISSION_REQUEST = 0;
    private static final float DEFAULT_ZOOM = 15f;

    //widgets
    private DrawerLayout drawer;
    private Button alertButton;
    private Button mapButton;
    private TextView locationText;
    private ImageView gps_icon;

    //Variables
    private Boolean location_permission_granted = false;
    LocationManager locationManager;
    DatabaseReference databaseReference_location;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_screen);

        init();
        requestLocationPermission();

        alertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
            }
        });

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(action_screen.this, MapActivity.class));
            }
        });

        gps_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
            }
        });

    }

    private void init(){

       /* Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        locationText = findViewById(R.id.locationText);


        databaseReference_location=FirebaseDatabase.getInstance().getReference().child("Problem_Record").child("1").child("Location");

        //Drawer
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, null, R.string.open_nav_drawer,
                R.string.close_nav_drawer);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // init widgets
        alertButton = findViewById(R.id.alertButton);
        mapButton = findViewById(R.id.mapButton);
        locationText = findViewById(R.id.locationText);
        gps_icon = findViewById(R.id.action_screen_gps_icon);

    }

    private void initMap(){

        Log.d(TAG, "initMap: initialising map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_actionScreen);

        mapFragment.getMapAsync(action_screen.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.d(TAG, "onMapReady: map is ready");
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();

        mMap = googleMap;

        if (location_permission_granted)
        {
            getLocation();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }

    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == FINE_LOCATION_PERMISSION_REQUEST_CODE)
        {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED)
            {
                location_permission_granted = false;
                Toast.makeText(getApplicationContext(), "Application will not run without location permission",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            else {
                location_permission_granted = true;
                initMap();
            }
        }
    }

    private void requestLocationPermission(){

        Log.d(TAG, "requestLocationPermission: requesting location permission");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED){

                location_permission_granted = true;
                initMap();
//                getLocation();
            }
            else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                    Toast.makeText(getApplicationContext(), "Application required to location permission", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else {
//            getLocation()
            initMap();
        }

    }

    void getLocation(){

        Log.d(TAG, "getLocation: fetching location");

        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
        }
        catch (SecurityException e){
            e.printStackTrace();
            Log.e(TAG, "getLocation: " + e.getMessage());
        }

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onLocationChanged(Location location) {

        locationText.setText("Latitude: " + location.getLatitude() + "\nLongitude: " + location.getLongitude());
        messaging();

        moveCamera(new LatLng(location.getLatitude(), location.getLongitude()), DEFAULT_ZOOM, "My Location");

        location_model location_data=new location_model();

        location_data.setLatitude(String.valueOf(location.getLatitude()));
        location_data.setLongitude(String.valueOf(location.getLongitude()));

        databaseReference_location.setValue(location_data);

        try{
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            locationText.setText(locationText.getText()+ "\n" + addresses.get(0).getAddressLine(0) + "\n"
                    + addresses.get(0).getAddressLine(1) + "\n" + addresses.get(0).getAddressLine(2));



        }
        catch (Exception e){

            e.printStackTrace();
            Log.d(TAG, "OnLocationChanged: error" + e.getMessage());

        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {
        Toast.makeText(this, "Please enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    private void messaging(){

        if (checkSMSpermission() && checkPhoneStatePermission()){

            String destPhone = "9024923695";
            String message = locationText.getText().toString();

            SMS smsObject = new SMS();
            smsObject.sendSMS(destPhone, message);

        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE}, SEND_SMS_PERMISSION_REQUEST);
        }
    }

    public boolean checkSMSpermission(){

        int check = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        return (check == PackageManager.PERMISSION_GRANTED);

    }

    public boolean checkPhoneStatePermission(){

        int check = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        return (check == PackageManager.PERMISSION_GRANTED);

    }

    private void moveCamera(LatLng latLng, float zoom, String title){

        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!title.equals("My Location"))
        {
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(title);
            mMap.addMarker(markerOptions);
        }
    }

}
