package com.example.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TimeUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.TimeUnit;

public class RegistraionActivity extends AppCompatActivity {

    private EditText mUsername, mEmail, mPassword;
    private Button btnreg;
    private TextView mSignin;

    private ProgressDialog mDialog;

    //Firebase...

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSupportActionBar().hide();
        setContentView(R.layout.activity_registraion);

        mAuth = FirebaseAuth.getInstance();

        mDialog = new ProgressDialog(this);


        registration();

    }

    private void registration(){

        mUsername = findViewById(R.id.etUserName);
        mEmail= findViewById(R.id.etEmail);
        mPassword= findViewById(R.id.etPassword);
        btnreg= findViewById(R.id.btnRegistration);
        mSignin= findViewById(R.id.tvSignIn);

        btnreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = mUsername.getText().toString();
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if (TextUtils.isEmpty(username)){
                    mUsername.setError("UserName Required..");
                    return;
                }
                if (TextUtils.isEmpty(email)){
                    mEmail.setError("Email Required..");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    mPassword.setError("Password Required..");
                    return;
                }

                mDialog.setMessage("Processing..");
                mDialog.show();


                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        if (task.isSuccessful()){
                            mDialog.dismiss();
                            Toast.makeText(RegistraionActivity.this, "Regestration Complete..", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));


                        }else {
                            mDialog.dismiss();
                            Toast.makeText(RegistraionActivity.this, "Registration Failed..", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        mSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });


    }


}