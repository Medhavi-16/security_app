package com.example.womensecurityapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Button actionScreenBtn,new_entry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                finish();
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
                            Intent i=new Intent(getApplicationContext(),action_screen.class);
                            startActivity(i);
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

}