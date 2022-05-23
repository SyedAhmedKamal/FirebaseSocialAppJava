package com.example.firebasesocialapp_java.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.firebasesocialapp_java.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.messaging.FirebaseMessaging;

public class FCMActivity extends AppCompatActivity {

    TextView textView;
    private static final String TAG = "FCMActivity";
    private static final String key = "key1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fcmactivity);

        textView = findViewById(R.id.textView2);

        if (getIntent()!=null && getIntent().hasExtra(key)){
            textView.setText("");
            for (String key:getIntent().getExtras().keySet()){
                Log.d(TAG, "onCreate: Key "+key+" Data "+getIntent().getExtras().getString(key));
                textView.append(getIntent().getExtras().getString(key)+"\n");
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

    }
}