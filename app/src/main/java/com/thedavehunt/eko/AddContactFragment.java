package com.thedavehunt.eko;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;




public class AddContactFragment extends AppCompatDialogFragment {
    private EditText addContactEdit;
    private AddContactFragment.AddContactDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_add_contact,null);

        addContactEdit = (EditText) view.findViewById(R.id.editAddContact);

        //create view in front of current activity
        builder.setView(view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String contactID = addContactEdit.getText().toString();
                        //return value of contactID to calling activity
                        listener.returnContactID(contactID);
                    }
                });



        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        listener = (AddContactFragment.AddContactDialogListener)context;
    }

    public interface AddContactDialogListener{
        void returnContactID(String contactID);


    }
}
