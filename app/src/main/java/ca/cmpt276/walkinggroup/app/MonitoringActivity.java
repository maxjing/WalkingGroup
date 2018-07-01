package ca.cmpt276.walkinggroup.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class MonitoringActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_MONITORINGNEW = 01;

    private User user;
    private List<User> monitorsUsers;
    private WGServerProxy proxy;
    private String token;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring);

        SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref",0);
        token = dataToGet.getString("userToken","");
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
        user = User.getInstance();
        Call<User> caller = proxy.getUserByEmail(user.getEmail());
        ProxyBuilder.callProxy(MonitoringActivity.this, caller, returnedUser -> response(returnedUser));
//        Toast.makeText(this,Long.toString(userId),Toast.LENGTH_SHORT).show();
//        userId = user.getId();
//        Log.i("Log",user.toString());

        setAddBtn();
    }

    private void response(User user) {
        userId = user.getId();
        populateListView();
    }

    private void setAddBtn() {
        Button btn = (Button) findViewById(R.id.btnAdd);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AddUserActivity.makeIntent(MonitoringActivity.this);
                startActivityForResult(intent, REQUEST_CODE_MONITORINGNEW);
            }
        });
    }

    private void populateListView() {
        Call<List<User>> caller = proxy.getMonitorsUsers(userId);
        ProxyBuilder.callProxy(MonitoringActivity.this, caller,returnedUser -> responseForMonitoring(returnedUser));
//        String[] items = new String[user.getMonitorsUsers().size()];
//        for (int i = 0; i < user.getMonitorsUsers().size(); i++) {
//            items[i] = user.getMonitorsUsers().get(i).getName() + " " + user.getMonitorsUsers().get(i).getEmail();
//        }
//        monitorsUsers = user.getMonitorsUsers();
//        ArrayAdapter<User> adapter = new ArrayAdapter<User>(this,R.layout.monitoring,monitorsUsers);
//
////        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.monitoring,items);
//        ListView list = (ListView) findViewById(R.id.listView_Monitoring);
//        list.setAdapter(adapter);
    }

    private void responseForMonitoring(List<User> users) {
//        String[] items = new String[user.size()];
//        for (int i = 0; i < user.size(); i++) {
//            items[i] = user.get(i).getName() + " " + user.get(i).getEmail();
//        }
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.monitoring,items);
        monitorsUsers = users;
        String[] items = new String[monitorsUsers.size()];
        for (int i = 0; i < monitorsUsers.size(); i++) {
            items[i] = monitorsUsers.get(i).getName() + " " + monitorsUsers.get(i).getEmail();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.monitoring,items);
//        ArrayAdapter<User> adapter = new ArrayAdapter<User>(this,R.layout.monitoring,monitorsUsers);
        ListView list = (ListView) findViewById(R.id.listView_Monitoring);
        list.setAdapter(adapter);
    }


    public static Intent makeIntent(Context context) {
        return new Intent(context, MonitoringActivity.class);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_MONITORINGNEW:
                if (resultCode == Activity.RESULT_OK) {
                    Call<User> caller = proxy.getUserByEmail(user.getEmail());
                    ProxyBuilder.callProxy(MonitoringActivity.this, caller, returnedUser -> response(returnedUser));
                }
        }
    }
}
//        switch(requestCode){
//            case REQUEST_CODE_EDIT:
//                if(resultCode == Activity.RESULT_OK) {
//                    Pot edited = AddPot.getPotFromIntent(data);
//                    potList.changePot(edited,index);
//                    populateListView();
//                }else if(resultCode == Activity.RESULT_FIRST_USER){
//                    int position = index;
//                    while(position < potList.countPots()-1){
//                        potList.changePot(potList.getPot(position + 1),position);
//                        position++;
//                    }
//                    potList.removePot(potList.countPots()-1);
//                    populateListView();
//                }
//        }
//    }

//}
