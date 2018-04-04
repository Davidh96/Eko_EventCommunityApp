package com.thedavehunt.eko;

import android.database.Cursor;
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

public class MessagingManager {

    private FirebaseUser user;
    private  DatabaseHelper dbm;
    private String url;

    public MessagingManager(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        dbm = new DatabaseHelper(getApplicationContext());
        url= getApplicationContext().getResources().getString(R.string.serverURLsendmsg);
    }

    public void sendMessage(ContactDoc contact,String myToken,String message) {
        EncryptionManager em = new EncryptionManager();

        HashMap req = new HashMap();
        req.put("to",contact.getContactToken());
        req.put("fromToken",myToken);
        req.put("fromID", user.getUid());
        req.put("fromName", user.getDisplayName());
        req.put("fromKey", em.getPublicKey());

        String publicKey = getPublicKey(contact.getContactID());

        while(publicKey!=null) {

            //encrypt the message with public key
            byte[] encrypted = em.encrypt(message, em.convertStringToPub(publicKey));

            //snend bytes array as string
            req.put("data", Arrays.toString(encrypted));

            Date currentTime = Calendar.getInstance().getTime();
            dbm.insertData(message, currentTime.toString(), contact.getContactID(), "Sent");

            //send message
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

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
                        }
                    });

            requestQueue.add(jsonObjectRequest);

            publicKey=null;
        }
    }

    public void initiateChat(String contactID){

        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        Cursor result = db.retrieveContact(db.getWritableDatabase(),contactID);

        ContactDoc contact=null;

        while(result.moveToNext()){
            String senderName = result.getString(result.getColumnIndex("fromName"));
            String id = result.getString(result.getColumnIndex("fromID"));
            String token = result.getString(result.getColumnIndex("fromToken"));
            String publicKey = null;
            contact = new ContactDoc(token,id,senderName,publicKey);
        }


        String myToken = db.retrieveToken(db.getWritableDatabase(),"temp");

        try {
            this.sendMessage(contact,myToken,"Initiate Conversation");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getPublicKey(String contactID){
        String publicKey=null;
        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        publicKey = db.retrieveContactPublicKey(db.getWritableDatabase(), contactID);

        return publicKey;
    }
}
