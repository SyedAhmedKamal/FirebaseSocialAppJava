package com.example.firebasesocialapp_java.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.firebasesocialapp_java.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class SendOTPActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private Button buttonGetOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_otpactivity);

        final EditText inputMobile = findViewById(R.id.inputMobile);
        buttonGetOTP = findViewById(R.id.get_otp_btn);
        progressBar = findViewById(R.id.progressBar);

        buttonGetOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputMobile.getText().toString().trim().isEmpty()) {
                    Toast.makeText(SendOTPActivity.this, "Enter mobile", Toast.LENGTH_SHORT).show();
                    return;
                }

                sendOTP(inputMobile);
            }
        });

    }

    private void sendOTP(EditText inputMobile) {

        progressBar.setVisibility(View.VISIBLE);
        buttonGetOTP.setVisibility(View.INVISIBLE);

        PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        progressBar.setVisibility(View.INVISIBLE);
                        buttonGetOTP.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        buttonGetOTP.setVisibility(View.VISIBLE);
                        Toast.makeText(SendOTPActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationID,
                                           @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        progressBar.setVisibility(View.INVISIBLE);
                        buttonGetOTP.setVisibility(View.VISIBLE);

                        Intent intent = new Intent(SendOTPActivity.this, VerifyOTPActivity.class);
                        intent.putExtra("mobile", inputMobile.getText().toString());
                        intent.putExtra("verificationId", verificationID);
                        startActivity(intent);
                        finish();
                    }
                };

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder()
                        .setPhoneNumber("+92" + inputMobile.getText().toString())
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setCallbacks(callbacks)
                        .setActivity(this)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        FirebaseAuth.getInstance().getFirebaseAuthSettings().forceRecaptchaFlowForTesting(true);
    }
}