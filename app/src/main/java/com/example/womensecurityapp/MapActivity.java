package com.example.womensecurityapp;

import android.Manifest;
import android.content.Intent;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.womensecurityapp.model.location_model;
import com.example.womensecurityapp.services.shake_service;
import com.example.womensecurityapp.services.BackgroundLocationService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.GeoApiContext;

import static com.example.womensecurityapp.MainActivity.preferences;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapActivity";
    private static final String fine_location = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String coarse_location = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int location_permission_request_code = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    Location currentLocation;
    DatabaseReference databaseReference_person_location;

    // widgets
    private ImageView gps_icon;

    // variables
    private Boolean location_permission_granted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    GeoApiContext mGeoApiContext=null;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        getLocationPermission();

        databaseReference= FirebaseDatabase.getInstance().getReference().child("Problem_Record").child("1").child("Location");
        databaseReference_person_location=FirebaseDatabase.getInstance().getReference().child("Problem_Record").child("1").child("person")
                .child("person_info").child("person_no_"+preferences.getString("new_user_counter","1")).child("location");

    }

    private void init(){

        gps_icon = findViewById(R.id.map_gps_icon);

        gps_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocation();
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                location_model location = new location_model();
                location=dataSnapshot.getValue(location_model.class);

                set_maker(location);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

  /*  private void add_polyline(final DirectionsResult result)
    {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                for(DirectionsRoute route: result.routes)
                {
                    List<com.google.maps.model.LatLng> decodedpath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodepath =new ArrayList<>();

                    for (com.google.maps.model.LatLng latLng: decodedpath)
                    {

                        newDecodepath.add(new LatLng(
                                latLng.lat,latLng.lng
                        ));

                    }

                    Polyline polyline=mMap.addPolyline(new PolylineOptions().addAll(newDecodepath));
                    polyline.setColor(R.color.colorPrimary);
                    polyline.setClickable(true);

                }
            }
        });
    }
    private void calculatedirection()
    {
        com.google.maps.model.LatLng final_destination =new com.google.maps.model.LatLng(
                26.25803796913233,78.16172756056995
        );

        if(mGeoApiContext==null)
        {
            mGeoApiContext=new GeoApiContext.Builder()
                    .apiKey("AIzaSyDdBWdqC9Xmh2Qbm2I4w9kgPu1883oI-2A")
                    .build();
        }


        DirectionsApiRequest directions= new DirectionsApiRequest(mGeoApiContext);

        directions.alternatives(true);

        directions.origin(
                new com.google.maps.model.LatLng(
                        currentLocation.getLatitude(),currentLocation.getLongitude()
                )
        );

        Log.e("hbj", String.valueOf(currentLocation.getLatitude()));



        directions.destination(final_destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {

                add_polyline(result);
                Log.e("fgh", String.valueOf(result.routes[0].legs[0].distance));

            }

            @Override
            public void onFailure(Throwable e) {

                Log.e("fgh",e.getMessage());

            }
        });*/

    private void getLocationPermission(){

        Log.d(TAG, "getLocationPermission: getting location permission");
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), fine_location) == PackageManager.PERMISSION_GRANTED)
        {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), coarse_location) == PackageManager.PERMISSION_GRANTED)

            {
                location_permission_granted = true;
                initMap();
            }
            else
            {
                ActivityCompat.requestPermissions(this, permissions, location_permission_request_code);
            }
        }
        else
        {
            ActivityCompat.requestPermissions(this, permissions, location_permission_request_code);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        Log.d(TAG, "onRequestPermissionsResult: called");
        location_permission_granted = false;

        switch (requestCode)
        {
            case location_permission_request_code:{

                if (grantResults.length > 0)
                {
                    for (int i=0; i < grantResults.length; i++)
                    {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                        {
                            Log.d(TAG, "onRequestPermissionsResult: Permission failed");
                            location_permission_granted = false;
                            return;
                        }
                    }

                    Log.d(TAG, "onRequestPermissionsResult: Permission granted");
                    location_permission_granted = true;
                    //initialize map
                    initMap();

                }

            }
        }

    }

    private void initMap(){

        Log.d(TAG, "initMap: initialising map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapActivity.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.d(TAG, "onMapReady: map is ready");
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();

        mMap = googleMap;

        if (location_permission_granted)
        {
            getDeviceLocation();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            init();
        }
    }

    private void getDeviceLocation(){

        Log.d(TAG, "getDeviceLocation: getting the device current location");

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (location_permission_granted)
            {
                Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        if (task.isSuccessful())
                        {
                            Log.d(TAG, "onComplete: location found");
                            currentLocation = (Location) task.getResult();
                            assert currentLocation != null;
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM, "My Location");

                            location_model location=new location_model();
                            location.setLongitude(String.valueOf(currentLocation.getLongitude()));
                            location.setLatitude(String.valueOf(currentLocation.getLatitude()));

                            databaseReference_person_location.setValue(location);
                            startBackgroundLocationService();

                        }
                        else
                        {
                            Log.d(TAG, "onComplete: Current Location is not found");
                            Toast.makeText(MapActivity.this, "Unable to find device's current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
        catch (SecurityException e){
            Log.d(TAG, "getDeviceLocation: SecurityException " + e.getMessage());
        }
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

    public void set_maker(location_model maker_location) {

        LatLng latLng=new LatLng(Double.valueOf(maker_location.getLatitude()),Double.valueOf(maker_location.getLongitude()));

        // Creating a marker
        MarkerOptions markerOptions = new MarkerOptions();

        // Setting the position for the marker
        markerOptions.position(latLng);

        // Setting the title for the marker.
        // This will be displayed on taping the marker
        markerOptions.title(latLng.latitude + " : " + latLng.longitude);

        // Clears the previously touched position
        mMap.clear();

        // Animating to the touched position
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

        // Placing a marker on the touched position
        mMap.addMarker(markerOptions);
    }

    private void startBackgroundLocationService(){
        if(!isLocationServiceRunning()){
            Intent serviceIntent = new Intent(this, BackgroundLocationService.class);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){

                MapActivity.this.startForegroundService(serviceIntent);
            }else{
                startService(serviceIntent);
            }
        }
    }

    private boolean isLocationServiceRunning() {

        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        assert manager != null;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if("com.example.womensecurityapp.services.BackgroundLocationService".equals(service.service.getClassName())) {
                Log.d(TAG, "isLocationServiceRunning: location service is already running.");
                return true;
            }
        }
        Log.d(TAG, "isLocationServiceRunning: location service is not running.");
        return false;
    }

}