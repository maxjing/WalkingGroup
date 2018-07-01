package ca.cmpt276.walkinggroup.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.IdItemBase;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Path;

public class AddUserActivity extends AppCompatActivity {

    private static final String EXTRA_FLAG = "ca.cmpt276.walkinggroup.app.AddUserActivity - FLAG";
    private User user;
    private List<User> monitorsUsers;
    private WGServerProxy proxy;
    String editName;
    private long userId;
    private long tem_id;
    private String token;
    List<User> userstomonitor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref",0);
        token = dataToGet.getString("userToken","");

        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
        user = User.getInstance();
        Call<User> caller = proxy.getUserByEmail(user.getEmail());
        ProxyBuilder.callProxy(AddUserActivity.this, caller, returnedUser -> response(returnedUser));
//        Toast.makeText(this,Long.toString(userId),Toast.LENGTH_LONG).show();
       // Log.i("Log",id.toString());

        setCancelBtn();
        setOKBtn();
    }

    private void setCancelBtn() {
        Button btn = (Button) findViewById(R.id.btnCancel);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setOKBtn() {

        Button btn = (Button) findViewById(R.id.btnOK);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Extract data from UI
                EditText editN = (EditText) findViewById(R.id.editName);
                editName = editN.getText().toString();
                EditText editE = (EditText) findViewById(R.id.editEmail);
                String editEmail = editE.getText().toString();
                Call<User> caller = proxy.getUserByEmail(editEmail);

                Intent intent = getIntent();
                String flag = intent.getStringExtra(EXTRA_FLAG);
                if(flag.equals("monitoring")){
                    ProxyBuilder.callProxy(AddUserActivity.this, caller, returnedUser -> responseForAddMonitoring(returnedUser));
                }
                if(flag.equals("monitored")){
                    ProxyBuilder.callProxy(AddUserActivity.this, caller, returnedUser -> responseForAddMonitored(returnedUser));
                }
//                Call<User> caller_ed = proxy.getUserById(userId);
               // Toast.makeText(AddUserActivity.this,Long.toString(userId),Toast.LENGTH_LONG).show();
                setResult(Activity.RESULT_OK);
                finish();
            }
        });
    }

    private void responseForAddMonitored(User parent) {
//        user.setId(userId);
//        Call<List<User>> caller = proxy.addToMonitorsUsers(parent.getId(), user);
//        ProxyBuilder.callProxy(AddUserActivity.this, caller, returnedUser -> response(returnedUser));
        Call<List<User>> caller_ed = proxy.addToMonitoredByUsers(userId, parent);
        ProxyBuilder.callProxy(AddUserActivity.this, caller_ed, returnedUser -> response(returnedUser));
    }

    private void response(User user) {
        userId = user.getId();
//        this.user.setId(userId);
    }

    private void responseForAddMonitoring(User child) {
        Call<List<User>> caller = proxy.addToMonitorsUsers(userId, child);
        ProxyBuilder.callProxy(AddUserActivity.this, caller, returnedUser -> response(returnedUser));
//        user.setId(userId);
//        Call<List<User>> caller_ed = proxy.addToMonitoredByUsers(child.getId(),User.getInstance());
//        ProxyBuilder.callProxy(AddUserActivity.this, caller_ed, returnedUser -> response(returnedUser));
    }


    private void response(List<User> returnedUsers) {
        for (User user : returnedUsers) {
            Log.w("TAG", "    User: " + user.toString());
        }
    }

    public static Intent makeIntent(Context context, String flag) {
        Intent intent = new Intent(context,AddUserActivity.class);
        intent.putExtra(EXTRA_FLAG,flag);
        return intent;
    }
}