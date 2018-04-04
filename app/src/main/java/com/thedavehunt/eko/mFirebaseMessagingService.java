package com.thedavehunt.eko;

import android.database.Cursor;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Calendar;
import java.util.Date;

import android.content.Intent;

public class mFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("FirebaseMessage", "Message data payload: " + remoteMessage.getData());

            //add message to offline db
            DatabaseHelper db = new DatabaseHelper(getApplicationContext());

            String msgFromToken = remoteMessage.getData().get("fromToken").toString();
            String msgFromID = remoteMessage.getData().get("fromID").toString();
            String msgFromKey = remoteMessage.getData().get("fromKey").toString();
            String msgFromName = remoteMessage.getData().get("fromName").toString();
            String msgData = remoteMessage.getData().get("msg").toString();
            //get time recieved
            Date currentTime = Calendar.getInstance().getTime();

            byte []enc=convertDataToByte(msgData);

            EncryptionManager em = new EncryptionManager();
            msgData = em.decrypt(enc,em.convertStringToPriv(em.getPrivateKey()));

            db.insertData(msgData,currentTime.toString(),msgFromID,"Received");

            ContactDoc contact = new ContactDoc(msgFromToken,msgFromID,msgFromName,msgFromKey);

            Cursor result = db.retrieveContact(db.getWritableDatabase(),msgFromID);

            //check if contact is already in database
            if(result.getCount()==0) {
                db.insertContact(contact);
            }
            else{
                //update contact details, the contact username
                db.updateContact(contact);
            }

            //send broadcast that IM was received
            Intent i = new Intent("IMReceived");
            i.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            sendBroadcast(i);
            //send broadcast that contact was added
            Intent j = new Intent("ContactAdded");
            j.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            sendBroadcast(j);


        }

    }

    private byte[] convertDataToByte(String msgData){
        //convert string to int array
        String[] items = msgData.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");

        //convert int array to byte array
        byte[] encrypted = new byte[items.length];

        for (int i = 0; i < items.length; i++) {
            try {
                encrypted[i] = (byte)Integer.parseInt(items[i]);
            } catch (NumberFormatException nfe) {
            };
        }

        //return byte array
        return encrypted;
    }
}
