package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
 * Help to enable group leader transfer his leader role
 */
public class GroupMembersActivity extends AppCompatActivity {
    public static final String INFO_GROUPID = "ca.cmpt276.walkinggroup.app - GroupInfo - GroupId";
    public static final String CHILD_GROUP = "child_group";
    private WGServerProxy proxy;
    private String token;
    private long userId;
    private long groupId;
    private List<User> members;
    private Session session;
    private Group group;
    private EarnedRewards current;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_members);
        session = Session.getInstance();
        proxy = session.getProxy();
        userId = session.getUser().getId();

        SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref", 0);
        int bgNum = dataToGet.getInt("bg",0);
        changeBackGround(bgNum);

        Intent intent = getIntent();
        groupId = intent.getLongExtra(INFO_GROUPID,0);

        Call<Group> caller = proxy.getGroupById(groupId);
        ProxyBuilder.callProxy(GroupMembersActivity.this,caller, returnedGroup -> response(returnedGroup));
        setBack();

    }



    private void changeBackGround(int bgNumber){
        RelativeLayout layout = findViewById(R.id.group_members_layout);
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



    private void response(Group returnedGroup){
        group = new Group();
        group = returnedGroup;

        Call<List<User>> caller = proxy.getGroupMembers(groupId);
        ProxyBuilder.callProxy(GroupMembersActivity.this,caller, returnedList -> responseMembers(returnedList));
    }

    private void responseMembers(List<User> returnedList) {
        members = new ArrayList<>();
        members = returnedList;
        String[] items = new String[members.size()];
        for (int i = 0; i < members.size(); i++) {
            items[i] = members.get(i).getName() + " - " + members.get(i).getEmail();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.group_member_transferleader, items);
        ListView list = (ListView) findViewById(R.id.listview_member_transfer);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                group.setLeader(members.get(i));
                Call<Group> caller = proxy.updateGroup(groupId,group);
                ProxyBuilder.callProxy(GroupMembersActivity.this, caller, returned -> responseNewLeader(returned));

            }
        });
    }

    private void responseNewLeader(Group group){
        MyToast.makeText(this, getString(R.string.transfer_request_made), Toast.LENGTH_SHORT).show();
    }

    private void setBack(){
        Button btnGroup = (Button)findViewById(R.id.btnleaderChangeBack);
        btnGroup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = GroupInfoActivity.makeIntent2(GroupMembersActivity.this);
                finish();

            }
        });
    }

    public static Intent makeIntent(Context context) {
        Intent intent = new Intent(context, GroupMembersActivity.class);
        return intent;
    }





}
