package com.thedavehunt.eko;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {

    //used to register/ssignin users
    private FirebaseAuth auth;
    private  CallbackManager mCallbackManager;
    private FirebaseAuth.AuthStateListener mAuthListener;

    /*
    private EditText emailEdt;
    private EditText passwordEdt;
    private Button registerBtn;
    private Button signBtn;
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get Firebase auth instance
        auth=FirebaseAuth.getInstance();

        /*
        emailEdt = (EditText)findViewById(R.id.emailEdit);
        passwordEdt = (EditText)findViewById(R.id.passwordEdit);
        registerBtn = (Button)findViewById(R.id.registerBtn);
        signBtn = (Button)findViewById(R.id.signinBtn);
        */


        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.loginButtonFacebook);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            //signed into facebook account
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent landingpage = new Intent(MainActivity.this,landingPage.class);
                    startActivity(landingpage);

                } else {
                    Log.d("TG", "SIGNED OUT");
                }
            }
        };


        /*
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
        */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser user = auth.getCurrentUser();
//        Toast.makeText(MainActivity.this,"Welcome " + user.getDisplayName(),Toast.LENGTH_SHORT).show();
//        if(user!=null) {
//            Intent landingpage = new Intent(MainActivity.this, landingPage.class);
//            startActivity(landingpage);
//        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();

                            Toast.makeText(MainActivity.this,"Welcome " + user.getDisplayName(),Toast.LENGTH_SHORT).show();
                            Intent landingpage = new Intent(MainActivity.this,landingPage.class);
                            startActivity(landingpage);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Unable to authenticate user",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }


    //code for signing in via an email and password
    /*

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
*/

}
