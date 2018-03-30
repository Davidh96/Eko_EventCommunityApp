package com.thedavehunt.eko;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class ViewChat extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_chat);

        Intent i = getIntent();

        //get id of event selected
        String id = i.getStringExtra("id");

        Toast.makeText(getApplicationContext(),id,Toast.LENGTH_SHORT).show();
    }
}
