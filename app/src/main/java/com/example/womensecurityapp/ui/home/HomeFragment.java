package com.example.womensecurityapp.ui.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.womensecurityapp.R;
import com.example.womensecurityapp.action_screen;
import com.example.womensecurityapp.model.Trusted_person_model;
import com.example.womensecurityapp.services.SMS;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private static final int SEND_SMS_PERMISSION_REQUEST = 0;

    private DatabaseReference databaseReference;
    /*public static SharedPreferences preferences;
    public static SharedPreferences.Editor editor;*/

    private HomeViewModel homeViewModel;
    private Button shareLocation,help,share;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.name);
        shareLocation=root.findViewById(R.id.type1);
        help=root.findViewById(R.id.help);
        share=root.findViewById(R.id.share_location);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);

            }
        });

//        init();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child("RO9P5BwxjQazx9XUeZ4dsufdTny1")
                .child("Trusted_person")
                .child("Info");

        shareLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareLocationToTrusted();
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareLocationToTrusted();
                callActionScreen();
            }
        });

        return root;
    }

    /*private void init(){

        // shared preference for info
        // it contains the basic info about the user
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = preferences.edit();

    }*/

    private void callActionScreen(){
        Intent intent = new Intent(getActivity(), action_screen.class);
        startActivity(intent);
    }

    private void shareLocationToTrusted(){

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    Trusted_person_model trusted_person_model = ds.getValue(Trusted_person_model.class);
                    String destPhone = trusted_person_model.getContact();
                    sendSMS(destPhone);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendSMS(String destPhone){

        if (checkSMSpermission() && checkPhoneStatePermission()){

            Log.d(TAG, "sendSMS: message sent");
            String message = "Hello";
            SMS smsObject = new SMS();
            smsObject.sendSMS(destPhone, message);

        }
        else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE}, SEND_SMS_PERMISSION_REQUEST);
        }
    }

    public boolean checkSMSpermission(){

        int check = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS);
        return (check == PackageManager.PERMISSION_GRANTED);

    }

    public boolean checkPhoneStatePermission(){

        int check = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE);
        return (check == PackageManager.PERMISSION_GRANTED);

    }

}






