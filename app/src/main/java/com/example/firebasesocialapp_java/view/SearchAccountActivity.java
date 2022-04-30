package com.example.firebasesocialapp_java.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.firebasesocialapp_java.databinding.ActivitySearchAccountBinding;
import com.example.firebasesocialapp_java.model.SearchAccountModel;
import com.example.firebasesocialapp_java.util.SearchedAccountAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchAccountActivity extends AppCompatActivity {

    private static final String TAG = "SearchAccountActivity";
    private ActivitySearchAccountBinding binding;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private static String uid;
    private static String name;
    private static String imageUrl;
    private SearchedAccountAdapter adapter;
    private ArrayList<SearchAccountModel> usersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseReference = FirebaseDatabase.getInstance().getReference();
        usersList = new ArrayList<>();

        databaseReference
                .child("Users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.i(TAG, "onDataChange: User - " + snapshot.getKey());
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            Log.i(TAG, "onDataChange: User - " + userSnapshot.getKey());// getting uid
                            Log.i(TAG, "onDataChange: User - " + userSnapshot.getValue());
                            uid = userSnapshot.getKey();

                            if (uid != null) {

                                databaseReference
                                        .child("Users")
                                        .child(uid)
                                        .child("UserProfile")
                                        .child("name")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                name = String.valueOf(snapshot.getValue());
                                                Log.i(TAG, "onDataChange: NAME - " + name);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }
                                        });

                                databaseReference
                                        .child("Users")
                                        .child(uid)
                                        .child("UserProfile")
                                        .child("ProfileImage")
                                        .child("imageUrl")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                imageUrl = String.valueOf(snapshot.getValue());
                                                Log.i(TAG, "onDataChange: " + imageUrl);

                                                SearchAccountModel user = new SearchAccountModel(uid, imageUrl, name);
                                                usersList.add(user);
                                                adapter = new SearchedAccountAdapter(usersList, SearchAccountActivity.this);
                                                binding.accRecyclerView.setAdapter(adapter);

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }
                                        });

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "onCancelled: " + error.getMessage());
                    }
                });

    }
}