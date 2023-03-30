package com.urban.clone.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mukesh.OnOtpCompletionListener;
import com.urban.clone.Helpers.CustomProgressDialog;
import com.urban.clone.Helpers.LocationHelper;
import com.urban.clone.Helpers.UserHelper;
import com.urban.clone.R;
import com.urban.clone.SessionManager;
import com.urban.clone.databinding.ActivityVerifyPhoneBinding;
import com.urban.clone.model.ModelUser;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class VerifyPhoneActivity extends AppCompatActivity {
    ActivityVerifyPhoneBinding binding;
    FirebaseAuth auth;
    String verificationID;
    CustomProgressDialog dialog;
    String UID;
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyPhoneBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(VerifyPhoneActivity.this, R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);


        dialog = new CustomProgressDialog(VerifyPhoneActivity.this);
        dialog.show();


        auth = FirebaseAuth.getInstance();
        phone = "+91" + getIntent().getStringExtra("phone");
        //String ph = getIntent().getStringExtra("phone");


        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(VerifyPhoneActivity.this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                    }

                    @Override
                    public void onCodeSent(@NonNull String verifyID, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verifyID, forceResendingToken);
                        dialog.dismiss();

                        verificationID = verifyID;

                    }
                }).build();


        PhoneAuthProvider.verifyPhoneNumber(options);


        binding.verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, binding.otpView.getText().toString());


                auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            try {

                                Query checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("phone").equalTo(phone);


                                checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if (snapshot.exists()) {

                                            ModelUser user = snapshot.child(auth.getUid()).getValue(ModelUser.class);

                                            SessionManager sessionManager = new SessionManager(VerifyPhoneActivity.this);

                                            sessionManager.createLoginSession(user.getUid(),user.getFullName(),user.getPhone(),user.getEmail(),user.getLocation().getLat(),user.getLocation().getLng(),user.getLocation().getTxt());
                                            dialog.dismiss();
                                            startActivity(new Intent(VerifyPhoneActivity.this, LocationMenuActivity.class));
                                            finish();

                                        } else {

                                            UID = auth.getCurrentUser().getUid();

                                            LocationHelper location = new LocationHelper("none", "none", "none");

                                            UserHelper user = new UserHelper(UID, "Verified User", phone, "none", location);

                                            FirebaseDatabase.getInstance().getReference("Users").child(UID).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    SessionManager sessionManager = new SessionManager(VerifyPhoneActivity.this);

                                                    sessionManager.createLoginSession(user.getUid(), user.getFullName(), user.getPhone(), user.getEmail(), user.getLocation().getLat(), user.getLocation().getLng(), user.getLocation().getTxt());
                                                    dialog.dismiss();
                                                    startActivity(new Intent(VerifyPhoneActivity.this, LocationMenuActivity.class));

                                                }
                                            });


                                        }


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        } else {
                            Toast.makeText(VerifyPhoneActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });

    }
}