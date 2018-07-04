package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Path;

public class GroupTestActivity extends AppCompatActivity {
    private String token;
    private String TAG = "GroupTestActivity";
    private WGServerProxy proxy;
    private long groudId = 199;
    private long userId = 0;
    private Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_test);
        SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref",0);
        token = dataToGet.getString("userToken","");
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
        userId = dataToGet.getLong("userId",0);

        Toast.makeText(this, ""+userId, Toast.LENGTH_SHORT).show();
        Call<User> caller = proxy.getUserById(userId);
        ProxyBuilder.callProxy(GroupTestActivity.this, caller, returnedUser -> response(returnedUser));
        setupAddChild();

    }

    private void response(User user) {
        Toast.makeText(this, user.getLeadsGroups().get(0).getGroupDescription(), Toast.LENGTH_SHORT).show();

        Toast.makeText(this,user.getMemberOfGroups().size(),Toast.LENGTH_LONG).show();

    }


    private void setupAddChild() {
        Button btn = findViewById(R.id.button1);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Call<Group>  caller = proxy.getGroupById(groudId);
                ProxyBuilder.callProxy(GroupTestActivity.this, caller, returnedUser -> response(returnedUser));
            }
        });
    }

    private void response(Group group) {
        Toast.makeText(this, ""+group.getLeader().getId(), Toast.LENGTH_SHORT).show();

    }
    public static Intent makeIntent(Context context){
        return new Intent(context, GroupTestActivity.class);
    }
}
