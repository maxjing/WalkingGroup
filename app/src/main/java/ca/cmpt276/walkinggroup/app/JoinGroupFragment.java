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
import android.widget.Toast;

import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

import static ca.cmpt276.walkinggroup.app.GoogleMapsActivity.JOINGROUP;
import static ca.cmpt276.walkinggroup.app.GoogleMapsActivity.MEETINGPLACE;
import static ca.cmpt276.walkinggroup.app.GoogleMapsActivity.USER_JOIN;

public class JoinGroupFragment extends AppCompatDialogFragment{
    private WGServerProxy proxy;
    private String token;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create the view to show
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.message_layout, null);

        Bundle mArgs = getArguments();

        long userId = mArgs.getLong(USER_JOIN);
        String meetingPlace = mArgs.getString(MEETINGPLACE);
        long selectedID = mArgs.getLong(JOINGROUP,0);

        Toast.makeText(getActivity(), "Meeting Place: "+ meetingPlace, Toast.LENGTH_SHORT).show();

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
                        Call<User> caller = proxy.getUserById(userId);
                        ProxyBuilder.callProxy(getActivity(), caller, returnedUser -> response(returnedUser));
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }

            private void response(User returnedUser) {
                Call<List<User>>  caller = proxy.addGroupMember(selectedID,returnedUser);
                ProxyBuilder.callProxy(getActivity(), caller, returnedList -> responseForJoining(returnedList));
            }

            private void responseForJoining(List<User> returnedList) {
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
