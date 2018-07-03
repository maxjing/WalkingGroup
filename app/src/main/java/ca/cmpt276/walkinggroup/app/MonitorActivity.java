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
import android.widget.Toast;

import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class MonitorActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_Monitored = 02;
    private User user;
    private List<User> monitoredByUsers;
    private WGServerProxy proxy;
    private String token;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref",0);
        token = dataToGet.getString("userToken","");
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
        user = User.getInstance();
//        Call<User> caller = proxy.getUserByEmail(user.getEmail());
//        ProxyBuilder.callProxy(MonitorActivity.this, caller, returnedUser -> response(returnedUser));
        setAddBtn();
        registerClickCallback();
        Toast.makeText(this, "from new"+dataToGet.getLong("userId",0), Toast.LENGTH_SHORT).show();
        userId = dataToGet.getLong("userId", 0);
        populateListView();
    }

    private void setAddBtn() {
        Button btn = (Button) findViewById(R.id.btnAdd_ed);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AddUserActivity.makeIntent(MonitorActivity.this,"monitored");
                startActivityForResult(intent, REQUEST_CODE_Monitored);
            }
        });
    }

    private void registerClickCallback() {
        ListView list = (ListView) findViewById(R.id.listView_Monitored);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Call<Void> removeMonitor = proxy.removeFromMonitorsUsers(monitoredByUsers.get(position).getId(),userId);
                ProxyBuilder.callProxy(MonitorActivity.this, removeMonitor, returnedUser -> responseRemove());
                Call<User> caller = proxy.getUserByEmail(user.getEmail());
                ProxyBuilder.callProxy(MonitorActivity.this, caller, returnedUser -> response(returnedUser));
            }
        });
    }

    private void responseRemove() {
    }

    private void response(User user) {

        populateListView();
    }

    private void populateListView() {
        Call<List<User>> caller_list = proxy.getMonitoredByUsers(userId);
        ProxyBuilder.callProxy(MonitorActivity.this, caller_list, returnedUser -> responseForMonitored(returnedUser));
    }

    private void responseForMonitored(List<User> users) {
        monitoredByUsers = users;
        String[] items = new String[monitoredByUsers.size()];
        for (int i = 0; i < monitoredByUsers.size(); i++) {
            items[i] = monitoredByUsers.get(i).getName() + " - " + monitoredByUsers.get(i).getEmail();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.monitored,items);
        ListView list = (ListView) findViewById(R.id.listView_Monitored);
        list.setAdapter(adapter);
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context,MonitorActivity.class);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_Monitored:
                if (resultCode == Activity.RESULT_OK) {
                    SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref",0);
                    token = dataToGet.getString("userToken","");
                    proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
                    user = User.getInstance();
                    Call<User> caller = proxy.getUserByEmail(user.getEmail());
                    ProxyBuilder.callProxy(MonitorActivity.this, caller, returnedUser -> response(returnedUser));
                }
        }
    }
}
