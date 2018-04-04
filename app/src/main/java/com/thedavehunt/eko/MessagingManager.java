package com.thedavehunt.eko;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MessagingManager {

    private FirebaseUser user;
    private Context context;
    private  DatabaseHelper dbm;

    public MessagingManager(Context context){
        user = FirebaseAuth.getInstance().getCurrentUser();
        this.context=context;
        dbm = new DatabaseHelper(context);
    }

    public void sendMessage(ContactDoc contact,String myToken,String message) throws UnsupportedEncodingException {

        EncryptionManager em = new EncryptionManager();

        HashMap req = new HashMap();
        req.put("to",contact.getContactToken());
        req.put("fromToken",myToken);
        req.put("fromID", user.getUid());
        req.put("fromName", user.getDisplayName());
        req.put("fromKey", em.getPublicKey());

        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        String publicKey=null;

        publicKey = db.retrieveContactPublicKey(db.getWritableDatabase(), contact.getContactID());


        while(publicKey!=null) {

            Log.d("ContactKey", publicKey);

            byte[] encrypted = em.encrypt(message, em.convertStringToPub(publicKey));

            Log.d("DecryptedMessage", message);


            req.put("data", Arrays.toString(encrypted));


            Date currentTime = Calendar.getInstance().getTime();
            dbm.insertData(message, currentTime.toString(), contact.getContactID(), "Sent");

            Toast.makeText(getApplicationContext(), "Hereee", Toast.LENGTH_SHORT).show();

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

            String url = context.getResources().getString(R.string.serverURLsendmsg);

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
            String id = result.getString(result.getColumnIndex("fromID"));
            String token = result.getString(result.getColumnIndex("fromToken"));
            String publicKey = result.getString(result.getColumnIndex("fromPublicKey"));
            contact = new ContactDoc(token,id,"unknwon");
        }

        //ContactDoc contact = new ContactDoc(contactToken,"unknown","unknown");
        //DatabaseHelper db = new DatabaseHelper(context);
        String myToken = db.retrieveToken(db.getWritableDatabase(),"temp");

        try {
            this.sendMessage(contact,myToken,"Initiate Conversation");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
}
