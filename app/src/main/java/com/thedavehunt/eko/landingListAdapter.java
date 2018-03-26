package com.thedavehunt.eko;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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

public class landingListAdapter extends ArrayAdapter<eventDoc> {

    private List<eventDoc> eventList;

    public landingListAdapter(Context context, List<eventDoc> list) {
        super(context,R.layout.custom_row, list);
        eventList=list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater infl = LayoutInflater.from(getContext());
        View custom = infl.inflate(R.layout.custom_row,null,true);

        eventDoc event = eventList.get(position);
        TextView eventNameText =(TextView)custom.findViewById(R.id.textEventName);
        TextView eventDescriptionText =(TextView)custom.findViewById(R.id.textEventDescription);
        TextView eventCategoryText =(TextView)custom.findViewById(R.id.textEventCategory);
        TextView eventAuthorText =(TextView)custom.findViewById(R.id.textEventAuthor);
        TextView eventPopularText = (TextView)custom.findViewById(R.id.textEventPopular);

        //set event information
        eventNameText.setText(event.getEventName());
        eventDescriptionText.setText(event.getEventDescription());
        eventCategoryText.setText("Category: " + event.getEventCategory());
        eventAuthorText.setText("Creator: " + event.getEventAuthor());

        //if recomendation
        if(position==0){
            eventPopularText.setText("Recommended");
            custom.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

        }

        return custom;
    }
}