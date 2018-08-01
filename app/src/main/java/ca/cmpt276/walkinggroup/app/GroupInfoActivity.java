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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

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
    private Session session;
    private EarnedRewards current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        session = Session.getInstance();
        proxy = session.getProxy();
        userId = session.getUser().getId();

        backGround();

        Intent intent = getIntent();
        groupId = intent.getLongExtra(INFO_GROUPID,0);
        childId = intent.getLongExtra(CHILD_GROUP,0);

        Button btn = (Button) findViewById(R.id.btnDelete);
        btn.setVisibility(View.GONE);

        Call<Group> caller = proxy.getGroupById(groupId);
        ProxyBuilder.callProxy(GroupInfoActivity.this,caller,returnedGroup -> response(returnedGroup));

        registerClickCallback();
        setDeleteBtn();
        setTransferBtn();
    }

    private void backGround() {
        Call<User> caller = proxy.getUserById(userId);
        ProxyBuilder.callProxy(GroupInfoActivity.this,caller,returned -> responseForGet(returned));
    }

    private void responseForGet(User returned) {
        Gson gson = new Gson();
        String json = returned.getCustomJson();
        current = gson.fromJson(json, EarnedRewards.class);
        changeBackGround();
    }

    private void changeBackGround(){
        ConstraintLayout layout = findViewById(R.id.group_info_layout);
        MyToast.makeText(this,""+current.getSelectedBackground(), Toast.LENGTH_SHORT).show();
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
        ListView list = (ListView) findViewById(R.id.list_group_member);
        if(childId == 0){
            TextView tv = (TextView) findViewById(R.id.txtInstruction);
            tv.setText(R.string.click_member_infoActivity);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Call<Void> caller = proxy.removeGroupMember(groupId,members.get(i).getId());
                    ProxyBuilder.callProxy(GroupInfoActivity.this,caller,returned -> responseForRemove());
                }
            });
        }
        TextView tv_long = (TextView) findViewById(R.id.txt_Long_Inst);
        tv_long.setText(R.string.long_click_to_view_users_parents);
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Call<User> caller = proxy.getUserById(members.get(position).getId());
                ProxyBuilder.callProxy(GroupInfoActivity.this,caller,returnedUser -> responseForMonitor(returnedUser));
                return true;
            }
        });
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

    private void setTransferBtn() {
        Button btn = (Button)findViewById(R.id.btntransfer);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = GroupMemmbersActivity.makeIntent(GroupInfoActivity.this);
                intent.putExtra(INFO_GROUPID,groupId);
                startActivity(intent);
            }
        });
    }


    public static Intent makeIntent(Context context, long groupId,long childId) {
        Intent intent = new Intent(context, GroupInfoActivity.class);
        intent.putExtra(INFO_GROUPID,groupId);
        intent.putExtra(CHILD_GROUP,childId);
        return intent;
    }
    public static Intent makeIntent2(Context context) {
        Intent intent = new Intent(context, GroupInfoActivity.class);
        return intent;
    }
}
