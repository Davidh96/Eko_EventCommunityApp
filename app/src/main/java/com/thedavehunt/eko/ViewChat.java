package com.thedavehunt.eko;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ViewChat extends AppCompatActivity {

    DatabaseHelper dbm;
    SQLiteDatabase db;

    ArrayList<MessageDoc> messageList;
    private MessageListAdpater listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_chat);

        Intent i = getIntent();

        //get id of event selected
        String id = i.getStringExtra("id");

        Toast.makeText(getApplicationContext(),id,Toast.LENGTH_SHORT).show();

        dbm = new DatabaseHelper(getApplicationContext());
        db = dbm.getReadableDatabase();
        Cursor results = dbm.retrieveChat(db,id);

        String messageID;
        String timestamp;
        String senderID;
        String messageData;
        String messageType;

        //create event list to display in list view
        //needs to be cleared everytime
        messageList = new ArrayList<MessageDoc>();

        while(results.moveToNext()){
            timestamp = results.getString(results.getColumnIndex("Timestamp"));
            senderID = results.getString(results.getColumnIndex("SenderID"));
            messageData = results.getString(results.getColumnIndex("Data"));
            messageType = results.getString(results.getColumnIndex("MessageType"));
            MessageDoc message = new MessageDoc("temp",timestamp,senderID,messageData,messageType);
            messageList.add(message);
        }

        //initialise adapter
        listAdapter = new MessageListAdpater(ViewChat.this,messageList);

        ListView list = (ListView)findViewById(R.id.listChatView);
        //set adapter for list view
        list.setAdapter(listAdapter);


    }
}
