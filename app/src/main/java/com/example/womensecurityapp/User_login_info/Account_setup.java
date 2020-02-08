package com.example.womensecurityapp.User_login_info;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.example.womensecurityapp.R;
import com.example.womensecurityapp.model.User_residential_details;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
                String mail=FirebaseAuth.getInstance().getCurrentUser().getEmail();
                User_residential_details profile=new User_residential_details(name.getEditText().getText().toString(),mail,contact.getEditText().getText().toString(),"India",city.getEditText().getText().toString(),street.getEditText().getText().toString(),house.getEditText().getText().toString());

                Toast.makeText(getApplicationContext(),"ok",Toast.LENGTH_LONG).show();


               /* profile.setName(name.getEditText().getText().toString());
                profile.setContact_no(contact.getEditText().getText().toString());
                profile.setHouse_no(house.getEditText().getText().toString());
                profile.setStreet(street.getEditText().getText().toString());
                profile.setCity(city.getEditText().getText().toString());
                profile.setCountry("India");
*/

                final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                databaseReference.child("Personal_info").setValue(profile);
                FirebaseDatabase.getInstance().getReference().child("City-Records").child(city.getEditText().getText().toString().toUpperCase()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(contact.getEditText().getText().toString());
               databaseReference.child("Trusted_person").child("count").addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       if(!dataSnapshot.exists())
                           databaseReference.child("Trusted_person").child("count").setValue("0");
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });


                Intent i=new Intent(Account_setup.this,Trusted_person.class);
                startActivity(i);
            }
        });

    }

}
