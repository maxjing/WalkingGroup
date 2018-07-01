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

    private User user;
    private List<User> monitorsUsers;
    private WGServerProxy proxy;
    String editName;
    private long userId;
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
        Toast.makeText(this,Long.toString(userId),Toast.LENGTH_LONG).show();
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
                ProxyBuilder.callProxy(AddUserActivity.this, caller, returnedUser -> responseforaddMonitoring(returnedUser));

                setResult(Activity.RESULT_OK);
                Toast.makeText(AddUserActivity.this,Long.toString(userId),Toast.LENGTH_LONG).show();
                finish();

//                monitorsUsers = user.getMonitorsUsers();
//
//                proxy = ProxyBuilder.getProxy(getString(R.string.apikey));
//                Call<User> caller = proxy.getUserByEmail(editEmail);
//                ProxyBuilder.callProxy(AddUserActivity.this, caller, returnedUser -> response(returnedUser));
//                Log.i("Log",caller.toString());
//
//                for(int i = 0 ;i < proxy.getUsers().size())
//                if(caller.getClass().getName().equals(editName)){
//                    user.addMonitorsUsers();
//                }
//                ProxyBuilder.callProxy(this,caller,returnedUser -> response(returnedUser));
//                add.setName(editName);
//                add.setEmail(editEmail);
//                user.addMonitorsUsers(add);
//                Log.i("Log",user.getMonitorsUsers().toString());
//                Call<List<User>> caller = proxy.getUsers();
//                Log.i("Log",caller.toString());
            }
        });
    }
    private void response(User user) {
        userId = user.getId();
    }

    private void responseforaddMonitoring(User user) {
        Call<List<User>> caller = proxy.addToMonitorsUsers(userId,user);
        //proxy.addToMonitorsUsers(userId,user);
        ProxyBuilder.callProxy(AddUserActivity.this, caller, returnedUser -> response(returnedUser) );
    }

    private void response(List<User> returnedUsers) {

        for (User user : returnedUsers) {
            Log.w("Log", "    User: " + user.toString());
        }
    }

//    private void response(User returnedUser) {
//        if(editName.equals(returnedUser.getName())){
//            Call<List<User>> monitorscaller =  proxy.addToMonitorsUsers(user.getId(),returnedUser);
//            //Call<List<User>> monitoredcaller = proxy.addToMonitoredByUsers(returnedUser,user.getId());
//        }
//    }

//    private void response(User user){
//       monitorsUsers.add(user);
//
//    }

    public static Intent makeIntent(Context context) {
        return new Intent(context,AddUserActivity.class);
    }
}