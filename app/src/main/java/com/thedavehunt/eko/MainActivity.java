package com.thedavehunt.eko;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {

    //used to register/signin users
    private FirebaseAuth auth;
    private CallbackManager mCallbackManager;

    private static final int RC_SIGN_IN =1;
    private GoogleSignInClient mGoogleSignInClient;

    private ProgressBar loadingCircle;
    private Button googleLoginBtn;
    private LoginButton fbLoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get views
        loadingCircle = (ProgressBar)findViewById(R.id.loadingCircle);
        loadingCircle.setVisibility(View.INVISIBLE);
        googleLoginBtn = (Button)findViewById(R.id.googleBtn);
        fbLoginBtn = (LoginButton) findViewById(R.id.loginButtonFacebook);

        //get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        //GUEST SIGN IN
        //button to allow guest users to use the app
        Button guestLogin = (Button)findViewById(R.id.mainGuestLogin);
        guestLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent landingpage = new Intent(MainActivity.this, LandingPage.class);
                startActivity(landingpage);
            }
        });

        //GOOGLE SIGN IN
        //Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //set google sign in click listener
        googleLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingCircle.setVisibility(View.VISIBLE);
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });


        //FACEBOOK SIGN IN
        mCallbackManager = CallbackManager.Factory.create();

        //get username and email from facebook profile
        fbLoginBtn.setReadPermissions("email", "public_profile");
        fbLoginBtn.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            //signed into facebook account
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
                loadingCircle.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });


        //request permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.INTERNET
                },10);
                return;
            }
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        loadingCircle.setVisibility(View.INVISIBLE);
        openLanding();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from Google/Facebook sign in intents
        //google login
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
        //facebook login
        else if(requestCode>0){
            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
        else{
        }
    }

    //sign into firebase with Google account
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        checkCredential(credential);
    }

    //sign into firebase with Facebook account
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        checkCredential(credential);
    }

    private void checkCredential(AuthCredential credential){
        auth.signInWithCredential(credential)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                // Sign in success, open landing page
                Log.d(TAG, "signInWithCredential:success");
                openLanding();

            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInWithCredential:failure", task.getException());
                Toast.makeText(getApplicationContext(),"Unable to authenticate user: "+task.getException(),Toast.LENGTH_SHORT).show();
            }

            }
        });
    }


    private void openLanding()
    {
        //check if user is signed in
        FirebaseUser user = auth.getCurrentUser();

        //if user signed in
        if(user!=null) {
            Toast.makeText(MainActivity.this,"Welcome " + user.getDisplayName(),Toast.LENGTH_SHORT).show();
            Intent landingpage = new Intent(MainActivity.this, LandingPage.class);
            startActivity(landingpage);
        }

    }



}
