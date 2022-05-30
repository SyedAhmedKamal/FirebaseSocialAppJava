package com.example.firebasesocialapp_java.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;
import com.example.firebasesocialapp_java.R;
import com.example.firebasesocialapp_java.databinding.ActivitySearchAccountBinding;
import com.example.firebasesocialapp_java.model.SearchAccountModel;
import com.example.firebasesocialapp_java.model.User;
import com.example.firebasesocialapp_java.util.SearchedAccountAdapter;
import com.example.firebasesocialapp_java.util.SearchedUserClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchAccountActivity extends AppCompatActivity implements SearchedUserClickListener {

    private static final String TAG = "SearchAccountActivity";
    private ActivitySearchAccountBinding binding;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private static String uid;
    private static String name;
    private static String imageUrl;
    private SearchedAccountAdapter adapter;
    private ArrayList<SearchAccountModel> usersList;
    protected SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        searchView = findViewById(R.id.search_view);
        
        databaseReference = FirebaseDatabase.getInstance().getReference();
        usersList = new ArrayList<>();

        //initRecyclerView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.isEmpty()){
                    usersList.clear();
                    adapter = new SearchedAccountAdapter(usersList, SearchAccountActivity.this);
                    binding.accRecyclerView.setAdapter(adapter);
                }
                else {
                    Log.i(TAG, "onQueryTextSubmit: "+query);
                    processQuery(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()){
                    usersList.clear();
                    adapter = new SearchedAccountAdapter(usersList, SearchAccountActivity.this);
                    binding.accRecyclerView.setAdapter(adapter);
                }
                else {
                    Log.i(TAG, "onQueryTextSubmit: "+newText);
                    processQuery(newText);
                }
                return false;
            }
        });


    }

    private void processQuery(String s) {


        Query userProfileQuery = databaseReference.child("Users").orderByChild("UserProfile/name").startAt(s).endAt(s+"\uf8ff");
        userProfileQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.i(TAG, "onDataChange: User - " + snapshot.getKey());
                usersList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Log.i(TAG, "onDataChange: User - " + userSnapshot.getKey());// getting uid
                    Log.i(TAG, "onDataChange: User - " + userSnapshot.getValue());
                    uid = userSnapshot.getKey();

                    databaseReference
                            .child("Users")
                            .child(userSnapshot.getKey())
                            .child("UserProfile")
                            .child("name")
                            .addValueEventListener(new ValueEventListener() {
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
                            .child(userSnapshot.getKey())
                            .child("UserProfile")
                            .child("ProfileImage")
                            .child("imageUrl")
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    imageUrl = String.valueOf(snapshot.getValue());
                                    Log.i(TAG, "onDataChange: " + imageUrl);
                                    Log.i(TAG, "onDataChange: UID -- "+userSnapshot.getKey());
                                    SearchAccountModel user = new SearchAccountModel(userSnapshot.getKey(), imageUrl, name);
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void initRecyclerView() {
        databaseReference
                .child("Users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.i(TAG, "onDataChange: User - " + snapshot.getKey());
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            Log.i(TAG, "onDataChange: User - " + userSnapshot.getKey());// getting uid
                            Log.i(TAG, "onDataChange: User - " + userSnapshot.getValue());
                            uid = userSnapshot.getKey();


                                databaseReference
                                        .child("Users")
                                        .child(userSnapshot.getKey())
                                        .child("UserProfile")
                                        .child("name")
                                        .addValueEventListener(new ValueEventListener() {
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
                                        .child(userSnapshot.getKey())
                                        .child("UserProfile")
                                        .child("ProfileImage")
                                        .child("imageUrl")
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                imageUrl = String.valueOf(snapshot.getValue());
                                                Log.i(TAG, "onDataChange: " + imageUrl);
                                                Log.i(TAG, "onDataChange: UID -- "+userSnapshot.getKey());
                                                SearchAccountModel user = new SearchAccountModel(userSnapshot.getKey(), imageUrl, name);
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

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "onCancelled: " + error.getMessage());
                    }
                });
    }

    @Override
    public void onUserProfileClicked(int position) {

        Toast.makeText(this, usersList.get(position).getUid(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SearchAccountActivity.this, SearchProfileActivity.class);
        intent.putExtra("uid", usersList.get(position).getUid());
        startActivity(intent);
        finish();

    }
}