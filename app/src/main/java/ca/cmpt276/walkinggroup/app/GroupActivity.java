package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;

public class GroupActivity extends AppCompatActivity {
    private String token;
    private String TAG = "GroupActivity";
    private WGServerProxy proxy;
    private String userEmail;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref",0);
        token = dataToGet.getString("userToken","");
        userEmail = dataToGet.getString("userEmail","");
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
        Toast.makeText(this, ""+user.getEmail(), Toast.LENGTH_SHORT).show();
    }
    public static Intent makeIntent(Context context){
        return new Intent(context, GroupActivity.class);
    }
}
