package ca.cmpt276.walkinggroup.app;

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

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class UserinfoActivity extends AppCompatActivity {
    private User user;
    private WGServerProxy proxy;
    private static final String TAG = "Userinfo";
    private String userEmail;
    private long userId = 0;
    private String token;
    List<User> userstomonitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref",0);
        token = dataToGet.getString("userToken","");
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
        user = User.getInstance();
        userEmail = user.getEmail();
        Call<User> caller = proxy.getUserByEmail(userEmail);
        ProxyBuilder.callProxy(UserinfoActivity.this, caller, returnedUser -> response(returnedUser));
        setAddBtn();


    }

    private void response(User user) {
        notifyUserViaLogAndToast("Server replied with user: " + user.toString());
        userId = user.getId();

    }



    private void setAddBtn(){
        Button btnAdd = (Button)findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                EditText email = (EditText)findViewById(R.id.editTextEmail);
                String Email = email.getText().toString();
                Call<User> caller = proxy.getUserByEmail(Email);
                ProxyBuilder.callProxy(UserinfoActivity.this, caller, returnedUser -> responseforaddMonitoring(returnedUser));


            }
        });
    }
    private void responseforaddMonitoring(User user) {
        notifyUserViaLogAndToast("Server replied with user: " + user.toString());
        Call<List<User>> caller = proxy.addToMonitoredByUsers(userId,user);
        ProxyBuilder.callProxy(UserinfoActivity.this, caller, returnedUser -> response(returnedUser) );
    }

    private void response(List<User> returnedUsers) {
        notifyUserViaLogAndToast("add success"+user.toString());

        for (User user : returnedUsers) {
            Log.w(TAG, "    User: " + user.toString());
        }
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, UserinfoActivity.class);
    }


    private void notifyUserViaLogAndToast(String message) {
        Log.w(TAG, message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }




}
