package com.thedavehunt.eko;

import android.content.Context;
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

    public void sendMessage(ContactDoc contact,String myToken,String message){
        HashMap req = new HashMap();
        req.put("to",contact.getContactToken());
        req.put("fromToken",myToken);
        req.put("fromID", user.getUid());
        req.put("fromName", user.getDisplayName());
        req.put("data", message);
        Date currentTime = Calendar.getInstance().getTime();
        dbm.insertData(message,currentTime.toString(),contact.getContactID(),"Sent");

        Toast.makeText(getApplicationContext(),"Hereee",Toast.LENGTH_SHORT).show();

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        String url = context.getResources().getString(R.string.serverURLsendmsg);
        //String url = "http://188.166.98.100/sendMsg";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,new JSONObject(req),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject obj = response;
                        //Toast.makeText(getApplicationContext(),obj.toString(),Toast.LENGTH_SHORT).show();

                    }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    public void initiateChat(String contactToken){

        ContactDoc contact = new ContactDoc(contactToken,"unknown","unknown");
        DatabaseHelper db = new DatabaseHelper(context);
        String myToken = db.retrieveToken(db.getWritableDatabase(),"temp");

        this.sendMessage(contact,myToken,"Initiate Conversation");

    }
}
