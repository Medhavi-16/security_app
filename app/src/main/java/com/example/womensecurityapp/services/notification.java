package com.example.womensecurityapp.services;

import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.womensecurityapp.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static com.example.womensecurityapp.MainActivity.editor;
import static com.example.womensecurityapp.MainActivity.preferences;

public class notification extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.e("noti",preferences.getString("current_user_city","GWA"));

        String a=remoteMessage.getData().get("message");
        int i=a.indexOf('(');
        int y=a.indexOf(')');
        Log.e("message",a);
        Log.e("a", String.valueOf(i));
        Log.e("b", String.valueOf(y));
        String b=a.substring(i+1,y-1);
        Log.e("lk",b);


        editor.putString("notification_info",b);
        editor.commit();


        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,preferences.getString("current_user_city","GWALIOR"))
                .setContentTitle(remoteMessage.getData().get("title"))
                .setSmallIcon(R.drawable.ic_security)
                .setContentText(remoteMessage.getData().get("message"))
                .setAutoCancel(true);

        NotificationManagerCompat managerCompat=NotificationManagerCompat.from(this);
        managerCompat.notify(999,builder.build());

    }
}
