package com.thedavehunt.eko;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class mFirebaseInstanceIdService extends FirebaseInstanceIdService {



    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("FirebaseToken", "Refreshed token: " + refreshedToken);
        //save token to local database
        DatabaseHelper dbm = new DatabaseHelper(getApplicationContext());
        dbm.insertToken("temp",refreshedToken);

    }
}
