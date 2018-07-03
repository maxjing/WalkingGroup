package ca.cmpt276.walkinggroup.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class GroupCreateActivity extends AppCompatActivity {
    private String token;
    private String TAG = "GroupCreateActivity";
    private WGServerProxy proxy;
    private String editDestination;
    private String editDesctiption;
    private String editMeetPlace;
    private User user;
    private long userId;
    private Group group;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);
        SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref",0);
        token = dataToGet.getString("userToken","");
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
        setOKBtn();
    }

    private void setOKBtn() {

        Button btn = (Button) findViewById(R.id.btnOK);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Extract data from UI
                EditText editDest = (EditText) findViewById(R.id.editDestination);
                editDestination = editDest.getText().toString();
                EditText editDesc= (EditText) findViewById(R.id.editDescription);
                editDesctiption = editDesc.getText().toString();
                EditText editMeet= (EditText) findViewById(R.id.editMeetPlace);
                editMeetPlace = editMeet.getText().toString();

                user = User.getInstance();
                Call<User> caller = proxy.getUserByEmail(user.getEmail());
                ProxyBuilder.callProxy(GroupCreateActivity.this, caller, returnedUser -> response(returnedUser));

            }
        });
    }

    private void response(User user) {

        group = new Group();
        group.setLeader(user);
        group.setGroupDescription(editDestination);
        Call<Group> caller = proxy.createGroup(group);
        ProxyBuilder.callProxy(GroupCreateActivity.this, caller, returnedGroup -> response(returnedGroup));
    }

    private void response(Group group) {

        Toast.makeText(GroupCreateActivity.this, ""+group.toString(), Toast.LENGTH_SHORT).show();

    }

    public static Intent makeIntent(Context context){
        return new Intent(context, GroupCreateActivity.class);
    }
}
