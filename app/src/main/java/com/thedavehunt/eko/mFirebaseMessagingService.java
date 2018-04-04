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

        Log.d("FirebaseMessage", "From: " + remoteMessage.getFrom());

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

            //convert string to int array
            String[] items = msgData.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");

            int[] results = new int[items.length];

            for (int i = 0; i < items.length; i++) {
                try {
                    results[i] = Integer.parseInt(items[i]);
                } catch (NumberFormatException nfe) {
                    //NOTE: write something here if you need to recover from formatting errors
                };
            }

            //convert int array to byte array

           byte[] enc = new byte[results.length];

            for(int i=0;i<results.length;i++){
                enc[i]=(byte)results[i];
            }

            EncryptionManager em = new EncryptionManager();
            msgData = em.decrypt(enc,em.convertStringToPriv(em.getPrivateKey()));

            Date currentTime = Calendar.getInstance().getTime();

            db.insertData(msgData,currentTime.toString(),msgFromID,"Received");
            ContactDoc contact = new ContactDoc(msgFromToken,msgFromID,msgFromName);
            contact.setContactPublicKey(msgFromKey);
            Cursor result = db.retrieveContact(db.getWritableDatabase(),msgFromID);
            if(result.getCount()==0) {
                Log.d("InsertingContact","Insert");
                db.insertContact(contact);
            }
            else{
                //Cursor result = db.retrieveContact(db.getWritableDatabase(),msgFromID);

                Log.d("AddingContact",result.getCount()+ "");

                String publicKey=null;

                while(result.moveToNext()){
                    publicKey=result.getString(result.getColumnIndex("fromPublicKey"));
                }

                contact.setContactPublicKey(publicKey);

                db.updateContact(contact);
            }

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
