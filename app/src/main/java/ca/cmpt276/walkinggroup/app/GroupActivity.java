package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import ca.cmpt276.walkinggroup.dataobjects.EarnedRewards;
import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class GroupActivity extends AppCompatActivity {
    private String token;
    private String TAG = "GroupActivity";
    private WGServerProxy proxy;
    private String userEmail;
    private User user;
    private Group group;
    private String groupLeader;
    private long groupId = 29;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref",0);
        token = dataToGet.getString("userToken","");
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
        setupNewGroupButton();
    }

    private void setupNewGroupButton() {
        Button btn = findViewById(R.id.btnNewGroup);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Build new user (with random email to avoid conflicts)
                group = new Group();
                int random = (int) (Math.random() * 100000);
                group.setLeader("sdjhakjsdhkashdkasdkjahdkjashdkjabcskja");

                // Make call
                Call<Group> caller = proxy.updateGroup(groupId,group);
                ProxyBuilder.callProxy(GroupActivity.this, caller, returnedGroup -> response(returnedGroup));
            }
        });
    }
    private void response(Group group) {
        groupId = group.getId();

        groupLeader = group.getLeader();
        Toast.makeText(GroupActivity.this, ""+group.toString(), Toast.LENGTH_SHORT).show();

    }

    public static Intent makeIntent(Context context){
        return new Intent(context, GroupActivity.class);
    }
}
