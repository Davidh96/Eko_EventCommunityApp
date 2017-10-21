package com.thedavehunt.eko;

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

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    private EditText emailEdt;
    private EditText passwordEdt;
    private Button registerBtn;
    private Button signBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth=FirebaseAuth.getInstance();

        emailEdt = (EditText)findViewById(R.id.emailEdit);
        passwordEdt = (EditText)findViewById(R.id.passwordEdit);
        registerBtn = (Button)findViewById(R.id.registerBtn);
        signBtn = (Button)findViewById(R.id.signinBtn);


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //register user
                try {
                    registerUser();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        });

        signBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    signinUser();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void registerUser() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        //get details entered in by user
        String email = emailEdt.getText().toString();
        String password = passwordEdt.getText().toString();

        password = SHA1(password);


        //register user and check if rgistartion is complete
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

    private void signinUser() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        //get details entered in by user
        String email = emailEdt.getText().toString();
        String password = passwordEdt.getText().toString();

        password = SHA1(password);

        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //if user was signed in correctly
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this,"Signed In",Toast.LENGTH_SHORT).show();
                    Intent signedIn = new Intent(MainActivity.this,landingPage.class);
                    startActivity(signedIn);
                }
                //if user was not signed in correctly
                else{
                    Toast.makeText(MainActivity.this,"Unable to find account",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] textBytes = text.getBytes("iso-8859-1");
        md.update(textBytes, 0, textBytes.length);
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }
}
