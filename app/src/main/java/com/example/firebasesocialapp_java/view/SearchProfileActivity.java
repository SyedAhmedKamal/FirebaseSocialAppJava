package com.example.firebasesocialapp_java.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.firebasesocialapp_java.R;
import com.example.firebasesocialapp_java.model.ProfileImage;
import com.example.firebasesocialapp_java.model.SearchAccountModel;
import com.example.firebasesocialapp_java.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SearchProfileActivity extends AppCompatActivity {

    private static final String TAG = "SearchProfileActivity";
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    protected String uid;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_profile);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        imageView = findViewById(R.id.p_image);

        uid = getIntent().getStringExtra("uid");
        Log.d(TAG, "onCreate: " + uid);

        loadUserProfile();


    }

    private void loadUserProfile() {

        databaseReference
                .child("Users")
                .child(uid)
                .child("UserProfile")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User user = snapshot.getValue(User.class);
                            Log.d(TAG, "onDataChange: USERNAME "+user.getName());

                            loadProfileImage();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "onCancelled: "+error.getMessage());
                    }
                });
    }

    private void loadProfileImage() {

        databaseReference
                .child("Users")
                .child(uid)
                .child("UserProfile")
                .child("ProfileImage")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            ProfileImage profileImage = snapshot.getValue(ProfileImage.class);
                            Glide.with(getApplicationContext()).load(profileImage.getImageUrl()).into(imageView);
                            Log.d(TAG, "onDataChange: PROFILE IMAGE "+profileImage.getImageUrl());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "onCancelled: "+error.getMessage());
                    }
                });
    }
}