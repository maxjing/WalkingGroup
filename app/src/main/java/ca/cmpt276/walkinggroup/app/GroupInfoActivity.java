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

public class GroupInfoActivity extends AppCompatActivity {

    public static final String INFO_GROUPID = "ca.cmpt276.walkinggroup.app - GroupInfo - GroupId";

    private WGServerProxy proxy;
    private String token;
    private long userId;
    private long groupId;
    private List<User> members;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref", 0);
        token = dataToGet.getString("userToken", "");
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);

        Intent intent = getIntent();
        groupId = intent.getLongExtra(INFO_GROUPID,0);

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
        ListView list = (ListView) findViewById(R.id.list_group_member);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Call<Void> caller = proxy.removeGroupMember(groupId,members.get(i).getId());
                ProxyBuilder.callProxy(GroupInfoActivity.this,caller,returned -> responseForRemove());
            }
        });
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
        if (members.size() == 0) {
            btn.setVisibility(View.VISIBLE);
        }
    }

    public static Intent makeIntent(Context context, long groupId) {
        Intent intent = new Intent(context, GroupInfoActivity.class);
        intent.putExtra(INFO_GROUPID,groupId);
        return intent;
//        return new Intent(context,GroupInfoActivity.class);
    }
}