package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.icu.text.UnicodeSetSpanner;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.GroupInfo;
import ca.cmpt276.walkinggroup.dataobjects.Session;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class GroupMemmbersActivity extends AppCompatActivity {
    public static final String INFO_GROUPID = "ca.cmpt276.walkinggroup.app - GroupInfo - GroupId";
    public static final String CHILD_GROUP = "child_group";
    private WGServerProxy proxy;
    private String token;
    private long userId;
    private long groupId;
    private List<User> members;
    private Session session;
    private Group group;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_memmbers);
        session = Session.getInstance();
        proxy = session.getProxy();
        userId = session.getUser().getId();

        Intent intent = getIntent();
        groupId = intent.getLongExtra(INFO_GROUPID,0);

        Call<Group> caller = proxy.getGroupById(groupId);
        ProxyBuilder.callProxy(GroupMemmbersActivity.this,caller, returnedGroup -> response(returnedGroup));
        setBack();

    }
    private void response(Group returnedGroup){
        group = new Group();
        group = returnedGroup;

        Call<List<User>> caller = proxy.getGroupMembers(groupId);
        ProxyBuilder.callProxy(GroupMemmbersActivity.this,caller,returnedList -> responseMembers(returnedList));
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
                ProxyBuilder.callProxy(GroupMemmbersActivity.this, caller, returned -> responseNewLeader(returned));

            }
        });
    }

    private void responseNewLeader(Group group){
        Toast.makeText(this, "Transfer Request Made", Toast.LENGTH_SHORT).show();
    }

    private void setBack(){
        Button btnGroup = (Button)findViewById(R.id.btnleaderChangeBack);
        btnGroup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = GroupInfoActivity.makeIntent2(GroupMemmbersActivity.this);
                finish();

            }
        });
    }

    public static Intent makeIntent(Context context) {
        Intent intent = new Intent(context, GroupMemmbersActivity.class);
        return intent;
    }





}
