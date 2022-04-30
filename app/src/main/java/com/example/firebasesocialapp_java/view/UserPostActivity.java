package com.example.firebasesocialapp_java.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.firebasesocialapp_java.databinding.ActivityUserPostBinding;
import com.example.firebasesocialapp_java.model.Post;
import com.example.firebasesocialapp_java.util.UserPostAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class UserPostActivity extends AppCompatActivity {

    private static final String TAG = "UserPostActivity";
    private ActivityUserPostBinding binding;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private ArrayList<Post> postArrayList;
    private UserPostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        postArrayList = new ArrayList<>();

        databaseReference
                .child("Users")
                .child(auth.getUid())
                .child("Posts")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d(TAG, "onDataChange: "+snapshot.getChildren());
                        for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                            Log.d(TAG, "Posts: "+postSnapshot.getValue().toString());
                            Post post = postSnapshot.getValue(Post.class);
                            postArrayList.add(post);
                            adapter = new UserPostAdapter(getApplicationContext(), postArrayList);
                            binding.recyclerView.setAdapter(adapter);
                            Log.d(TAG, "onCreate: "+postArrayList.size());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



    }
}