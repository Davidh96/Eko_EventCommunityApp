package com.thedavehunt.eko;

import android.content.DialogInterface;
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

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    private EditText emailEdt;
    private EditText passwordEdt;
    private Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth=FirebaseAuth.getInstance();

        emailEdt = (EditText)findViewById(R.id.emailEdit);
        passwordEdt = (EditText)findViewById(R.id.passwordEdit);
        registerBtn = (Button)findViewById(R.id.registerBtn);


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser(){
        String email = emailEdt.getText().toString();
        String password = passwordEdt.getText().toString();

        Toast.makeText(this,email + " " + password,Toast.LENGTH_SHORT).show();

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                Toast.makeText(MainActivity.this,"Registered",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MainActivity.this,"Unable to register you at this time",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
