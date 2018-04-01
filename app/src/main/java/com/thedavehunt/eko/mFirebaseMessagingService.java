package com.thedavehunt.eko;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Calendar;
import java.util.Date;

import android.content.Intent;

public class mFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("FirebaseMessage", "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("FirebaseMessage", "Message data payload: " + remoteMessage.getData());
            DatabaseHelper db = new DatabaseHelper(getApplicationContext());
            //String msgTo = remoteMessage.getData().get("to").toString();
            String msgFromToken = remoteMessage.getData().get("fromToken").toString();
            String msgFromID = remoteMessage.getData().get("fromID").toString();
            String msgFromName = remoteMessage.getData().get("fromName").toString();
            String msgData = remoteMessage.getData().get("msg").toString();

            Date currentTime = Calendar.getInstance().getTime();

            db.insertData(msgData,currentTime.toString(),msgFromID,"Received");
            db.insertContact(msgFromID,msgFromToken,msgFromName);

            Intent i = new Intent("com.thedavehunt.eko");
            i.setAction("com.thedavehunt.eko");
            i.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            sendBroadcast(i);


        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d("FirebaseMessage", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
}
