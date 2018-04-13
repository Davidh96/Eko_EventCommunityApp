package com.thedavehunt.eko;

import android.database.Cursor;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import org.json.JSONObject;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MessagingManager  {

    private FirebaseUser user;
    private  LocalDatabaseManager db;
    private String url;

    public MessagingManager(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = new LocalDatabaseManager(getApplicationContext());
        url= getApplicationContext().getResources().getString(R.string.serverURLsendmsg);
    }

    public void sendMessage(ContactDoc contact,String myToken,String message) {
        EncryptionManager em = new EncryptionManager();

        //set the contents of JSON to be sent
        HashMap req = new HashMap();
        req.put("to",contact.getContactToken());
        req.put("fromToken",myToken);
        req.put("fromID", user.getUid());
        req.put("fromName", user.getDisplayName());
        req.put("fromKey", em.getKeyFromFile("publicKey.txt"));

        //get contacts public key
        String publicKey = getPublicKey(contact.getContactID());

        //encrypt the message
        byte[] encrypted = em.encrypt(message, em.convertStringToPub(publicKey));

        //send bytes array as string
        req.put("data", Arrays.toString(encrypted));

        //get current date/time
        Date currentTime = Calendar.getInstance().getTime();

        db.insertData(message, currentTime.toString(), contact.getContactID(), "Sent");

        //send message
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        //send message to server
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(req),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject obj = response;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("sent1",error.toString());
                    }
                });

        requestQueue.add(jsonObjectRequest);

    }


    //get contact public key
    public String getPublicKey(String contactID){
        String publicKey=null;
        LocalDatabaseManager db = new LocalDatabaseManager(getApplicationContext());
        //retrieve key from database
        publicKey = db.retrieveContactPublicKey(contactID);

        return publicKey;
    }
}
