package ca.cmpt276.walkinggroup.app;

import android.app.Activity;
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
import android.widget.TextView;

import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

/**
 * View the selected group's description and members.
 * When the group is led by the user logged in, the user is able to remove members and delete the group when there is no member.
 * When the group is led by whom the user monitors, the user could only see the information.
 */

public class GroupInfoActivity extends AppCompatActivity {

    public static final String INFO_GROUPID = "ca.cmpt276.walkinggroup.app - GroupInfo - GroupId";
    public static final String CHILD_GROUP = "child_group";

    private WGServerProxy proxy;
    private String token;
    private long userId;
    private long groupId;
    private List<User> members;
    private long childId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref", 0);
        token = dataToGet.getString("userToken", "");
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);

        Intent intent = getIntent();
        groupId = intent.getLongExtra(INFO_GROUPID,0);
        childId = intent.getLongExtra(CHILD_GROUP,0);

        Button btn = (Button) findViewById(R.id.btnDelete);
        btn.setVisibility(View.GONE);

        Call<Group> caller = proxy.getGroupById(groupId);
        ProxyBuilder.callProxy(GroupInfoActivity.this,caller,returnedGroup -> response(returnedGroup));

        registerClickCallback();
        setDeleteBtn();
    }

    private void setDeleteBtn() {
        Button btn = (Button)findViewById(R.id.btnDelete);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<Void> caller = proxy.deleteGroup(groupId);
                ProxyBuilder.callProxy(GroupInfoActivity.this,caller,returned -> responseForDelete());
                setResult(Activity.RESULT_OK);
            }
        });
    }

    private void responseForDelete() {
        finish();
    }

    private void registerClickCallback() {
        if(childId == 0){
            ListView list = (ListView) findViewById(R.id.list_group_member);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Call<Void> caller = proxy.removeGroupMember(groupId,members.get(i).getId());
                    ProxyBuilder.callProxy(GroupInfoActivity.this,caller,returned -> responseForRemove());
                }
            });

            list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    Call<User> caller = proxy.getUserById(members.get(position).getId());
                    ProxyBuilder.callProxy(GroupInfoActivity.this,caller,returnedUser -> responseForMonitor(returnedUser));
                    return true;
                }
            });
        }
    }

    private void responseForMonitor(User returnedUser) {
        Intent intent = MonitorActivity.makeMemberIntent(GroupInfoActivity.this,returnedUser.getId());
        startActivity(intent);
    }

    private void responseForRemove() {
        Call<List<User>> caller = proxy.getGroupMembers(groupId);
        ProxyBuilder.callProxy(GroupInfoActivity.this,caller,returnedList -> responseMembers(returnedList));
    }

    private void response(Group returnedGroup) {
        TextView tv = (TextView) findViewById(R.id.txtDesc);
        tv.setText(returnedGroup.getGroupDescription());
        Call<List<User>> caller = proxy.getGroupMembers(groupId);
        ProxyBuilder.callProxy(GroupInfoActivity.this,caller,returnedList -> responseMembers(returnedList));
    }

    private void responseMembers(List<User> returnedList) {
        members = returnedList;
        String[] items = new String[members.size()];
        for (int i = 0; i < members.size(); i++) {
            items[i] = members.get(i).getName() + " - " + members.get(i).getEmail();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.group_member, items);
        ListView list = (ListView) findViewById(R.id.list_group_member);
        list.setAdapter(adapter);
        Button btn = (Button) findViewById(R.id.btnDelete);
        if (members.size() == 0 && childId == 0) {
            btn.setVisibility(View.VISIBLE);
        }
    }

    public static Intent makeIntent(Context context, long groupId,long childId) {
        Intent intent = new Intent(context, GroupInfoActivity.class);
        intent.putExtra(INFO_GROUPID,groupId);
        intent.putExtra(CHILD_GROUP,childId);
        return intent;
    }
}
