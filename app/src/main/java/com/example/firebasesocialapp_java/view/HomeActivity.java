package com.example.firebasesocialapp_java.view;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.security.keystore.StrongBoxUnavailableException;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.firebasesocialapp_java.databinding.ActivityHomeBinding;
import com.example.firebasesocialapp_java.model.Post;
import com.example.firebasesocialapp_java.model.ProfileImage;
import com.example.firebasesocialapp_java.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private ActivityHomeBinding binding;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private ActivityResultLauncher<String> activityResultLauncher;
    private StorageReference storageReference;
    protected Uri imgUri;
    private static String author;
    private static String userId;
    private static String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getImageUri();
        createNewPost();
        //loadProfileImage();

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference(auth.getCurrentUser().getUid()).child("ProfileImage");

        databaseReference.child("Users").child(auth.getUid()).child("UserProfile").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    binding.username.setText(
                            "UserInfo: \n" +
                                    user.getUsername() + "\n"
                                    + user.getName() + "\n"
                                    + user.getPhoneNumber()

                    );

                    loadProfileImage();


                }
                Log.d(TAG, "onDataChange: " + snapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i(TAG, "onCancelled: " + error.getMessage());
            }
        });

        binding.signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                startActivity(new Intent(HomeActivity.this, MainActivity.class));
            }
        });

        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityResultLauncher.launch("image/*");
            }
        });

        binding.postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityResultLauncher.launch("image/*");
            }
        });

        binding.myPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, UserPostActivity.class));
                finish();
            }
        });

    }

    private void createNewPost() {

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {

                if (uri != null) {
                    imgUri = uri;
                    newPost();
                } else {
                    Toast.makeText(HomeActivity.this, "No image select", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void newPost() {

        StorageReference postImgStorage = FirebaseStorage
                .getInstance()
                .getReference(auth.getCurrentUser().getUid())
                .child("Posts")
                .child(System.currentTimeMillis() + "." + getImageExtension(imgUri));


        databaseReference.child("Users").child(auth.getUid()).child("UserProfile")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            author = user.getName();
                            userId = auth.getCurrentUser().getUid();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        postImgStorage.putFile(imgUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        postId = databaseReference.push().getKey();

                        taskSnapshot.getStorage().getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Post newPost = new Post(
                                                String.valueOf(System.currentTimeMillis()),
                                                author,
                                                userId,
                                                uri.toString(),
                                                0,
                                                postId
                                        );
                                        databaseReference
                                                .child("Users")
                                                .child(auth.getUid())
                                                .child("Posts")
                                                .child(postId)
                                                .setValue(newPost);
                                        Toast.makeText(HomeActivity.this, "Post created", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "onFailure: POST uploading failed" + e.getMessage());
                    }
                });

    }

    // Load image from firebase storage via real time db
    private void loadProfileImage() {
        databaseReference.child("Users").child(auth.getCurrentUser().getUid()).child("UserProfile").child("ProfileImage")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot profileImageSnapshot) {
                        Log.i(TAG, "onDataChange: " + profileImageSnapshot.getValue());
                        ProfileImage profileImage = profileImageSnapshot.getValue(ProfileImage.class);
                        Glide.with(HomeActivity.this).load(profileImage.getImageUrl()).into(binding.profileImage);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getImageUri() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {

                if (uri != null) {
                    imgUri = uri;
                    uploadProfileImg();
                } else {
                    Toast.makeText(HomeActivity.this, "No image select", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getImageExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadProfileImg() {
        if (imgUri != null) {
            StorageReference profileImageRef = storageReference
                    .child(System.currentTimeMillis() + "." + getImageExtension(imgUri));

            profileImageRef
                    .putFile(imgUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(HomeActivity.this, "Image upload successful", Toast.LENGTH_SHORT).show();
                            taskSnapshot.getStorage().getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            ProfileImage pi = new ProfileImage(uri.toString(), "profile mage");
                                            databaseReference.child("Users").child(auth.getUid()).child("UserProfile").child("ProfileImage")
                                                    .setValue(pi);
                                            Log.e(TAG, "onSuccess: " + uri);
                                        }
                                    });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "onFailure: " + e.getMessage());
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }


}