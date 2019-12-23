package com.example.womensecurityapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final int FINE_LOCATION_PERMISSION_REQUEST_CODE = 0;
    private static final int SEND_SMS_PERMISSION_REQUEST = 0;
    private static final String TAG = "MainActivity";

    //widgets
    private DrawerLayout drawer;
    private Button alertButton;
    private TextView locationText;

    //Variables
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Drawer
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open_nav_drawer,
                R.string.close_nav_drawer);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        alertButton = findViewById(R.id.alertButton);
        locationText = findViewById(R.id.locationText);

        alertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestLocationPermission();
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
                Toast.makeText(getApplicationContext(), "Application will not run without location permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void requestLocationPermission(){

        Log.d(TAG, "requestLocationPermission: requesting location permission");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED){
                getLocation();
            }
            else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                    Toast.makeText(getApplicationContext(), "Application required to access location", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else {
            getLocation();
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

        try{
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            locationText.setText(locationText.getText()+ "\n" + addresses.get(0).getAddressLine(0) + "\n"
                    + addresses.get(0).getAddressLine(1) + "\n" + addresses.get(0).getAddressLine(2));

            sendMail();
            SMS();
        }
        catch (Exception e){

            e.printStackTrace();
            Log.d(TAG, "OnLocationChanged: " + e.getMessage());

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

    private void sendMail(){
        Log.d(TAG, "sendMail: trying to send Mail");
        String mail = "Womensecapp@gmail.com.";
        String subject = "Women Security";
        String message = "Hey! I'm in trouble \n" + "Please Help!" + locationText.getText().toString();

        try {
            JavaMailAPI javaMailAPI  = new JavaMailAPI(this, mail, subject, message);
            javaMailAPI.execute();
        }
        catch (Exception e){
            Log.d(TAG, "sendMail: Failed to send mail" + e.getMessage());
        }
    }

    private void SMS(){

        if (checkSMSpermission() && checkPhoneStatePermission()){
            sendSMS();
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

    private void sendSMS(){
        Log.d(TAG, "sendSMS: trying to send SMS");

        try {
            String destPhone = "6265105303";
            String message = locationText.getText().toString();

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(destPhone, null, message, null, null);
        }
        catch (Exception e){
            Log.d(TAG, "sendSMS: failed to send SMS" + e.getMessage());
        }
    }
}














