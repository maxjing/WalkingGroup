package ca.cmpt276.walkinggroup.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.app.DialogFragment.MyToast;
import ca.cmpt276.walkinggroup.dataobjects.EarnedRewards;
import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.Session;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

/**
 * List the groups the user joined as a leader or a member.
 * Be able to click the list of groups to view group information if the user is a leader.
 * Be able to click the list of groups to leave the group if the user is a member.
 */

public class GroupActivity extends AppCompatActivity {
    public static final String GROUP_REMOVE = "ca.cmpt276.walkinggroup.app - Group - GroupID";
    public static final String USER_REMOVE = "ca.cmpt276.walkinggroup.app - Group - UserId";
    public static final String POSITION = "POSITION";
    public static final int REQUEST_CODE_DELETE = 07;
    public static final int REQUEST_SEND_GROUPMSGS = 33;
    private String token;
    private String TAG = "GroupActivity";
    private WGServerProxy proxy;
    private User user;
    private List<Group> groupsMember;
    private List<Group> groupsLeader;
    private List<String> groupsLeaderDes = new ArrayList<>();
    private List<Long> leadID = new ArrayList<>();
    private List<Long> memberID = new ArrayList<>();
    private List<String> groupsMemberDes = new ArrayList<>();

    private Long userId;
    private Long childId;
    private boolean isSendMsg;
    private Session session;
    private EarnedRewards current;

    public static final String CHILD_ID = "ca.cmpt276.walkinggroup.app - GroupActivity.class";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        session = Session.getInstance();
        proxy = session.getProxy();
        user = session.getUser();
        userId = user.getId();

        backGround();

        Intent intent = getIntent();
        childId = intent.getLongExtra(CHILD_ID,0);

        if(childId != 0){
            userId = childId;
        }
        Call<User> caller = proxy.getUserById(userId);
        ProxyBuilder.callProxy(GroupActivity.this, caller, returnedUser -> responseL(returnedUser));
        Call<User> caller_m = proxy.getUserById(userId);
        ProxyBuilder.callProxy(GroupActivity.this, caller_m, returnedUser -> responseM(returnedUser));

        registerClickCallback_Leader();
        registerClickCallback_Member();

    }

    private void backGround() {
        Call<User> caller = proxy.getUserById(userId);
        ProxyBuilder.callProxy(GroupActivity.this,caller, returned -> responseForGet(returned));
    }

    private void responseForGet(User returned) {
        Gson gson = new Gson();
        String json = returned.getCustomJson();
        current = gson.fromJson(json, EarnedRewards.class);
        changeBackGround();
    }

    private void changeBackGround(){
        ConstraintLayout layout = findViewById(R.id.group_layout);
        if(current.getSelectedBackground() == 0){
            layout.setBackground(getResources().getDrawable(R.drawable.background0));
        }
        if(current.getSelectedBackground() == 1){
            layout.setBackground(getResources().getDrawable(R.drawable.background1));
        }
        if(current.getSelectedBackground() == 2){
            layout.setBackground(getResources().getDrawable(R.drawable.background2));
        }
        if(current.getSelectedBackground() == 3){
            layout.setBackground(getResources().getDrawable(R.drawable.background3));
        }
        if(current.getSelectedBackground() == 4){
            layout.setBackground(getResources().getDrawable(R.drawable.background4));
        }
        if(current.getSelectedBackground() == 5){
            layout.setBackground(getResources().getDrawable(R.drawable.background5));
        }
        if(current.getSelectedBackground() == 6){
            layout.setBackground(getResources().getDrawable(R.drawable.background6));
        }

    }

    private void responseL(User user) {
        groupsLeader = user.getLeadsGroups();
        try {
            groupsLeaderDes.clear();
            leadID.clear();
            if(groupsLeader.size() > 0){
                for(int i = 0; i < groupsLeader.size(); i++){
                    Call<Group> caller = proxy.getGroupById(groupsLeader.get(i).getId());
                    ProxyBuilder.callProxy(GroupActivity.this, caller, returnedGroup -> responseGroup_leader(returnedGroup));
                }
            }else{
                populateLeader();
            }
        } catch (Exception e) {

        }


    }

    private void responseGroup_leader(Group returnedGroup) {
        leadID.add(returnedGroup.getId());
        groupsLeaderDes.add(getString(R.string.Group_)+" " + returnedGroup.getGroupDescription());
        if(leadID.size() == groupsLeader.size()){
            populateLeader();
        }
    }

    private void populateLeader() {
        ArrayAdapter<String> adapterLeader = new ArrayAdapter<>(this, R.layout.lead, groupsLeaderDes);
        ListView listLeader = (ListView) findViewById(R.id.list_leader);
        listLeader.setAdapter(adapterLeader);
    }

    private void registerClickCallback_Leader() {
        ListView list = (ListView) findViewById(R.id.list_leader);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = GroupInfoActivity.makeIntent(GroupActivity.this,leadID.get(position),childId);
                startActivityForResult(intent, REQUEST_CODE_DELETE);
            }
        });
    }

    private void responseM(User user) {
        groupsMember = user.getMemberOfGroups();
        try {
            groupsMemberDes.clear();
            memberID.clear();
            if(groupsMember.size() > 0){
                for(int i = 0; i < groupsMember.size(); i++){
                    Call<Group> caller = proxy.getGroupById(groupsMember.get(i).getId());
                    ProxyBuilder.callProxy(GroupActivity.this, caller, returnedGroup -> responseGroup_member(returnedGroup));
                }
            }else {
                populateMember();
            }
        } catch (Exception e) {

        }
    }

    private void responseGroup_member(Group returnedGroup) {
        memberID.add(returnedGroup.getId());
        groupsMemberDes.add(getString(R.string.Group_)+" " + returnedGroup.getGroupDescription());
        if(memberID.size() == groupsMember.size()){
            populateMember();
        }
    }

    private void populateMember() {
        ArrayAdapter<String> adapterMember = new ArrayAdapter<>(this, R.layout.member, groupsMemberDes);
        ListView listLeader = (ListView) findViewById(R.id.list_member);
        listLeader.setAdapter(adapterMember);
    }

    private void registerClickCallback_Member() {
        ListView list = (ListView) findViewById(R.id.list_member);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = GroupInfoActivity.makeIntent(GroupActivity.this,memberID.get(position),userId);
                startActivity(intent);
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Call<Void> caller = proxy.removeGroupMember(memberID.get(position), userId);
                ProxyBuilder.callProxy(GroupActivity.this, caller, returned -> responseForRemove());
                MyToast.makeText(GroupActivity.this, R.string.leave_success, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void responseForRemove() {
        Call<User> caller_m = proxy.getUserById(userId);
        ProxyBuilder.callProxy(GroupActivity.this, caller_m, returnedUser -> responseM(returnedUser));
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
                    session = Session.getInstance();
                    proxy = session.getProxy();
                    user = session.getUser();
                    userId = user.getId();
                    Call<User> caller = proxy.getUserById(userId);
                    ProxyBuilder.callProxy(GroupActivity.this, caller, returnedUser -> responseL(returnedUser));
                }
        }
    }

}
