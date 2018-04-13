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

/**
 * Created by david on 05/02/18.
 */

//displaya calander and returns a chosen date
public class SelectDateDialog extends AppCompatDialogFragment {
    private DatePicker datePicker;
    private DateDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_select_date,null);

        builder.setView(view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //format day
                        String day =""+datePicker.getDayOfMonth();
                        if(day.length()==1){
                            day="0" + day;
                        }

                        //format month
                        String month =""+(datePicker.getMonth()+1);
                        if(month.length()==1){
                            month="0" + month;
                        }

                        String date =datePicker.getYear() + "-" + month + "-"+ day;

                        //String date= datePicker.getYear() + "-" + datePicker.getMonth() + "-" + datePicker.getDayOfMonth();
                        listener.returnDate(date);
                    }
                });

        datePicker = (DatePicker)view.findViewById(R.id.datePickerFragment);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        listener = (DateDialogListener)context;
    }

    public interface DateDialogListener{
        void returnDate(String date);


    }
}
