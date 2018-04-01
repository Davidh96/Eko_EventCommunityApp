package com.thedavehunt.eko;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ViewChat extends AppCompatActivity {

    DatabaseHelper dbm;
    SQLiteDatabase db;

    ArrayList<MessageDoc> messageList;
    public static MessageListAdpater listAdapter;

    Button sendMessageBtn;
    EditText messageTextEdit;

    private FirebaseUser user;
    ContactDoc contact=null;
    String myToken;

    public ListView list;

    static Cursor results;
    String id;

    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_chat);

        //getApplicationContext().registerReceiver(android.content.BroadcastReceiver, android.content.IntentFilter);

        Intent i = getIntent();

        //get id of event selected
        id = i.getStringExtra("id");

        user = FirebaseAuth.getInstance().getCurrentUser();

        Toast.makeText(getApplicationContext(),id,Toast.LENGTH_SHORT).show();

        dbm = new DatabaseHelper(getApplicationContext());
        db = dbm.getReadableDatabase();

        messageList = new ArrayList<MessageDoc>();


        retrieveMessages();

        results = dbm.retrieveContact(db,id);

        String senderName;
        //String senderMessage;
        String senderToken;
        String senderID;

        //create event list to display in list view
        //needs to be cleared everytime
        //contactList = new ArrayList<ContactDoc>();


        while(results.moveToNext()){
            senderName = results.getString(results.getColumnIndex("fromName"));
            senderToken = results.getString(results.getColumnIndex("fromToken"));
            senderID = results.getString(results.getColumnIndex("fromID"));
            contact = new ContactDoc(senderToken, senderID, senderName);
            //contactList.add(message);
        }

        results = dbm.retrieveToken(db,"temp");


        while(results.moveToNext()){
            myToken = results.getString(results.getColumnIndex("TokenValue"));
//            senderToken = results.getString(results.getColumnIndex("fromToken"));
//            senderID = results.getString(results.getColumnIndex("fromID"));
//            contact = new ContactDoc(senderToken, senderID, senderName);
            //contactList.add(message);
        }



        Toast.makeText(getApplicationContext(),contact.getContactToken(),Toast.LENGTH_LONG).show();
        //initialise adapter


        messageTextEdit = (EditText)findViewById(R.id.editMessageChat);
        sendMessageBtn = (Button)findViewById(R.id.buttonSendChat);

        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        listAdapter = new MessageListAdpater(ViewChat.this,messageList);

        list = (ListView)findViewById(R.id.listChatView);
        //set adapter for list view
        list.setAdapter(listAdapter);

        list.setSelection(list.getAdapter().getCount()-1);



    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        IntentFilter intentFilter = new IntentFilter(
                "com.thedavehunt.eko");

        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                listAdapter.clear();

                retrieveMessages();


                listAdapter.notifyDataSetChanged();
            }
        };
        //registering our receiver
        this.registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        //unregister our receiver
        this.unregisterReceiver(this.mReceiver);
    }

    public void retrieveMessages(){

        results = dbm.retrieveChat(db,id);
        String messageID;
        String timestamp;
        String senderID;
        String messageData;
        String messageType;

        //create event list to display in list view
        //needs to be cleared everytime


        while(results.moveToNext()){
            timestamp = results.getString(results.getColumnIndex("Timestamp"));
            senderID = results.getString(results.getColumnIndex("SenderID"));
            messageData = results.getString(results.getColumnIndex("Data"));
            messageType = results.getString(results.getColumnIndex("MessageType"));
            MessageDoc message = new MessageDoc("temp",timestamp,senderID,messageData,messageType);
            messageList.add(message);
        }

        Toast.makeText(getApplicationContext(),messageList.size() + "",Toast.LENGTH_SHORT).show();




    }

    private void sendMessage(){

        HashMap req = new HashMap();
        req.put("to",contact.getContactToken());
        req.put("fromToken",myToken);
        req.put("fromID", user.getUid());
        req.put("fromName", user.getDisplayName());
        req.put("data", messageTextEdit.getText().toString());
        Date currentTime = Calendar.getInstance().getTime();
        dbm.insertData(messageTextEdit.getText().toString(),currentTime.toString(),contact.getContactID(),"Sent");

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        //Toast.makeText(getApplicationContext(),filterCategory,Toast.LENGTH_SHORT).show();
        String url = "http://188.166.98.100/sendMsg";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,new JSONObject(req),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //hiding the loading after completion
                        //loadingCircle.setVisibility(View.INVISIBLE);

//                        try {

                            //getting the whole json object from the response
                            JSONObject obj = response;


//                            String jsonOBj = obj.toString();
//
//                            //if more than one event
//                            while(jsonOBj.length()>2) {
//
//                                String startString = "{\"-";
//                                int ind = jsonOBj.indexOf(startString) + startString.length();
//                                String id = jsonOBj.substring(ind, jsonOBj.substring(ind).indexOf("\"") + ind);
//
//                                JSONObject eventObj = obj.getJSONObject("-" + id);
//
//                                //place event details into an eventDoc
//                                eventDoc event = new eventDoc(eventObj.getString("id"), eventObj.getString("eventName"), eventObj.getString("eventAuthor"), eventObj.getString("eventAuthorID"),
//                                        eventObj.getString("eventDescription"), eventObj.getString("eventCategory"), eventObj.getString("eventLocation"), eventObj.getString("eventDate"),
//                                        eventObj.getString("eventTime"));
//
//                                //filter out results based on category
//                                if(filter.equals("All") || filter.equals(event.eventCategory)) {
//                                    //add event to list
//                                    eventList.add(event);
//                                }
//
//
//                                //get next event in list
//                                String middle = "{\"-" + id + "\":" + eventObj.toString() + ",";
//                                String last ="{\"-" + id + "\":" + eventObj.toString();
//
//                                //if we haven't reached end of list
//                                if(jsonOBj.indexOf(middle)>=0){
//                                    jsonOBj = jsonOBj.replace(middle, "{");
//                                }
//                                else{
//                                    jsonOBj = jsonOBj.replace(last,"{");
//                                }
//
//
//                            }
//
//                            //initialise adapter
//                            listAdapter = new landingListAdapter(LandingPage.this,eventList);
//
//                            ListView list = (ListView)findViewById(R.id.listEventLanding);
//                            //set adapter for list view
//                            list.setAdapter(listAdapter);


//                        } catch (JSONException e) {
//
//                        }
                    }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

        requestQueue.add(jsonObjectRequest);

        listAdapter.clear();

        retrieveMessages();


        listAdapter.notifyDataSetChanged();
        //list.invalidateViews();

        messageTextEdit.setText("");

    }


}
