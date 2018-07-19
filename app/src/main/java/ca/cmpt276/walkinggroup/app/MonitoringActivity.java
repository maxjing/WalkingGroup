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
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.Session;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

/**
 * Show the list of users who they monitor.
 * If the previous activity is MainActivity, add (jump to AddUserActivity) or remove (click the list) the users whom they monitor.
 * If the previous activity is JoinGroupActivity, join the selected usesr in the selected group.
 * Be able to long click the list to view the users' groups.
 */

public class MonitoringActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_Monitoring = 01;
    public static final String GROUP_ID_MONITORING = "ca.cmpt276.walkinggroup.app - Monitoring -  GroupID";
    private User user;
    private List<User> monitorsUsers;
    private WGServerProxy proxy;
    private String token;
    private long userId;
    private long groupId;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring);

        session = Session.getInstance();
        proxy = session.getProxy();
        token = session.getToken();
        user = session.getUser();
        userId = user.getId();
//        SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref", 0);
//        token = dataToGet.getString("userToken", "");
//        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
//        user = User.getInstance();

        setAddBtn();
        registerClickCallback();
//        userId = dataToGet.getLong("userId", 0);

        populateListView();
    }

    private void setAddBtn() {
        Button btn = (Button) findViewById(R.id.btnAdd);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AddUserActivity.makeIntent(MonitoringActivity.this, "monitoring");
                startActivityForResult(intent,REQUEST_CODE_Monitoring);
            }
        });
}

    private void registerClickCallback() {
        Intent intent = getIntent();
        groupId = intent.getLongExtra(GROUP_ID_MONITORING,0);
        TextView tv = (TextView) findViewById(R.id.txtClick);
        if(groupId == 0){
            tv.setText(R.string.click_from_main);
        }else{
            tv.setText(R.string.click_for_join);
        }
        ListView list = (ListView) findViewById(R.id.listView_Monitoring);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(groupId == 0){
                    Call<Void> remove = proxy.removeFromMonitorsUsers(userId, monitorsUsers.get(position).getId());
                    ProxyBuilder.callProxy(MonitoringActivity.this, remove, returnedUser -> responseRemove());
                    Call<User> caller = proxy.getUserById(userId);
                    ProxyBuilder.callProxy(MonitoringActivity.this, caller, returnedUser -> response(returnedUser));
                }
                else{
                    Call<User> caller = proxy.getUserById(monitorsUsers.get(position).getId());
                    ProxyBuilder.callProxy(MonitoringActivity.this,caller,returnedUser -> responsejoin(returnedUser));
                    finish();
                }

            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Call<User> caller = proxy.getUserById(monitorsUsers.get(position).getId());
                ProxyBuilder.callProxy(MonitoringActivity.this,caller,returnedChild -> responseChild(returnedChild));
                return true;
            }
        });

    }

    private void responsejoin(User returnedUser) {
        Call<List<User>> caller = proxy.addGroupMember(groupId,returnedUser);
        ProxyBuilder.callProxy(MonitoringActivity.this,caller,returnedList -> responseForJoining(returnedList));
    }

    private void responseForJoining(List<User> returnedList) {
    }

    private void responseChild(User returnedChild) {
        Intent intent = ChildInfoActivity.makeChildIntent(MonitoringActivity.this,returnedChild.getId());
        startActivity(intent);
    }

    private void responseRemove() {
    }

    private void response(User user) {
        populateListView();
    }


    private void populateListView() {
        Call<List<User>> caller_list = proxy.getMonitorsUsers(userId);
        ProxyBuilder.callProxy(MonitoringActivity.this, caller_list, returnedUser -> responseForMonitoring(returnedUser));
    }

    private void responseForMonitoring(List<User> users) {
        monitorsUsers = users;
        String[] items = new String[monitorsUsers.size()];
        for (int i = 0; i < monitorsUsers.size(); i++) {
            items[i] = monitorsUsers.get(i).getName() + " - " + monitorsUsers.get(i).getEmail();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.monitoring, items);
        ListView list = (ListView) findViewById(R.id.listView_Monitoring);
        list.setAdapter(adapter);
    }


    public static Intent makeIntent(Context context,long groupId) {
        Intent intent = new Intent(context, MonitoringActivity.class);
        intent.putExtra(GROUP_ID_MONITORING,groupId);
        return intent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_Monitoring:
                if (resultCode == Activity.RESULT_OK) {
//                    SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref",0);
//                    token = dataToGet.getString("userToken","");
//                    proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
//                    user = User.getInstance();
//                    userId = dataToGet.getLong("userId", 0);
                    session = Session.getInstance();
                    proxy = session.getProxy();
                    user = session.getUser();
                    userId = user.getId();
                    //Toast.makeText(this,""+userId,Toast.LENGTH_LONG).show();
                    Log.i("LOG",""+userId);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Call<User> caller = proxy.getUserById(userId);
                    ProxyBuilder.callProxy(MonitoringActivity.this, caller, returnedUser -> response(returnedUser));
                }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateListView();
    }
}

