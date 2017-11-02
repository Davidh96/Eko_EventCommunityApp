package com.thedavehunt.eko;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends Activity {

    //used to register/ssignin users
    private FirebaseAuth auth;

    private EditText emailEdt;
    private EditText passwordEdt;
    private Button registerBtn;
    private Button signBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get Firebase auth instance
        auth=FirebaseAuth.getInstance();

        emailEdt = (EditText)findViewById(R.id.emailEdit);
        passwordEdt = (EditText)findViewById(R.id.passwordEdit);
        registerBtn = (Button)findViewById(R.id.registerBtn);
        signBtn = (Button)findViewById(R.id.signinBtn);


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //register user
                registerUser();

            }
        });

        signBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sign up user
                signinUser();
            }
        });
    }

    //method for registering users
    private void registerUser() {
        //get details entered in by user
        String email = emailEdt.getText().toString();
        String password = passwordEdt.getText().toString();




        //register user and check if registartion is complete
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //if user was registered correctly
                if(task.isSuccessful()){
                Toast.makeText(MainActivity.this,"Registered",Toast.LENGTH_SHORT).show();
                }
                //if user was not registered correctly
                else{
                    Toast.makeText(MainActivity.this,"Unable to register you at this time",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //method for signing up users
    private void signinUser(){
        //get details entered in by user
        String email = emailEdt.getText().toString();
        String password = passwordEdt.getText().toString();


        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //if user was signed in correctly
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this,"Signed In",Toast.LENGTH_SHORT).show();
                    Intent landingpage = new Intent(MainActivity.this,landingPage.class);
                    startActivity(landingpage);


                }
                //if user was not signed in correctly
                else{
                    Toast.makeText(MainActivity.this,"Unable to find account",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
