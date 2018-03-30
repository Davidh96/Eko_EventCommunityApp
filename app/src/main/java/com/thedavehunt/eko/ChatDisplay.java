package com.thedavehunt.eko;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ChatDisplay extends Activity {
    DatabaseHelper dbm;
    SQLiteDatabase db;

    ArrayList<ContactDoc> contactList;
    private ChatListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_display);

        dbm = new DatabaseHelper(getApplicationContext());
        db = dbm.getReadableDatabase();
        Cursor results = dbm.retrieveContacts(db);

        String senderName;
        String senderMessage;
        String senderToken;
        String senderID;

        //create event list to display in list view
        //needs to be cleared everytime
        contactList = new ArrayList<ContactDoc>();

        while(results.moveToNext()){
            senderName = results.getString(results.getColumnIndex("fromName"));
            senderToken = results.getString(results.getColumnIndex("fromToken"));
            senderID = results.getString(results.getColumnIndex("fromID"));
            ContactDoc message = new ContactDoc(senderToken, senderID, senderName);
            contactList.add(message);
        }

        //initialise adapter
        listAdapter = new ChatListAdapter(ChatDisplay.this,contactList);

        ListView list = (ListView)findViewById(R.id.listChatDisplay);
        //set adapter for list view
        list.setAdapter(listAdapter);
    }
}
