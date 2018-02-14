package com.thedavehunt.eko;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

/**
 * Created by david on 05/02/18.
 */

public class selectTimeDialog extends AppCompatDialogFragment {

    private TimePicker timePicker;
    private selectTimeDialog.TimeDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_select_time,null);

        builder.setView(view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //format hour
                        String hour = ""+timePicker.getHour();
                        if(hour.length()==1){
                            hour="0" + hour;
                        }

                        //format minutes
                        String mins =""+timePicker.getMinute();
                        if(mins.length()==1){
                            mins="0" + mins;
                        }

                        String time = hour + ""+ mins;

                        listener.returnTime(time);
                    }
                });

        timePicker = (TimePicker)view.findViewById(R.id.timePickerFragment);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        listener = (selectTimeDialog.TimeDialogListener)context;
    }

    public interface TimeDialogListener{
        void returnTime(String time);


    }
}
