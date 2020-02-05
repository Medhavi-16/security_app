package com.example.womensecurityapp.User_login_info;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.example.womensecurityapp.R;
import com.example.womensecurityapp.model.User_residential_details;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Account_setup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setup);

        final TextInputLayout name,contact,house,street,city;

        name= findViewById(R.id.account_name);
        contact=findViewById(R.id.account_phone_no);
        house=findViewById(R.id.account_house_no);
        street=findViewById(R.id.account_street_name);
        city=findViewById(R.id.account_city_name);
        Button submit=findViewById(R.id.account_submit);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User_residential_details profile=new User_residential_details();

                Toast.makeText(getApplicationContext(),"ok",Toast.LENGTH_LONG).show();

                profile.setName(name.getEditText().getText().toString());
                profile.setContact_no(contact.getEditText().getText().toString());
                profile.setHouse_no(house.getEditText().getText().toString());
                profile.setStreet(street.getEditText().getText().toString());
                profile.setCity(city.getEditText().getText().toString());
                profile.setCountry("India");


                DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Person_info").child("1").child("User_info").child("personal_info");
                databaseReference.setValue(profile);
            }
        });

    }
}
