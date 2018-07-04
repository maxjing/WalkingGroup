package ca.cmpt276.walkinggroup.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import static ca.cmpt276.walkinggroup.app.GoogleMapsActivity.JOINGROUP;

public class JoinGroupFragment extends AppCompatDialogFragment{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create the view to show
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.message_layout, null);

        Bundle mArgs = getArguments();

        Double selectedID = mArgs.getDouble(JOINGROUP,0);

        // Create a button listener
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("TAG", "You click the dialog button");
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        Toast.makeText(getActivity(),"Group ID: "+selectedID, Toast.LENGTH_SHORT).show();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        // Build the alert dialog
        return new AlertDialog.Builder(getActivity())
                .setTitle("Join group")
                .setView(v)
                .setPositiveButton(android.R.string.ok, listener)
                .setNegativeButton(android.R.string.cancel, listener)
                .create();
    }
}
