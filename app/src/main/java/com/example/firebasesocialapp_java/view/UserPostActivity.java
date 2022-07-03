package com.example.firebasesocialapp_java.view;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.style.TabStopSpan;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.example.firebasesocialapp_java.databinding.ActivityUserPostBinding;
import com.example.firebasesocialapp_java.model.Post;
import com.example.firebasesocialapp_java.services.UploadImageService;
import com.example.firebasesocialapp_java.util.ItemClickInterface;
import com.example.firebasesocialapp_java.util.UserPostAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class UserPostActivity extends AppCompatActivity implements ItemClickInterface {

    private static final String TAG = "UserPostActivity";
    private ActivityUserPostBinding binding;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseAuth auth;
    private ArrayList<Post> postArrayList;
    private UserPostAdapter adapter;
    int position;

    private ActivityResultLauncher<String> activityResultLauncher;
    private ActivityResultLauncher<String> activityImageUpload;
    private Uri updateImgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getUpdateImg();


        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage
                .getInstance()
                .getReference(auth.getCurrentUser().getUid())
                .child("Posts");

        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        postArrayList = new ArrayList<>();

        databaseReference
                .child("Users")
                .child(auth.getUid())
                .child("Posts")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        postArrayList.clear();
                        Log.d(TAG, "onDataChange: " + snapshot.getChildren());

                        if (snapshot.exists()) {

                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                Log.d(TAG, "Posts: " + postSnapshot.getValue().toString());
                                Post post = postSnapshot.getValue(Post.class);
                                postArrayList.add(post);
                                adapter = new UserPostAdapter(UserPostActivity.this, postArrayList, UserPostActivity.this);
                                binding.recyclerView.setAdapter(adapter);
                            }

                            if (postArrayList.size() == 0) {
                                Toast.makeText(UserPostActivity.this, "No posts", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Log.e(TAG, "onDataChange: snapshot is not exits");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(UserPostActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }



    private void getUpdateImg() {

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri imgUri) {

                if (imgUri != null) {
                    updateImgUri = imgUri;
                    upadte();
                } else {
                    Toast.makeText(UserPostActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void upadte() {
        Post postToUpdate = postArrayList.get(position);

        StorageReference imgRef = storageReference.getStorage().getReferenceFromUrl(postToUpdate.getImageUrl());

        imgRef.putFile(updateImgUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri newUri) {

                                        postToUpdate.setImageUrl(newUri.toString());
                                        databaseReference
                                                .child("Users")
                                                .child(auth.getUid())
                                                .child("Posts")
                                                .child(postToUpdate.getPostId())
                                                .setValue(postToUpdate)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(UserPostActivity.this, "Updated post " + postToUpdate.getPostId(), Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(UserPostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        Log.e(TAG, "onFailure: update DB" + e.getMessage());
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "onFailure: UrlDownload " + e.getMessage());
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserPostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onFailure: " + e.getMessage());
                    }
                });

    }

    private String getImageExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    @Override
    public void onItemDelete(int position) {

        Post postToDelete = postArrayList.get(position);

        StorageReference imgRef = storageReference.getStorage().getReferenceFromUrl(postToDelete.getImageUrl());
        imgRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                databaseReference
                        .child("Users")
                        .child(auth.getUid())
                        .child("Posts")
                        .child(postToDelete.getPostId())
                        .removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(UserPostActivity.this, "Post deleted", Toast.LENGTH_SHORT).show();
                                adapter.notifyItemRemoved(position);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UserPostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });
    }

    @Override
    public void onItemUpdate(int position) {

        this.position = position;

        activityResultLauncher.launch("image/*");

    }
}