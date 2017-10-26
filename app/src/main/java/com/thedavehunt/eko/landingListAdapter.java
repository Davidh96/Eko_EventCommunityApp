package com.thedavehunt.eko;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 21/10/17.
 */

//public class landingListAdapter extends ArrayAdapter<String> {
//
//    public landingListAdapter(Context context, ArrayList<String> list1) {
//        super(context,R.layout.custom_row, list1);
//    }
//
//    @NonNull
//    @Override
//    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        LayoutInflater infl = LayoutInflater.from(getContext());
//        View custom = infl.inflate(R.layout.custom_row,parent,false);
//
//        String item = getItem(position);
//        TextView text1 =(TextView)custom.findViewById(R.id.text1);
//
//        text1.setText(item);
//
//        return custom;
//    }
//}

public class landingListAdapter extends ArrayAdapter<eventDoc> {

    private List<eventDoc> eventList;

    public landingListAdapter(Context context, List<eventDoc> list1) {
        super(context,R.layout.custom_row, list1);
        eventList=list1;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater infl = LayoutInflater.from(getContext());
        View custom = infl.inflate(R.layout.custom_row,null,true);

        eventDoc event = eventList.get(position);
        //String item = getItem(position);
        TextView text1 =(TextView)custom.findViewById(R.id.text1);

        text1.setText(event.getEventName());

        return custom;
    }
}