package ca.cmpt276.walkinggroup.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

import static ca.cmpt276.walkinggroup.app.GroupActivity.GROUP_REMOVE;
import static ca.cmpt276.walkinggroup.app.GroupActivity.POSITION;
import static ca.cmpt276.walkinggroup.app.GroupActivity.USER_REMOVE;

public class RemoveMessage extends AppCompatDialogFragment {
//
    private WGServerProxy proxy;
    private String token;
    private long userId;
    private List<Group> memberOfGroups;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.message_layout,null);

        Bundle mArgs = getArguments();
        long groupId = mArgs.getLong(GROUP_REMOVE);
        userId = mArgs.getLong(USER_REMOVE);
        int position = mArgs.getInt(POSITION);

        SharedPreferences dataToGet = getActivity().getSharedPreferences("userPref",0);
               // getApplicationContext().getSharedPreferences("userPref",0);
        token = dataToGet.getString("userToken","");
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
        Call<User> caller = proxy.getUserById(userId);
        ProxyBuilder.callProxy(getActivity(),caller,returnedUser -> response(returnedUser));
        DialogInterface.OnClickListener listener =  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                Log.i("TAG", "You click the dialog button");
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        try {
                            Call<Void> caller = proxy.removeGroupMember(groupId, userId);
                            ProxyBuilder.callProxy(getActivity(), caller, returned -> responseForRemove());
                            memberOfGroups.remove(position);
                            //        List<Group> groupsMember = returnedUser.getMemberOfGroups();
                            try {
                                String[] groupsMemberData = new String[memberOfGroups.size()];
                                //String msg = "";
                                for (int i = 0; i < memberOfGroups.size(); i++) {
                                    groupsMemberData[i] = "Group  - " + memberOfGroups.get(i).getId();
                                  //  msg = msg + groupsMemberData[i];
                                }
                                //Log.i("Log", msg);

                                ArrayAdapter<String> adapterMember = new ArrayAdapter<String>(getActivity(), R.layout.member, groupsMemberData);
                                ListView listMember = (ListView) getActivity().findViewById(R.id.list_member);
                                listMember.setAdapter(adapterMember);
                                }
                                catch (Exception e) {}
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

    private void responseForRemove() {
        Call<User> caller = proxy.getUserById(userId);
        ProxyBuilder.callProxy(getActivity(),caller,returnedUser -> response(returnedUser));
    }

    private void response(User returnedUser) {
        //Log.i("Log",""+returnedUser.getId());
        memberOfGroups = returnedUser.getMemberOfGroups();
        //Log.i("Log",""+returnedUser.getMemberOfGroups());

//        List<Group> groupsMember = returnedUser.getMemberOfGroups();
//        try {
//            String[] groupsMemberData = new String[groupsMember.size()];
//            for (int i = 0; i < groupsMember.size(); i++) {
//                groupsMemberData[i] = "Group  - " + groupsMember.get(i).getId();
//            }
//        } catch (Exception e) {
//
//        }
    }


}
