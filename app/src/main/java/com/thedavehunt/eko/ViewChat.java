package com.thedavehunt.eko;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ViewChat extends Activity {

    LocalDatabaseManager dbm;
    SQLiteDatabase db;

    private ArrayList<MessageDoc> messageList;
    public MessageListAdpater listAdapter;

    FloatingActionButton sendMessageBtn;
    EditText messageTextEdit;
    TextView chatTitleBar;

    private FirebaseUser user;
    ContactDoc contact=null;
    String myToken;

    public ListView list;

    Cursor results;
    String id;

    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_chat);

        //set views
        messageTextEdit = (EditText)findViewById(R.id.editMessageChat);
        sendMessageBtn = (FloatingActionButton)findViewById(R.id.buttonSendChat);
        chatTitleBar = (TextView)findViewById(R.id.textTitleBarChat);
        list = (ListView)findViewById(R.id.listChatView);

        Intent i = getIntent();

        //get id of contact selected
        id = i.getStringExtra("id");

        user = FirebaseAuth.getInstance().getCurrentUser();


        dbm = new LocalDatabaseManager(getApplicationContext());
        db = dbm.getReadableDatabase();

        messageList = new ArrayList<MessageDoc>();

        retrieveMessages();

        retrieveContactDetails();

        myToken = dbm.retrieveToken("temp");

        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messNospc = messageTextEdit.getText().toString().replaceAll("\\s+","");
                if(messNospc.length()>0) {
                    sendMessage();
                }
            }
        });

        //set list adapter
        listAdapter = new MessageListAdpater(ViewChat.this,messageList);
        //set adapter for list view
        list.setAdapter(listAdapter);

        //set focus on last message
        list.setSelection(list.getAdapter().getCount()-1);

        //dbm.close();

    }

    @Override
    protected void onResume() {
        super.onResume();

        //filter to receiveing messages
        IntentFilter intentFilter = new IntentFilter(
                "IMReceived");

        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                listAdapter.clear();

                retrieveMessages();


                listAdapter.notifyDataSetChanged();
            }
        };
        //register receiver
        this.registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregister receiver
        this.unregisterReceiver(this.mReceiver);
    }

    public void retrieveMessages(){

        results = dbm.retrieveChat(id);
        String timestamp;
        String senderID;
        String messageData;
        String messageType;

        while(results.moveToNext()){
            timestamp = results.getString(results.getColumnIndex("Timestamp"));
            senderID = results.getString(results.getColumnIndex("SenderID"));
            messageData = results.getString(results.getColumnIndex("Data"));
            messageType = results.getString(results.getColumnIndex("MessageType"));
            messageData = new String(messageData.getBytes());
            MessageDoc message = new MessageDoc(timestamp,senderID,messageData,messageType);

            messageList.add(message);
        }

        dbm.close();

    }

    private void retrieveContactDetails(){
        results = dbm.retrieveContact(id);

        String senderName="";
        String senderToken;
        String senderID;
        String senderKey;



        while(results.moveToNext()){
            senderName = results.getString(results.getColumnIndex("contactName"));
            senderToken = results.getString(results.getColumnIndex("contactToken"));
            senderID = results.getString(results.getColumnIndex("contactID"));
            senderKey = results.getString(results.getColumnIndex("contactPublicKey"));
            contact = new ContactDoc(senderToken, senderID, senderName,senderKey);
        }



        chatTitleBar.setText(senderName);

    }

    private void sendMessage(){

        MessagingManager mm = new MessagingManager();
        mm.sendMessage(contact,myToken,messageTextEdit.getText().toString());


        listAdapter.clear();

        retrieveMessages();

        listAdapter.notifyDataSetChanged();

        messageTextEdit.setText("");

    }


}
