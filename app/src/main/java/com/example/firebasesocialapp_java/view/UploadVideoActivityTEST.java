package com.example.firebasesocialapp_java.view;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.example.firebasesocialapp_java.R;
import com.example.firebasesocialapp_java.databinding.ActivityUploadVideoTestBinding;
import com.example.firebasesocialapp_java.model.Post;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadVideoActivityTEST extends AppCompatActivity {

    private static final String TAG = "UploadVideoActivityTEST";
    private ActivityUploadVideoTestBinding binding;
    private ActivityResultLauncher<String> activityResultLauncher;
    private Uri videoUri;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private static String author;
    private static String userId;
    private static String postId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadVideoTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        uploadVideo();

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference(auth.getCurrentUser().getUid()).child("ProfileImage");

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

                if (uri != null) {
                    Toast.makeText(UploadVideoActivityTEST.this, uri.toString(), Toast.LENGTH_LONG).show();
                    videoUri = uri;
                    startUpload();
                } else {
                    Toast.makeText(UploadVideoActivityTEST.this, "No video select", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private String getVideoExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void startUpload() {
        if (videoUri != null) {

            // creating post instance
            StorageReference postImgStorage = FirebaseStorage
                    .getInstance()
                    .getReference(auth.getCurrentUser().getUid())
                    .child("Posts")
                    .child(System.currentTimeMillis() + "." + getVideoExtension(videoUri));

            // getting author name and userId of that user
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

            // post videoUri
            postImgStorage.putFile(videoUri)
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
                                            Toast.makeText(UploadVideoActivityTEST.this, "Post created", Toast.LENGTH_SHORT).show();
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
    }
}