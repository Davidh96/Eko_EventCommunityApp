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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class AddContactFragment extends AppCompatDialogFragment {
    private EditText addContactEdit;
    private TextView userIDContactText;
    private AddContactFragment.AddContactDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_add_contact,null);

        addContactEdit = (EditText) view.findViewById(R.id.editAddContact);
        userIDContactText = (TextView)view.findViewById(R.id.textUserIDContact);

        //display user id, used to add contacts
        userIDContactText.setText(user.getUid());

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
