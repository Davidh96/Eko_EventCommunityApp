package com.thedavehunt.eko;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TimePicker;

//displays a star rating and returns rating
public class RateFragment extends AppCompatDialogFragment {

    private RatingBar ratingPicker;
    private RateFragment.RatingDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_rate,null);

        //get views
        ratingPicker = (RatingBar) view.findViewById(R.id.ratingBarFrag);

        builder.setView(view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        float rating = ratingPicker.getRating();
                        //return rating
                        listener.returnRating(rating);
                    }
                });


        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        listener = (RateFragment.RatingDialogListener)context;
    }

    public interface RatingDialogListener{
        void returnRating(float rating);


    }

}
