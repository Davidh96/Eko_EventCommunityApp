package com.thedavehunt.eko;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

//adapter for contact list
public class ContactListAdapter extends ArrayAdapter {

    private List<ContactDoc> contactList;

    public ContactListAdapter(Context context, List<ContactDoc> list) {
        super(context,R.layout.custom_row_contact_list, list);
        contactList=list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater infl = LayoutInflater.from(getContext());
        View custom = infl.inflate(R.layout.custom_row_contact_list,null,true);

        ContactDoc contact = contactList.get(position);

        //get views
        TextView senderNameText =(TextView)custom.findViewById(R.id.textSenderName);
        TextView senderMessageText =(TextView)custom.findViewById(R.id.textMessageData);

        //get chat history
        LocalDatabaseManager db = new LocalDatabaseManager(getContext());
        Cursor results =db.retrieveChat(contact.getContactID());

        //get the last message from this chat
        results.moveToLast();
        if(results.getCount()>0) {
            String message = results.getString(results.getColumnIndex("Data"));
            String messageType = results.getString(results.getColumnIndex("MessageType"));
            //display last message and type of message
            senderMessageText.setText(messageType + ": " + message);
        }


        //set event information
        senderNameText.setText(contact.getContactName());

        //close database reference
        db.close();

        return custom;
    }
}
