package com.example.womensecurityapp;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.womensecurityapp.Report.ProblemReport;
import com.example.womensecurityapp.User_login_info.Signup;
import com.example.womensecurityapp.model.location_model;
import com.example.womensecurityapp.model.person_details;
import com.example.womensecurityapp.model.person_info;
import com.example.womensecurityapp.services.SMS;
import com.example.womensecurityapp.services.foreground_service;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_PERMISSION_CODE = 1000;
    public static final String TAG = "MainActivity";
    public static final String tag_service = "MyServiceTag";

    private static final int SEND_SMS_PERMISSION_REQUEST = 0;


    private Button actionScreenBtn, new_entry, recent_activity;
    public static SharedPreferences preferences;
    public static SharedPreferences.Editor editor;

    private Button startRecordingBtn, stopRecordingBtn, playRecordingBtn, stopPlayingBtn, new_registration;
    private Button reportButton;
    private Button shareLocationButton;
    String pathSave = "";
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel("mynotification", "mynotification", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel2 = new NotificationChannel("Gwalior", "Gwalior", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel2);
        }


        FirebaseMessaging.getInstance().subscribeToTopic("hello")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "ok";
                        if (!task.isSuccessful()) {
                            msg = "not";
                        }
                    }
                });

        final Button start_service = findViewById(R.id.main_start_service);
        Button stop_service = findViewById(R.id.main_stop_service);


        new_registration = findViewById(R.id.new_user);

        new_registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), Signup.class);
                startActivity(i);
            }
        });

        start_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent serviceIntent = new Intent(MainActivity.this, foreground_service.class);
                serviceIntent.addCategory(tag_service);
                serviceIntent.putExtra("inputExtra", "shake your phone to start the security service");

                ContextCompat.startForegroundService(MainActivity.this, serviceIntent);

            }
        });
        stop_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent serviceIntent = new Intent(MainActivity.this, foreground_service.class);
                serviceIntent.addCategory(tag_service);
                stopService(serviceIntent);

            }
        });


        // shared preference for info
        // it contains the basic info about the user
        recent_activity = findViewById(R.id.main_recent_activity);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();


        actionScreenBtn = findViewById(R.id.main_actionScreenBtn);
        new_entry = findViewById(R.id.main_new_entry);
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

                if (!preferences.getString("active", "no").equals("no")) {
                    Toast.makeText(getApplicationContext(), preferences.getString("active", "no"), Toast.LENGTH_LONG).show();
                    Intent i = new Intent(getApplicationContext(), MapActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "You don't have recent activity", Toast.LENGTH_LONG).show();
                }

            }
        });

        startRecordingBtn = findViewById(R.id.main_startRecording);
        startRecordingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkPermissionFromDevice()) {
                    startRecording();
                } else {
                    requestAudioPermission();
                }
            }
        });

        stopRecordingBtn = findViewById(R.id.main_stopRecording);
        stopRecordingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRecording();
            }
        });

        playRecordingBtn = findViewById(R.id.main_play);
        playRecordingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playRecording();
            }
        });

        stopPlayingBtn = findViewById(R.id.main_stop);
        stopPlayingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPlaying();
            }
        });

        reportButton = findViewById(R.id.reportButton);
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProblemReport.class);
                startActivity(intent);

            }
        });

        shareLocationButton = findViewById(R.id.shareLocation);
        shareLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messaging();
            }
        });

        if (preferences.getString("girl-login", "no").equals("yes")) {
            Intent i = new Intent(getApplicationContext(), action_screen.class);
            startActivity(i);
            finish();
        } else {
            Intent intent = new Intent(MainActivity.this, Main2Activity.class);
            startActivity(intent);
            finish();
        }
    }
    public void new_entry_track() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.new_user_track_request_popup);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final EditText editText = dialog.findViewById(R.id.new_entry_text);
        Button cancel = dialog.findViewById(R.id.new_entry_cancel);
        Button ok = dialog.findViewById(R.id.new_entry_ok);

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

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Problem_Record_data")
                        .child(editText.getText().toString());

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (!(dataSnapshot.getValue() == null)) {
                            Toast.makeText(getApplicationContext(), "Starting the live Tracking", Toast.LENGTH_LONG).show();

                            new_user_info();
                            dialog.dismiss();
                        } else {

                            Toast.makeText(getApplicationContext(), "Wrong ID, Please Check it", Toast.LENGTH_LONG).show();
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

    public void new_user_info() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.new_user_track_info_popup);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final EditText name = dialog.findViewById(R.id.new_entry_name);
        final EditText contact = dialog.findViewById(R.id.new_entry_contact);

        Button cancel = dialog.findViewById(R.id.new_entry_cancel2);
        Button ok = dialog.findViewById(R.id.new_entry_ok2);

        final int[] a = new int[1];

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Problem_Record").child("1").child("person").child("counter");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                a[0] = dataSnapshot.getValue().hashCode();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                person_details details = new person_details(new location_model("0", "0"), new person_info(name.getText().toString(), contact.getText().toString()), "1");

                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Problem_Record")
                        .child("1").child("person").child("person_info").child("person_no_" + String.valueOf(a[0]));

                databaseReference1.setValue(details);


                editor.putString("new_user_problem_id", "1");
                editor.putString("new_user_name", name.getText().toString());
                editor.putString("new_user_contact", contact.getText().toString());
                editor.putString("new_user_counter", String.valueOf(a[0]));
                editor.putString("active", "yes");
                editor.commit();

                a[0]++;

                databaseReference.setValue(a[0]);

                Intent i = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(i);


                dialog.dismiss();

            }
        });

        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setAttributes(lp);

    }

    private void requestAudioPermission() {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSION_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case REQUEST_PERMISSION_CODE: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }

    }

    private boolean checkPermissionFromDevice() {

        int write_external_storage_result = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);

        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED;

    }

    public void startRecording() {

        pathSave = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                UUID.randomUUID().toString() + "_audio.3gp";

        setupMediaRecorder();
        try {

            mediaRecorder.prepare();
            mediaRecorder.start();

        } catch (Exception e) {
            Log.d(TAG, "onCreate: " + e.getMessage());
        }

        Toast.makeText(this, "Recording...", Toast.LENGTH_SHORT).show();
    }

    public void stopRecording() {
        mediaRecorder.stop();
    }

    public void playRecording() {

        mediaPlayer = new MediaPlayer();
        try {

            mediaPlayer.setDataSource(pathSave);
            mediaPlayer.prepare();
        } catch (Exception e) {
            Log.d(TAG, "onCreate: " + e.getMessage());
        }

        mediaPlayer.start();
        Toast.makeText(this, "Playing...", Toast.LENGTH_SHORT).show();
    }

    public void stopPlaying() {

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            setupMediaRecorder();
        }
    }


    private void setupMediaRecorder() {

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(pathSave);

    }

    private void messaging() {

        if (checkSMSpermission() && checkPhoneStatePermission()) {

            String destPhone = "6265105303";
            String message = "Hello";

            SMS smsObject = new SMS();
            smsObject.sendSMS(destPhone, message);

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE}, SEND_SMS_PERMISSION_REQUEST);
        }
    }

    public boolean checkSMSpermission() {

        int check = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        return (check == PackageManager.PERMISSION_GRANTED);

    }

    public boolean checkPhoneStatePermission() {

        int check = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        return (check == PackageManager.PERMISSION_GRANTED);

    }

}