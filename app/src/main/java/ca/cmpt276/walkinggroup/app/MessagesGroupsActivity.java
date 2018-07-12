package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.Message;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_groups);

        SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref",0);
        token = dataToGet.getString("userToken","");
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
        userId = dataToGet.getLong("userId",0);
        Call<User> caller_user = proxy.getUserById(userId);
        ProxyBuilder.callProxy(MessagesGroupsActivity.this, caller_user, returnedUser -> response(returnedUser));
        msg = dataToGet.getString("pMessage","");
        message = new Message();
        message.setText(msg);
        setSendAllBtn();
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
        Toast.makeText(MessagesGroupsActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();


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
