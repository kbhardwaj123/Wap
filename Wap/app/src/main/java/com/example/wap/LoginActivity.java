package com.example.wap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private EditText phoneNo,code;
    private Button send;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String mVerificationId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        userIsLoggedIn();

        phoneNo = findViewById(R.id.phoneNumber);
        code = findViewById(R.id.code);
        send = findViewById(R.id.send);
        send.setOnClickListener((v) -> {
            if(mVerificationId !=null) {
                verifyPhoneNoWithCode();
            } else
                startPhoneVerification();
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                mVerificationId = s;
                send.setText("Verify Code");
            }
        };

    }

    private void  verifyPhoneNoWithCode() {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,code.getText().toString());
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(
                        this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    if(user!=null) {
                                        final DatabaseReference mUserDB = FirebaseDatabase
                                                .getInstance().getReference()
                                                .child("user").child(user.getUid());
                                        mUserDB.addListenerForSingleValueEvent(
                                                new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(!dataSnapshot.exists()) {
                                                    Map<String, Object> userMap = new HashMap<>();
                                                    userMap.put("phone",user.getPhoneNumber());
                                                    userMap.put("name",user.getPhoneNumber());
                                                    mUserDB.updateChildren(userMap);
                                                }
                                                userIsLoggedIn();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                    //userIsLoggedIn();
                                }
                            }
                        }
                );
    }

    private void userIsLoggedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            startActivity(new Intent(getApplicationContext(),MainPageActivity.class));
            finish();
            return;
        }
    }

    private void startPhoneVerification() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNo.getText().toString(),
                120,
                TimeUnit.SECONDS,
                this,
                mCallbacks
        );
    }
}
