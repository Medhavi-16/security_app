package com.example.womensecurityapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.womensecurityapp.model.location_model;
import com.example.womensecurityapp.model.person_details;
import com.example.womensecurityapp.services.AppController;
import com.example.womensecurityapp.services.SMS;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class action_screen extends AppCompatActivity implements LocationListener, OnMapReadyCallback {

    private static final String TAG = "MainActivity";
    public static final String STATUS = "status";
    public static final String OK = "OK";
    public static final String GEOMETRY = "geometry";
    public static final String LOCATION = "location";
    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "lng";
    public static final String NAME = "name";
    public static final String VICINITY = "vicinity";
    private static final int FINE_LOCATION_PERMISSION_REQUEST_CODE = 0;
    private static final int SEND_SMS_PERMISSION_REQUEST = 0;
    private static final float DEFAULT_ZOOM = 15f;

    private String safe_location_FLAG;

    //widgets
    private DrawerLayout drawer;
    private Button alertButton;
    private Button mapButton;
    private TextView locationText;
    private ImageView gps_icon;
    private Button policeStationButton;
    private Button railwayStationButton;
    private Button airportButton;
    private Button mallButton;

    //Variables
    private Boolean location_permission_granted = false;
    LocationManager locationManager;
    DatabaseReference databaseReference_location;
    DatabaseReference databaseReference_person,databaseReference_person_info;
    private GoogleMap mMap;
    private Location myLocation;

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

        policeStationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = "police";
                safe_location_FLAG = type;
                getNearByPlaces(type);
            }
        });

        railwayStationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = "train_station";
                safe_location_FLAG = type;
                getNearByPlaces(type);
            }
        });

        airportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = "airport";
                safe_location_FLAG = type;
                getNearByPlaces(type);
            }
        });

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = "shopping_mall";
                safe_location_FLAG = type;
                getNearByPlaces(type);
            }
        });

    }

    private void init(){

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        locationText = findViewById(R.id.locationText);


        databaseReference_location=FirebaseDatabase.getInstance().getReference().child("Problem_Record").child("1").child("Location");
        databaseReference_person=FirebaseDatabase.getInstance().getReference().child("Problem_Record").child("1").child("person").child("person_info");

        //Drawer
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open_nav_drawer,
                R.string.close_nav_drawer);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // init widgets
        alertButton = findViewById(R.id.alertButton);
        mapButton = findViewById(R.id.mapButton);
        locationText = findViewById(R.id.locationText);
        gps_icon = findViewById(R.id.action_screen_gps_icon);
        policeStationButton = findViewById(R.id.policeStationBtn);
        railwayStationButton = findViewById(R.id.railwayStationBtn);
        airportButton = findViewById(R.id.AirportBtn);
        mapButton = findViewById(R.id.MallBtn);

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



        // to add different person location into the map
        addAllPerson();

        mMap = googleMap;

        if (location_permission_granted)
        {
            getLocation();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);

        }

    }

    private void addAllPerson(){

        databaseReference_person.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (final DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    final MarkerOptions[] markerOptions = {null};
                    final Marker[] marker = new Marker[1];

                    databaseReference_person_info=databaseReference_person.child(postSnapshot.getKey()).child("location");

                    databaseReference_person_info.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot pdataSnapshot) {

                            location_model location = new location_model();
                            location=pdataSnapshot.getValue(location_model.class);


                            if(markerOptions[0] ==null)
                            {

                                markerOptions[0] = new MarkerOptions();

                                LatLng latLng = new LatLng(Double.valueOf(location.getLatitude()), Double.valueOf(location.getLongitude()));

                                markerOptions[0].position(latLng);

                                markerOptions[0].title(latLng.latitude + " : " + latLng.longitude);

                                marker[0] =mMap.addMarker(markerOptions[0]
                                        .icon(bitmapDescriptorFromVector(getApplicationContext(),
                                                R.drawable.ic_person_pin_circle)));

                            }
                            else
                            {
                                LatLng latLng = new LatLng(Double.valueOf(location.getLatitude()), Double.valueOf(location.getLongitude()));
                                marker[0].setPosition(latLng);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

        myLocation = location;

        locationText.setText("Latitude: " + location.getLatitude() + "\nLongitude: " + location.getLongitude());
  //      messaging();

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

  /*  private void messaging(){

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
*/
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
    //    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

        // Placing a marker on the touched position
        mMap.addMarker(markerOptions);
    }

    private void getNearByPlaces(String type){

        double latitude = myLocation.getLatitude();
        double longitude = myLocation.getLongitude();

        StringBuilder googlePlacesUrl =
                new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=").append(latitude).append(",").append(longitude);
        googlePlacesUrl.append("&rankby=").append("distance");
        googlePlacesUrl.append("&types=").append(safe_location_FLAG);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=AIzaSyC1uMbDWYK6aaFPdiT9Fp1KsHwMPNJ96d4");

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, googlePlacesUrl.toString(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: Result= " + response.toString());
                        parseLocationResult(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: Error= " + error);
                        Log.e(TAG, "onErrorResponse: Error= " + error.getMessage());
                    }
                });


                AppController.getInstance().addToRequestQueue(request);


    }

    private void parseLocationResult(JSONObject result) {

        String id, place_id, placeName = null, reference, icon, vicinity = null;
        double latitude, longitude;

        try {
            JSONArray jsonArray = result.getJSONArray("results");

            if (result.getString(STATUS).equalsIgnoreCase(OK)) {

                mMap.clear();
                addAllPerson();

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject place = jsonArray.getJSONObject(i);

                    if (!place.isNull(NAME)) {
                        placeName = place.getString(NAME);
                    }
                    if (!place.isNull(VICINITY)) {
                        vicinity = place.getString(VICINITY);
                    }
                    latitude = place.getJSONObject(GEOMETRY).getJSONObject(LOCATION)

                            .getDouble(LATITUDE);
                    longitude = place.getJSONObject(GEOMETRY).getJSONObject(LOCATION)

                            .getDouble(LONGITUDE);

                    MarkerOptions markerOptions = new MarkerOptions();
                    LatLng latLng = new LatLng(latitude, longitude);
                    markerOptions.position(latLng);
                    markerOptions.title(placeName + " : " + vicinity);

                    if (safe_location_FLAG == "police"){
                        mMap.addMarker(markerOptions.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_security)));
                    }
                    else if (safe_location_FLAG == "train_station"){
                        mMap.addMarker(markerOptions.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_train)));
                    }
                    else if (safe_location_FLAG == "airport"){
                        mMap.addMarker(markerOptions.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_airport)));
                    }
                    else if (safe_location_FLAG == "shopping_mall"){
                        mMap.addMarker(markerOptions.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_store_mall)));
                    }

                }

                Toast.makeText(getBaseContext(), jsonArray.length() + " found!", Toast.LENGTH_SHORT).show();
            }
            else if (result.getString(STATUS).equalsIgnoreCase("ZERO_RESULTS")) {
                Toast.makeText(getBaseContext(), "Nothing found!", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {

            e.printStackTrace();
            Log.e(TAG, "parseLocationResult: Error=" + e.getMessage());
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId){

        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}








