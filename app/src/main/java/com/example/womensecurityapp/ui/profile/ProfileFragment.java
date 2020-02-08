package com.example.womensecurityapp.ui.profile;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.example.womensecurityapp.Login;
import com.example.womensecurityapp.Main2Activity;
import com.example.womensecurityapp.R;
import com.example.womensecurityapp.User_login_info.Account_setup;
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
        edit=root.findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(),Account_setup.class);
                i.putExtra("edit",1);
                startActivity(i);
            }
        });


           name.setText(Main2Activity.t.getName());

           email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
           no.setText(Main2Activity.t.getContact_no());
           city.setText(Main2Activity.t.getCity());
           address.setText(Main2Activity.t.getHouse_no() + "," + Main2Activity.t.getStreet());
           city.setText(Main2Activity.t.getCity());
           country.setText(Main2Activity.t.getCountry());





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