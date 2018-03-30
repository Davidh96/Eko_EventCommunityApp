package com.thedavehunt.eko;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ContactDisplay extends Activity {
    DatabaseHelper dbm;
    SQLiteDatabase db;

    ArrayList<ContactDoc> contactList;
    private ContactListAdapter listAdapter;

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
        listAdapter = new ContactListAdapter(ContactDisplay.this,contactList);

        ListView list = (ListView)findViewById(R.id.listChatDisplay);
        //set adapter for list view
        list.setAdapter(listAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                Intent viewChat = new Intent(ContactDisplay.this, ViewChat.class);

                //eventDoc evnt = (eventDoc) eventList.get(i);
                Toast.makeText(getApplicationContext(), "Hello " + i, Toast.LENGTH_LONG);
                viewChat.putExtra("id", contactList.get(i).getContactID() );

                startActivity(viewChat);

            }
        });
    }
}
