package com.example.womensecurityapp.services;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.womensecurityapp.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class notification extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,"Gwalior")
                .setContentTitle(remoteMessage.getData().get("title"))
                .setSmallIcon(R.drawable.ic_security)
                .setContentText(remoteMessage.getData().get("message"))
                .setAutoCancel(true);

        NotificationManagerCompat managerCompat=NotificationManagerCompat.from(this);
        managerCompat.notify(999,builder.build());

    }
}
