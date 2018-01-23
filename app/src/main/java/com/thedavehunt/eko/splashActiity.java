package com.thedavehunt.eko;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class splashActiity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_actiity);

        //set delay to 1 second
        int second= 1;
        //delay starting of main activity
        new Handler().postDelayed(new Runnable() {
            public void run() {
                //start main activity
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        }, second * 1000);
    }
}
