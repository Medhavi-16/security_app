package com.example.womensecurityapp.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.womensecurityapp.MainActivity;
import com.example.womensecurityapp.MapActivity;
import com.example.womensecurityapp.R;

import static com.example.womensecurityapp.services.App.CHANNEL_ID;

public class foreground_service extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String input = intent.getStringExtra("inputExtra");

        Intent notificationIntent=new Intent(getApplicationContext(), MapActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),0,notificationIntent,0);

        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle("Women Security Running in Background")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_security)
                .setContentIntent(pendingIntent)
                .build();

        startService(new Intent(getApplicationContext(), shake_service.class));
        startForeground(1, notification);

        //do heavy work on a background thread
        //stopSelf();

        return START_NOT_STICKY;
    }

    @Nullable

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
