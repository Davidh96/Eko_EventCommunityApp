package com.thedavehunt.eko;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Calendar;
import java.util.Date;

import android.content.Intent;

public class mFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d("FirebaseMessage", "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("FirebaseMessage", "Message data payload: " + remoteMessage.getData());

            //add message to offline db
            DatabaseHelper db = new DatabaseHelper(getApplicationContext());

            String msgFromToken = remoteMessage.getData().get("fromToken").toString();
            String msgFromID = remoteMessage.getData().get("fromID").toString();
            String msgFromName = remoteMessage.getData().get("fromName").toString();
            String msgData = remoteMessage.getData().get("msg").toString();
            //get time recieved
            Date currentTime = Calendar.getInstance().getTime();

            db.insertData(msgData,currentTime.toString(),msgFromID,"Received");
            db.insertContact(msgFromID,msgFromToken,msgFromName);

            //send broadcast that IM was received
            Intent i = new Intent("IMReceived");
            i.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            sendBroadcast(i);

            Intent j = new Intent("ContactAdded");
            j.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            sendBroadcast(j);


        }

    }
}
