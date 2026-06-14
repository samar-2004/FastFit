package com.example.fastfit.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.fastfit.R;
import com.example.fastfit.SplashScreen;
import com.example.fastfit.data.Repo;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Collections;

/** Receives FCM pushes and posts them as system notifications. */
public class FastFitMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        String title = "FASTFit";
        String body = "";
        if (message.getNotification() != null) {
            title = message.getNotification().getTitle() != null
                    ? message.getNotification().getTitle() : title;
            body = message.getNotification().getBody() != null
                    ? message.getNotification().getBody() : body;
        } else if (!message.getData().isEmpty()) {
            title = message.getData().getOrDefault("title", title);
            body = message.getData().getOrDefault("body", body);
        }
        showNotification(title, body);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        // Persist the device token so the backend can target this user.
        String uid = Repo.get().uid();
        if (uid != null) {
            FirebaseFirestore.getInstance().collection("users").document(uid)
                    .set(Collections.singletonMap("fcmToken", token), SetOptions.merge());
        }
    }

    private void showNotification(String title, String body) {
        Intent intent = new Intent(this, SplashScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        PendingIntent pending = PendingIntent.getActivity(this, 0, intent, flags);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this, getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.drawable.ic_dumbell)
                .setColor(getColor(R.color.red))
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setAutoCancel(true)
                .setContentIntent(pending);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
