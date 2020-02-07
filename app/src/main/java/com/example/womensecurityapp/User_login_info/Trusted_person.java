package com.example.womensecurityapp.User_login_info;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.womensecurityapp.R;
import com.example.womensecurityapp.model.Trusted_person_model;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Trusted_person extends AppCompatActivity {
    TextInputLayout name,contact,mail,address,relation;
    Button add, more,proceed;
    DatabaseReference d;
    int c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trusted_person);
        name=findViewById(R.id.trusted_name);
        contact=findViewById(R.id.trusted_contact);
        mail=findViewById(R.id.trusted_email);
        address=findViewById(R.id.trusted_address);
        relation=findViewById(R.id.trusted_relation);
        add=findViewById(R.id.trusted_add);

        d=  FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Trusted_person");

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                d.child("count").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        c=Integer.parseInt(dataSnapshot.getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                if(name.getEditText().getText().toString().isEmpty() || contact.getEditText().getText().toString().isEmpty())
                    Toast.makeText(getApplicationContext(),"Name and contact no. is mandatory",Toast.LENGTH_SHORT).show();
                else
                {
                    Trusted_person_model trusted_person_model=new Trusted_person_model(name.getEditText().getText().toString(),contact.getEditText().getText().toString(),mail.getEditText().getText().toString(),address.getEditText().getText().toString(),relation.getEditText().getText().toString());
                    d.child("Info").child(Integer.toString(++c)).setValue(trusted_person_model);
                    d.child("count").setValue(Integer.toString(c));
                    showDialog();

                }

            }
        });







    }
    private void showDialog()
    {
        final Dialog dialog = new Dialog(this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_add_more);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT ;
        dialog.setCancelable(true);
        more=dialog.findViewById(R.id.more);
        proceed=dialog.findViewById(R.id.proceed);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Trusted_person.this,Trusted_person.class);
                startActivity(i);
            }
        });
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        dialog.show();


    }
}
