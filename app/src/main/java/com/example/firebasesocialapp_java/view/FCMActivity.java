package com.example.firebasesocialapp_java.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebasesocialapp_java.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class FCMActivity extends AppCompatActivity {

    TextView textView;
    Button subBtn, unSubBtn;
    private static final String TAG = "FCMActivity";
    private static final String key = "key1";
    private static final String TOPIC = "Weather-topic";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fcmactivity);

        textView = findViewById(R.id.textView2);
        subBtn = findViewById(R.id.subBtn);
        unSubBtn = findViewById(R.id.unSubBtn);

        if (getIntent() != null && getIntent().hasExtra(key)) {
            textView.setText("");
            for (String key : getIntent().getExtras().keySet()) {
                Log.d(TAG, "onCreate: Key " + key + " Data " + getIntent().getExtras().getString(key));
                textView.append(getIntent().getExtras().getString(key) + "\n");
            }

        }

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            String token = task.getResult();
                            Log.d(TAG, "onComplete: TOKEN - " + token);
                            //textView.setText(token);
                        } else {
                            Log.e(TAG, "onComplete: " + task.getException());
                        }
                    }
                });
        subBtn.setOnClickListener(view -> {
            FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Subscribe to " + TOPIC, Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onCreate topic: " + task.getResult());
                        } else {
                            Log.e(TAG, "onCreate exception:" + task.getException());
                        }
                    });
        });

        unSubBtn.setOnClickListener(view -> {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC)
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Unsubscribe to " + TOPIC, Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onCreate topic: " + task.getResult());
                        } else {
                            Log.e(TAG, "onCreate exception:" + task.getException());
                        }

                    });
        });
    }
}