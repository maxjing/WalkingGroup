package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class UserinfoActivity extends AppCompatActivity {
    private User user;
    private WGServerProxy proxy;
    private static final String TAG = "Userinfo";
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);

        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), null);

        user = User.getInstance();
//        Toast.makeText(UserinfoActivity.this, "Email:"+user.getEmail(), Toast.LENGTH_SHORT).show();
//        userEmail = user.getEmail();
//        Call<User> caller = proxy.getUserByEmail(userEmail);
//        ProxyBuilder.callProxy(UserinfoActivity.this, caller, returnedUser -> response(returnedUser));
        userEmail = user.getEmail();
        setupGetUserByEmail();



    }
    private void response(User user) {
        notifyUserViaLogAndToast("Server replied with user: " + user.toString());
        userEmail = user.getEmail();
    }

    private void setupGetUserByEmail() {
        Button btn = findViewById(R.id.btnGetEmail);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Make call
                Toast.makeText(UserinfoActivity.this, "email from userinfo:"+userEmail, Toast.LENGTH_SHORT).show();
                Call<User> caller = proxy.getUserByEmail(userEmail);
                ProxyBuilder.callProxy(UserinfoActivity.this, caller, returnedUser -> response(returnedUser));

            }
        });
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, UserinfoActivity.class);
    }


    private void notifyUserViaLogAndToast(String message) {
        Log.w(TAG, message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }



}
