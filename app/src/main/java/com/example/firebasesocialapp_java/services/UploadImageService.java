package com.example.firebasesocialapp_java.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.firebasesocialapp_java.R;
import com.example.firebasesocialapp_java.view.UserPostActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadImageService extends Service {

    private static final String TAG = "UploadImageService";
    private static final String CHANNEL_ID = "NOT_ID";
    private static final int NOTIFICATION_ID = 1;

    private StorageReference storageReference;
    private FirebaseAuth auth;
    NotificationCompat.Builder myNotBuilder;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage
                .getInstance()
                .getReference(auth.getCurrentUser().getUid())
                .child("Posts");


        if (intent != null && intent.getExtras() != null) {
            String imgUri = intent.getStringExtra("uri");
            doWork(Uri.parse(imgUri));
        } else {
            Log.d(TAG, "onStartCommand: null");
        }

        return START_NOT_STICKY;
    }

    private void doWork(Uri uri) {

        /*Intent notificationIntent = new Intent(this, UserPostActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent,
                        PendingIntent.FLAG_IMMUTABLE);*/

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        myNotBuilder = new NotificationCompat.Builder(UploadImageService.this, CHANNEL_ID)
                .setContentTitle("Hello")
                .setOnlyAlertOnce(true)
                .setSmallIcon(R.drawable.ic_baseline_message_24);

        startForeground(NOTIFICATION_ID, myNotBuilder.build());


        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String name = mime.getExtensionFromMimeType(contentResolver.getType(uri));

        StorageReference testImage = FirebaseStorage
                .getInstance()
                .getReference(auth.getCurrentUser().getUid())
                .child("Posts")
                .child(System.currentTimeMillis() + "." + name);

        testImage.putFile(uri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {
                            Log.d(TAG, "Success: " + task.getResult().getStorage().getDownloadUrl());
                            stopSelf();
                            Log.d(TAG, "onComplete: service shutdown");
                        } else {
                            Log.d(TAG, "Exception: " + task.getException());
                        }
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        long progress = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        Log.d(TAG, "onProgress: " + progress);

                        myNotBuilder
                                .setSound(null)
                                .setDefaults(Notification.DEFAULT_SOUND)
                                .setProgress(100, (int) progress, false);

                        notificationManager.notify(NOTIFICATION_ID, myNotBuilder.build());

                    }
                });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Test", importance);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called");
    }
}
