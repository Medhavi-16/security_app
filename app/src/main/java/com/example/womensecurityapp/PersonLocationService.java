package com.example.womensecurityapp;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.example.womensecurityapp.model.location_model;
import com.google.android.gms.location.LocationResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.womensecurityapp.MainActivity.preferences;


public class PersonLocationService extends IntentService {

    public static final String ACTION_PROCESS_UPDATES= "com.example.womensecurityapp.UPDATE_LOCATION";
    public static final String TAG = "PersonLocationService";

    DatabaseReference databaseReference_person_location;

    public PersonLocationService() {
        super("PersonLocationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(TAG, "onHandleIntent: called");

         if (intent != null){
             if (ACTION_PROCESS_UPDATES.equals(intent.getAction())){

                 LocationResult locationResult = LocationResult.extractResult(intent);

                 if (locationResult != null){

                     try {
                         Log.d(TAG, "onHandleIntent: Successful");

                         Location location = locationResult.getLastLocation();
                         Log.d(TAG, "onHandleIntent: Location: " + location.getLatitude() + "/" + location.getLongitude());

                         databaseReference_person_location= FirebaseDatabase.getInstance().getReference().child("Problem_Record")
                                 .child("1").child("person").child("person_info")
                                 .child("person_no_"+preferences.getString("new_user_counter","1")).child("location");

                         location_model mlocationModel=new location_model();
                         mlocationModel.setLongitude(String.valueOf(location.getLongitude()));
                         mlocationModel.setLatitude(String.valueOf(location.getLatitude()));

                         databaseReference_person_location.setValue(mlocationModel);
                     }
                     catch (Exception e){
                         Log.d(TAG, "onHandleIntent: error " + e.getMessage());
                     }

                 }
             }
         }
    }


}
