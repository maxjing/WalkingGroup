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

import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

import static ca.cmpt276.walkinggroup.app.GoogleMapsActivity.LATITUDE;
import static ca.cmpt276.walkinggroup.app.GoogleMapsActivity.LONGTITUDE;
import static ca.cmpt276.walkinggroup.app.GroupCreateActivity.PLACENAME;

public class RemoveMessage extends AppCompatDialogFragment {
//
//    private WGServerProxy proxy;
//    private String token;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.message_layout,null);

//        Bundle mArgs = getArguments();
//        long groupId = mArgs.getLong(GROUP_REMOVE);
//        long userId = mArgs.getLong(USER_REMOVE);

//        SharedPreferences dataToGet = getActivity().getSharedPreferences("userPref",0);
//               // getApplicationContext().getSharedPreferences("userPref",0);
//        token = dataToGet.getString("userToken","");
//        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
        DialogInterface.OnClickListener listener =  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                Log.i("TAG", "You click the dialog button");
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        try {
                            Intent intent = new Intent();
                            intent.putExtra("REMOVE","remove");
                        } catch (Exception e) {

                        }

                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        return new AlertDialog.Builder(getActivity())
                .setTitle("Leave Group")
                .setView(v)
                .setPositiveButton(android.R.string.ok, listener)
                .setNegativeButton(android.R.string.cancel, listener)
                .create();
    }

}
