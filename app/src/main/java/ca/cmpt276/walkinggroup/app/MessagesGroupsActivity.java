package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.app.DialogFragment.MyToast;
import ca.cmpt276.walkinggroup.dataobjects.EarnedRewards;
import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.Message;
import ca.cmpt276.walkinggroup.dataobjects.Session;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

/**
 * able to send to all the group or single group
 * able to send to member's parents
 */

public class MessagesGroupsActivity extends AppCompatActivity {

    private WGServerProxy proxy;
    private Long userId;
    private String token;
    private User user;
    private List<Group> groupsLeader;
    private List<String> groupsLeaderDes;
    private List<Long> leadID;
    private String msg;
    private Message message;
    private String TAG = "MessagesGroupsActivity";
    private Session session;
    private EarnedRewards current;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_groups);

        SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref",0);

        session = Session.getInstance();
        proxy = session.getProxy();
        user = session.getUser();
        userId = user.getId();

        int bgNum = dataToGet.getInt("bg",0);
        changeBackGround(bgNum);



        Call<User> caller_user = proxy.getUserById(userId);
        ProxyBuilder.callProxy(MessagesGroupsActivity.this, caller_user, returnedUser -> response(returnedUser));
        msg = dataToGet.getString("pMessage","");
        message = new Message();
        message.setText(msg);
        setSendAllBtn();
    }



    private void changeBackGround(int bgNumber){
        ConstraintLayout layout = findViewById(R.id.messages_groups_layout);
//        MyToast.makeText(this,""+current.getSelectedBackground(),Toast.LENGTH_SHORT).show();
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


    private void response(User returnedUser) {
        user = returnedUser;
        groupsLeader = user.getLeadsGroups();
        groupsLeaderDes = new ArrayList<>();
        leadID = new ArrayList<>();

        try {

            for(int i = 0; i < groupsLeader.size(); i++){
                Call<Group> caller = proxy.getGroupById(groupsLeader.get(i).getId());
                ProxyBuilder.callProxy(MessagesGroupsActivity.this, caller, returnedGroup -> responseGroup_leader(returnedGroup));

            }

        } catch (Exception e) {

        }

    }

    private void responseGroup_leader(Group returnedGroup) {
        groupsLeaderDes.add(returnedGroup.getGroupDescription());
        leadID.add(returnedGroup.getId());
        populateLeader();
    }


    private void populateLeader() {
        ArrayAdapter<String> adapterLeader = new ArrayAdapter<>(this, R.layout.lead, groupsLeaderDes);
        ListView listLeader = (ListView) findViewById(R.id.listView_LeadersGroup);
        listLeader.setAdapter(adapterLeader);

        listLeader.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Call<List<Message>> caller = proxy.newMessageToGroup(leadID.get(position),message);
                ProxyBuilder.callProxy(MessagesGroupsActivity.this, caller, returnedMsg -> response(returnedMsg));

            }
        });


    }
    private void response(List<Message> returnedMsg) {
        MyToast.makeText(MessagesGroupsActivity.this, getString(R.string.message_sent), Toast.LENGTH_SHORT).show();


    }




    private void setSendAllBtn() {
        Button btn = (Button) findViewById(R.id.btnSendAll);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<leadID.size();i++){
                    Call<List<Message>> caller = proxy.newMessageToGroup(leadID.get(i),message);
                    ProxyBuilder.callProxy(MessagesGroupsActivity.this, caller, returnedMsg -> response(returnedMsg));

                }

            }
        });
    }




    public static Intent makeIntent(Context context){
        return new Intent(context, MessagesGroupsActivity.class);
    }
}
