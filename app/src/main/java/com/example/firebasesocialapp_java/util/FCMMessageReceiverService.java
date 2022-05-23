package com.example.firebasesocialapp_java.util;

import static com.example.firebasesocialapp_java.util.App.FCM_CHANNEL_ID;

import android.app.Notification;
import android.app.NotificationManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.firebasesocialapp_java.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class FCMMessageReceiverService extends FirebaseMessagingService {

    private static final String TAG = "service";
    private Bitmap image;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        Log.d(TAG, "onMessageReceived: called");
        Log.d(TAG, "onMessageReceived: Message received from: " + message.getFrom());

        if (message.getNotification() != null) {
            String title = message.getNotification().getTitle();
            String body = message.getNotification().getBody();

            try {
                URL url = new URL(message.getNotification().getImageUrl().toString());
                image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch(IOException e) {
                System.out.println(e);
            }

            Notification notification = new NotificationCompat.Builder(this, FCM_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_baseline_message_24)
                    .setColor(Color.BLUE)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image))
                    .setLargeIcon(image)
                    .build();

            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(1002, notification);
        }

        if (message.getData().size() > 0){
            Log.d(TAG, "onMessageReceived: DATA SIZE - "+message.getData().size());

            for (String key:message.getData().keySet()){
                Log.d(TAG, "onMessageReceived: Key "+key+" Data "+message.getData().get(key));
            }

            Log.d(TAG, "onMessageReceived: DATA - "+message.getData());
        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
        Log.d(TAG, "onDeletedMessages: called");
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "onNewToken: called");
    }
}
