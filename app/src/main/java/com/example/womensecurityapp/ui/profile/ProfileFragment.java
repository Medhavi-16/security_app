package com.example.womensecurityapp.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.example.womensecurityapp.Main2Activity;
import com.example.womensecurityapp.R;
import com.example.womensecurityapp.User_login_info.Trusted_person;
import com.example.womensecurityapp.model.Trusted_person_model;
import com.example.womensecurityapp.model.User_residential_details;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private ProfileModel profileModel;
    TextView name,email,no,city,address,country;

    Button edit;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileModel =
                new ViewModelProvider(this).get(ProfileModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
         name= root.findViewById(R.id.set_name);
        email= root.findViewById(R.id.set_email);
        no= root.findViewById(R.id.set_no);
        city= root.findViewById(R.id.set_city);
        address= root.findViewById(R.id.set_address);
        country=root.findViewById(R.id.set_country);
       /* final DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.child("Personal_info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                t=dataSnapshot.getValue(User_residential_details.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
       name.setText(Main2Activity.t.getName());
       email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
       no.setText(Main2Activity.t.getContact_no());
       city.setText(Main2Activity.t.getCity());
       address.setText(Main2Activity.t.getHouse_no()+","+Main2Activity.t.getStreet());
       city.setText(Main2Activity.t.getCity());
       country.setText(Main2Activity.t.getCountry());
        /*final DatabaseReference databaseReference1=FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference1.child("Trusted_person").child("Info").child("1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Trusted_person_model t1 = dataSnapshot.getValue(Trusted_person_model.class);
                    Toast.makeText(getContext(),FirebaseAuth.getInstance().getCurrentUser().getUid()+ " "+t1.getName(), Toast.LENGTH_SHORT).show();
                    name.setText(t1.getName());
                }
                else
                    Toast.makeText(getContext(),"no"+FirebaseAuth.getInstance().getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
*/


       //
      /*  profileModel.getmName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
               name.setText(s);
            }
        });
        profileModel.getmEmail().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                email.setText(s);
            }
        });
        profileModel.getmNo().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                no.setText(s);
            }
        });
        profileModel.getmAddress().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                address.setText(s);
            }
        });
        profileModel.getmCity().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
               city.setText(s);
            }
        });
        profileModel.getmCountry().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                country.setText(s);
            }
        });*/



        return root;
    }
}