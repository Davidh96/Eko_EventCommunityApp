package com.thedavehunt.eko;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by david on 20/12/17.
 */

public class memberListAdapter extends ArrayAdapter<eventMember> {

        private List <eventMember> eventMembers;


        public memberListAdapter(Context context, List<eventMember> list) {
            super(context,R.layout.custom_row, list);
            eventMembers=list;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater infl = LayoutInflater.from(getContext());
            View custom = infl.inflate(R.layout.custom_row_member_list,null,true);

            eventMember member = eventMembers.get(position);
            TextView memberNameTxt = (TextView)custom.findViewById(R.id.eventMemberName);
            memberNameTxt.setText(member.getName());

//            eventDoc event = eventList.get(position);
//            TextView eventNameText =(TextView)custom.findViewById(R.id.textEventName);
//            TextView eventDescriptionText =(TextView)custom.findViewById(R.id.textEventDescription);
//            TextView eventCategoryText =(TextView)custom.findViewById(R.id.textEventCategory);
//            TextView eventAuthorText =(TextView)custom.findViewById(R.id.textEventAuthor);
//
//            //set event information
//            eventNameText.setText(event.getEventName());
//            eventDescriptionText.setText(event.getEventDescription());
//            eventCategoryText.setText("Category: " + event.getEventCategory());
//            eventAuthorText.setText("Creator: " + event.getEventAuthor());

            return custom;
        }
}
