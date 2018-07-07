package ca.cmpt276.walkinggroup.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;

import static ca.cmpt276.walkinggroup.app.GoogleMapsActivity.JOINGROUP;

/**
 * Show a dialog to make sure that the user select the correct group he/she wants to join themselves of their children..
 */
public class JoinGroupFragment extends AppCompatDialogFragment{
    private WGServerProxy proxy;
    private String token;
    public static final String GROUP_DES = "groupDescription";
    private String groupDescription;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create the view to show
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.message_layout, null);

        Bundle mArgs = getArguments();

        long selectedID = mArgs.getLong(JOINGROUP);
        groupDescription = mArgs.getString(GROUP_DES);


        SharedPreferences dataToGet = getActivity().getSharedPreferences("userPref",0);
        token = dataToGet.getString("userToken","");
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
        // Create a button listener
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Log.i("TAG", "You click the dialog button");
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        Intent intent = JoinGroupActivity.makeIntent(getContext(),selectedID);
                        startActivity(intent);
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
                .setMessage(groupDescription)
                .setPositiveButton(android.R.string.ok, listener)
                .setNegativeButton(android.R.string.cancel, listener)
                .create();
    }
}
