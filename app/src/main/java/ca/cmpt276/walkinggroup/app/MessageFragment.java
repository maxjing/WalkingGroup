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

import static ca.cmpt276.walkinggroup.app.GoogleMapsActivity.LATITUDE;
import static ca.cmpt276.walkinggroup.app.GoogleMapsActivity.LONGTITUDE;
import static ca.cmpt276.walkinggroup.app.GoogleMapsActivity.MEETINGPLACE;
import static ca.cmpt276.walkinggroup.app.GoogleMapsActivity.MEETLAT;
import static ca.cmpt276.walkinggroup.app.GoogleMapsActivity.MEETLNG;
import static ca.cmpt276.walkinggroup.app.GroupCreateActivity.PLACENAME;

public class MessageFragment extends AppCompatDialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create the view to show
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.message_layout, null);

        Bundle mArgs = getArguments();
        Double Latitude = mArgs.getDouble(LATITUDE, 0);
        Double Longtitude = mArgs.getDouble(LONGTITUDE, 0);
        String PlaceName = mArgs.getString(PLACENAME, "");

        Double meetLat = mArgs.getDouble(MEETLAT, 0);
        Double meetLng = mArgs.getDouble(MEETLNG, 0);
        String meetingPlace = mArgs.getString(MEETINGPLACE);

        // Create a button listener
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("TAG", "You click the dialog button");
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:

                        try {
                            Intent intentToCreate = GroupCreateActivity.makeIntent(getActivity());
                            intentToCreate.putExtra(LATITUDE, Latitude);
                            intentToCreate.putExtra(LONGTITUDE, Longtitude);
                            intentToCreate.putExtra(PLACENAME, PlaceName);
                            intentToCreate.putExtra(MEETLAT, meetLat);
                            intentToCreate.putExtra(MEETLNG, meetLng);
                            intentToCreate.putExtra(MEETINGPLACE, meetingPlace);

                            startActivity(intentToCreate);
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "should create a group at selected location ", Toast.LENGTH_SHORT).show();
                        }

                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        // Build the alert dialog
        return new AlertDialog.Builder(getActivity())
                .setTitle("Create group")
                .setView(v)
                .setPositiveButton(android.R.string.ok, listener)
                .setNegativeButton(android.R.string.cancel, listener)
                .create();
    }
}
