package com.thedavehunt.eko;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ContactDisplay extends FragmentActivity implements AddContactFragment.AddContactDialogListener {

    LocalDatabaseManager dbm;
    SQLiteDatabase db;

    private ArrayList<ContactDoc> contactList;
    private ContactListAdapter listAdapter;
    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_display);

        //create contact list to display in list view
        contactList = new ArrayList<ContactDoc>();

        retrieveContactList();

        //initialise adapter
        listAdapter = new ContactListAdapter(ContactDisplay.this,contactList);

        ListView list = (ListView)findViewById(R.id.listChatDisplay);
        //set adapter for list view
        list.setAdapter(listAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                Intent viewChat = new Intent(ContactDisplay.this, ViewChat.class);

                viewChat.putExtra("id", contactList.get(i).getContactID() );

                startActivity(viewChat);

            }
        });
    }

    private void retrieveContactList(){

        dbm = new LocalDatabaseManager(getApplicationContext());
        db = dbm.getReadableDatabase();

        Cursor results = dbm.retrieveContacts(db);

        String senderName;
        String senderToken;
        String senderID;
        String senderKey;

        while(results.moveToNext()){
            senderName = results.getString(results.getColumnIndex("contactName"));
            senderToken = results.getString(results.getColumnIndex("contactToken"));
            senderID = results.getString(results.getColumnIndex("contactID"));
            senderKey = results.getString(results.getColumnIndex("contactPublicKey"));
            ContactDoc message = new ContactDoc(senderToken, senderID, senderName,senderKey);
            contactList.add(message);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //when new contact has been added
        IntentFilter intentFilter = new IntentFilter(
                "ContactAdded");

        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                //update contact list

                refreshContactList();
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

    //shows time fragment
    public void showAddContactFragment(View v){
        AddContactFragment addContactFragment = new AddContactFragment();
        addContactFragment.show(getSupportFragmentManager(),"tag");
    }

    public void returnContactID(String contactID) {
        Toast.makeText(getApplicationContext(),"Contact Added",Toast.LENGTH_SHORT).show();
        LocalDatabaseManager dbm = new LocalDatabaseManager(getApplicationContext());
        dbm.addContact(contactID);
        refreshContactList();

    }

    private void refreshContactList(){
        listAdapter.clear();

        //get new list
        retrieveContactList();

        //update list
        listAdapter.notifyDataSetChanged();
    }
}
