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
        TextView senderNameText =(TextView)custom.findViewById(R.id.textSenderName);
        TextView senderMessageText =(TextView)custom.findViewById(R.id.textMessageData);

        DatabaseHelper db = new DatabaseHelper(getContext());
        Cursor results =db.retrieveChat(db.getWritableDatabase(),contact.getContactID());

        //get the last message from this chat
        results.moveToLast();
        String message = results.getString(results.getColumnIndex("Data"));
        String messageType = results.getString(results.getColumnIndex("MessageType"));


        //set event information
        senderNameText.setText(contact.getContactName());
        senderMessageText.setText(messageType + ": " + message);

        return custom;
    }
}
