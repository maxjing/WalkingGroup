package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

/**
 * Ask the user who wants to join the selected walking group.
 */

public class JoinGroupActivity extends AppCompatActivity {

    public static final String GROUPID = "GROUPID";
    private long groupId = 0;
    private WGServerProxy proxy;
    private String token;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);

        Intent intent = getIntent();
        groupId = intent.getLongExtra(GROUPID,0);

        SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref",0);
        token = dataToGet.getString("userToken","");
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
        userId = dataToGet.getLong("userId",0);

        setMyselfBtn();
        setMyChildBtn();
    }

    private void setMyselfBtn() {
        Button btn = (Button) findViewById(R.id.btnMyself);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<User> caller = proxy.getUserById(userId);
                ProxyBuilder.callProxy(JoinGroupActivity.this, caller, returnedUser -> response(returnedUser));
                finish();
            }
        });
    }

    private void response(User returnedUser) {
        Call<List<User>>  caller = proxy.addGroupMember(groupId,returnedUser);
        ProxyBuilder.callProxy(JoinGroupActivity.this, caller, returnedList -> responseForJoining(returnedList));
    }

    private void responseForJoining(List<User> returnedList) {
       // finish();
    }

    private void setMyChildBtn() {
        Button btn = (Button) findViewById(R.id.btnMyChild);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToMonitoring = MonitoringActivity.makeIntent(JoinGroupActivity.this,groupId);
                startActivity(intentToMonitoring);
                finish();
            }
        });
    }

    public static Intent makeIntent(Context context, long selectedID) {
        Intent intent = new Intent(context,JoinGroupActivity.class);
        intent.putExtra(GROUPID,selectedID);
        return intent;
    }
}
