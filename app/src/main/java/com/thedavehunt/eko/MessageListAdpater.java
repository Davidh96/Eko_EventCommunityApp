package com.thedavehunt.eko;

import android.app.Notification;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static android.view.View.TEXT_ALIGNMENT_VIEW_END;

public class MessageListAdpater extends ArrayAdapter {

    private List<MessageDoc> messageList;

    public MessageListAdpater(Context context, List<MessageDoc> list) {
        super(context,R.layout.custom_row_message, list);
        messageList=list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater infl = LayoutInflater.from(getContext());
        View custom = infl.inflate(R.layout.custom_row_message,null,true);

        MessageDoc message = messageList.get(position);
        //get views
        TextView messageDta =(TextView)custom.findViewById(R.id.textMessageRow);
        TextView messageTime = (TextView)custom.findViewById(R.id.textDateTimeChat);

        //set message text
        messageDta.setText(message.getMessageData());
        //set time to time that message was recieved
        messageTime.setText(message.getTimestamp());

        //if the message is sent from this device
        if(message.getMessageType().equals("Sent")){
            //change background to accent
            custom.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            //align message to the right
            messageDta.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        }

        return custom;
    }

}
