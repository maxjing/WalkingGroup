package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import ca.cmpt276.walkinggroup.app.DialogFragment.MyToast;
import ca.cmpt276.walkinggroup.dataobjects.EarnedRewards;
import ca.cmpt276.walkinggroup.dataobjects.Session;
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
    private User user;
    private Session session;
    private EarnedRewards current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);

        Intent intent = getIntent();
        groupId = intent.getLongExtra(GROUPID,0);

        session = Session.getInstance();
        proxy = session.getProxy();
        user = session.getUser();
        userId = user.getId();

        SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref", 0);
        int bgNum = dataToGet.getInt("bg",0);
        changeBackGround(bgNum);

        setMyselfBtn();
        setMyChildBtn();
    }


    private void changeBackGround(int bgNumber){
        ConstraintLayout layout = findViewById(R.id.join_group_layout);
        if(bgNumber == 0){
            layout.setBackground(getResources().getDrawable(R.drawable.background0));
        }
        if(bgNumber == 1){
            layout.setBackground(getResources().getDrawable(R.drawable.background1));
        }
        if(bgNumber == 2){
            layout.setBackground(getResources().getDrawable(R.drawable.background2));
        }
        if(bgNumber == 3){
            layout.setBackground(getResources().getDrawable(R.drawable.background3));
        }
        if(bgNumber == 4){
            layout.setBackground(getResources().getDrawable(R.drawable.background4));
        }
        if(bgNumber == 5){
            layout.setBackground(getResources().getDrawable(R.drawable.background5));
        }
        if(bgNumber == 6){
            layout.setBackground(getResources().getDrawable(R.drawable.background6));
        }

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
        MyToast.makeText(JoinGroupActivity.this, R.string.requestSend, Toast.LENGTH_LONG).show();
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
