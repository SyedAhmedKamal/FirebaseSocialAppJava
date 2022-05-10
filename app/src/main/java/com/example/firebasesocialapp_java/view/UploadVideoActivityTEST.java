package com.example.firebasesocialapp_java.view;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.firebasesocialapp_java.R;
import com.example.firebasesocialapp_java.databinding.ActivityUploadVideoTestBinding;

public class UploadVideoActivityTEST extends AppCompatActivity {

    private ActivityUploadVideoTestBinding binding;
    private ActivityResultLauncher<String> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadVideoTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        uploadVideo();

        binding.uploadVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityResultLauncher.launch("video/*");
            }
        });

    }

    private void uploadVideo() {

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {

                if (uri != null){
                    Toast.makeText(UploadVideoActivityTEST.this, uri.toString(), Toast.LENGTH_LONG).show();
                    startUpload();
                }
                else{
                    Toast.makeText(UploadVideoActivityTEST.this, "No video select", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void startUpload() {

    }
}