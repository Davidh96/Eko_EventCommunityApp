package com.thedavehunt.eko;

import android.app.Notification;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

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
        TextView messageDta =(TextView)custom.findViewById(R.id.textMessageRow);

        messageDta.setText(message.getMessageData());

        return custom;
    }

}
