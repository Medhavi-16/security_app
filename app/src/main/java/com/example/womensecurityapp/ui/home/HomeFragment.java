package com.example.womensecurityapp.ui.home;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.womensecurityapp.Main2Activity;
import com.example.womensecurityapp.MapActivity;
import com.example.womensecurityapp.R;
import com.example.womensecurityapp.User_login_info.Account_setup;
import com.example.womensecurityapp.action_screen;
import com.example.womensecurityapp.model.Trusted_person_model;
import com.example.womensecurityapp.model.User_residential_details;
import com.example.womensecurityapp.model.location_model;
import com.example.womensecurityapp.model.person_details;
import com.example.womensecurityapp.model.person_info;
import com.example.womensecurityapp.services.SMS;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.womensecurityapp.MainActivity.editor;
import static com.example.womensecurityapp.MainActivity.preferences;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private static final int SEND_SMS_PERMISSION_REQUEST = 0;

    private DatabaseReference databaseReference;
    /*public static SharedPreferences preferences;
    public static SharedPreferences.Editor editor;*/

    private HomeViewModel homeViewModel;
    private Button shareLocation,help,share,start,recent;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView name=root.findViewById(R.id.home_name);
        final TextView contact=root.findViewById(R.id.home_contact);



        try {


            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Personal_info");

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())

                    {
                        User_residential_details u = dataSnapshot.getValue(User_residential_details.class);
                        name.setText(u.getName());
                        contact.setText(u.getContact_no());
                        editor.putString("current_user_name",u.getName());
                        editor.putString("current_user_contact",u.getContact_no());
                        editor.commit();
                    }
                    else
                    {
                        Intent i=new Intent(getActivity(), Account_setup.class);
                        startActivity(i);
                    }



                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();

        }


        shareLocation=root.findViewById(R.id.type1);
        help=root.findViewById(R.id.help);

        start=root.findViewById(R.id.home_enter);
        recent=root.findViewById(R.id.home_recent_activity);

        recent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!preferences.getString("active", "no").equals("no"))
                {
                    Toast.makeText(getActivity(),preferences.getString("active", "no"),Toast.LENGTH_LONG).show();
                    Intent i=new Intent(getActivity(),MapActivity.class);
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(getActivity(),"You don't have recent activity",Toast.LENGTH_LONG).show();
                }
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preferences.getString("active", "no").equals("no")) {
                    new_entry_track();
                }
                else
                {
                    recent_check();
                }
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

                if(preferences.getString("active", "no").equals("no")) {
                    shareLocationToTrusted();
                    callActionScreen();
                }
                else
                {
                    recent_check();
                }
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
    public void new_entry_track() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.new_user_track_request_popup);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final EditText editText=dialog.findViewById(R.id.new_entry_text);
        Button cancel=dialog.findViewById(R.id.new_entry_cancel);
        Button ok=dialog.findViewById(R.id.new_entry_ok);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editText.onEditorAction(EditorInfo.IME_ACTION_DONE);

                DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Problem_Record_data")
                        .child(editText.getText().toString());

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (!(dataSnapshot.getValue()==null))
                        {
                            Toast.makeText(getActivity(),"Starting the live Tracking",Toast.LENGTH_LONG).show();

                            new_user_info();
                            dialog.dismiss();
                        }
                        else
                        {

                            Toast.makeText(getActivity(),"Wrong ID, Please Check it",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setAttributes(lp);
    }

    public void new_user_info()
    {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.new_user_track_info_popup);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final   EditText name=dialog.findViewById(R.id.new_entry_name);
        final EditText contact=dialog.findViewById(R.id.new_entry_contact);

        final int[] a = new int[1];

        final DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("Problem_Record").child("1").child("person").child("counter");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                a[0] =dataSnapshot.getValue().hashCode();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

                person_details details=new person_details(new location_model("0","0"),new person_info(preferences.getString("current_user_name","NA"),preferences.getString("current_user_contact","NA")),"1");

                DatabaseReference databaseReference1=FirebaseDatabase.getInstance().getReference().child("Problem_Record")
                        .child("1").child("person").child("person_info").child("person_no_"+ String.valueOf(a[0]));

                databaseReference1.setValue(details);


                editor.putString("new_user_problem_id","1");
                editor.putString("new_user_name",name.getText().toString());
                editor.putString("new_user_contact",contact.getText().toString());
                editor.putString("new_user_counter", String.valueOf(a[0]));
                editor.putString("active","yes");
                editor.commit();

                a[0]++;

                databaseReference.setValue(a[0]);

                Intent i=new Intent(getActivity(), MapActivity.class);
                startActivity(i);

    }

    public void recent_check()
    {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.recent_action_check);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        Button cancel=dialog.findViewById(R.id.recent_action_cancel);
        Button ok=dialog.findViewById(R.id.recent_action_ok);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(),MapActivity.class);
                startActivity(i);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("active","no");
                editor.commit();
                Toast.makeText(getActivity(),"successfully exit",Toast.LENGTH_LONG).show();
            }
        });

        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setAttributes(lp);
    }
}






