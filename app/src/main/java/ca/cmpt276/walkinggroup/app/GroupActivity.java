package ca.cmpt276.walkinggroup.app;

import android.app.Activity;
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

import ca.cmpt276.walkinggroup.dataobjects.EarnedRewards;
import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class GroupActivity extends AppCompatActivity {
    public static final String GROUP_REMOVE = "ca.cmpt276.walkinggroup.app - Group - GroupID";
    public static final String USER_REMOVE = "ca.cmpt276.walkinggroup.app - Group - UserId";
    public static final String POSITION = "POSITION";
    public static final int REQUEST_CODE_DELETE = 07;
    private String token;
    private String TAG = "GroupActivity";
    private WGServerProxy proxy;
    private User user;
    private List<Group> groupsMember;
    private List<Group> groupsLeader;

    private Long userId;
    private Long childId;

    public static final String CHILD_ID = "ca.cmpt276.walkinggroup.app - GroupActivity.class";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref",0);
        token = dataToGet.getString("userToken","");
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
        userId = dataToGet.getLong("userId",0);

        Intent intent = getIntent();
        childId = intent.getLongExtra(CHILD_ID,0);

        if(childId != 0){
            userId = childId;
        }
        Call<User> caller = proxy.getUserById(userId);
        ProxyBuilder.callProxy(GroupActivity.this, caller, returnedUser -> response(returnedUser));
        groupsMember = new ArrayList<>();
        groupsLeader = new ArrayList<>();


        registerClickCallback_Leader();
        registerClickCallback_Member();


    }

    private void registerClickCallback_Member() {
        ListView list = (ListView) findViewById(R.id.list_member);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle args = new Bundle();

                args.putLong(GROUP_REMOVE, groupsMember.get(position).getId());
                args.putLong(USER_REMOVE, userId);
                args.putInt(POSITION,position);
               // args.putStringArray(GROUPS_MEMBER,groupsMemberData);

                android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
                RemoveMessage dialog = new RemoveMessage();
                dialog.setArguments(args);
                dialog.show(manager, "MessageDialog");
            }
        });

    }


    private void response(User user) {
        groupsMember = user.getMemberOfGroups();
        groupsLeader = user.getLeadsGroups();
        try {
            String[] groupsLeaderData = new String[groupsLeader.size()];
            for (int i = 0; i < groupsLeader.size(); i++) {
                groupsLeaderData[i] = "Group  - " + groupsLeader.get(i).getId();
            }
            ArrayAdapter<String> adapterLead = new ArrayAdapter<String>(this, R.layout.lead, groupsLeaderData);
            ListView listLead = (ListView) findViewById(R.id.list_leader);
            listLead.setAdapter(adapterLead);

        }
            catch(Exception e){

            }

        try{

            String[] groupsMemberData = new String[groupsMember.size()];
            for (int i = 0; i < groupsMember.size(); i++) {
                groupsMemberData[i] = "Group  - " + groupsMember.get(i).getId();
            }



            ArrayAdapter<String> adapterMember = new ArrayAdapter<String>(this, R.layout.member, groupsMemberData);
            ListView listMember = (ListView) findViewById(R.id.list_member);
            listMember.setAdapter(adapterMember);

        }catch(Exception e){

        }


    }

    private void registerClickCallback_Leader() {
        ListView list = (ListView) findViewById(R.id.list_leader);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = GroupInfoActivity.makeIntent(GroupActivity.this,groupsLeader.get(position).getId());
                startActivityForResult(intent, REQUEST_CODE_DELETE);
            }
        });
    }



    public static Intent makeIntent(Context context){
        return new Intent(context, GroupActivity.class);
    }

    public static Intent makeChildIntent(Context context,long id){
        Intent intent = new Intent(context,GroupActivity.class);
        intent.putExtra(CHILD_ID,id);
        return intent;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_DELETE:
                if (resultCode == Activity.RESULT_OK) {
                    SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref",0);
                    token = dataToGet.getString("userToken","");
                    proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
                    user = User.getInstance();
                    Call<User> caller = proxy.getUserByEmail(user.getEmail());
                    ProxyBuilder.callProxy(GroupActivity.this, caller, returnedUser -> response(returnedUser));
                }
        }
    }

}
