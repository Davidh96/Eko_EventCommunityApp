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

//list adapter for displaying event members
public class MemberListAdapter extends ArrayAdapter<EventMember> {

        private List <EventMember> eventMembers;
        private Context context;


        public MemberListAdapter(Context context, List<EventMember> list) {
            super(context,R.layout.custom_row, list);
            this.context=context;
            eventMembers=list;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater infl = LayoutInflater.from(context);
            View custom = infl.inflate(R.layout.custom_row_member_list,null,true);

            EventMember member = eventMembers.get(position);
            //get views
            TextView memberNameTxt = (TextView)custom.findViewById(R.id.eventMemberName);
            //set text to member name
            memberNameTxt.setText(member.getName());

            return custom;
        }
}
