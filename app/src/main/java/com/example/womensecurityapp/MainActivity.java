package com.example.womensecurityapp;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.womensecurityapp.model.location_model;
import com.example.womensecurityapp.model.person_details;
import com.example.womensecurityapp.model.person_info;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Button actionScreenBtn,new_entry,recent_activity;
    public static SharedPreferences preferences;
    public static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // shared preference for info
        // it contains the basic info about the user
        recent_activity=findViewById(R.id.main_recent_activity);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();


        actionScreenBtn = findViewById(R.id.main_actionScreenBtn);
        new_entry=findViewById(R.id.main_new_entry);
        new_entry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new_entry_track();
            }
        });
        actionScreenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this, action_screen.class));

            }
        });

        recent_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!preferences.getString("active", "no").equals("no"))
                {
                    Toast.makeText(getApplicationContext(),preferences.getString("active", "no"),Toast.LENGTH_LONG).show();
                    Intent i=new Intent(getApplicationContext(),MapActivity.class);
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"You don't have recent activity",Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    public void new_entry_track()
    {
        final Dialog dialog = new Dialog(this);
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

                DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Problem_Record_data").child(editText.getText().toString());

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (!(dataSnapshot.getValue()==null))
                        {
                            Toast.makeText(getApplicationContext(),"Starting the live Tracking",Toast.LENGTH_LONG).show();

                            new_user_info();
                            dialog.dismiss();
                        }
                        else
                        {

                            Toast.makeText(getApplicationContext(),"Wrong ID, Please Check it",Toast.LENGTH_LONG).show();
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
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.new_user_track_info_popup);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

      final   EditText name=dialog.findViewById(R.id.new_entry_name);
       final EditText contact=dialog.findViewById(R.id.new_entry_contact);

        Button cancel=dialog.findViewById(R.id.new_entry_cancel2);
        Button ok=dialog.findViewById(R.id.new_entry_ok2);

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

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                person_details details=new person_details(new location_model("0","0"),new person_info(name.getText().toString(),contact.getText().toString()),"1");

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

                Intent i=new Intent(getApplicationContext(),MapActivity.class);
                startActivity(i);



                dialog.dismiss();

            }
        });

        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setAttributes(lp);

    }
}