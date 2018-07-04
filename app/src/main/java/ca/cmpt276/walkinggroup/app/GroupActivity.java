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

import ca.cmpt276.walkinggroup.dataobjects.EarnedRewards;
import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class GroupActivity extends AppCompatActivity {
    private String token;
    private String TAG = "GroupActivity";
    private WGServerProxy proxy;
    private List<Group> groupList;
    private Double[] latitudes;
    private Double[] longtitudes;
    private String[] groupDes;
    private Long[] groupId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref",0);
        token = dataToGet.getString("userToken","");
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
        setupNewGroupButton();
        setupTestButton();
        setupShowGroup();

    }

    private void setupNewGroupButton() {
        Button btn = findViewById(R.id.btnNewGroup);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToCreate = GroupCreateActivity.makeIntent(GroupActivity.this);
                startActivity(intentToCreate);
            }
        });
    }


    private void setupShowGroup() {
        Button btn = findViewById(R.id.showGroup);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<List<Group>> caller = proxy.getGroups();
                ProxyBuilder.callProxy(GroupActivity.this, caller, returnedGroup -> response(returnedGroup));
            }
        });
    }

    private void response(List<Group> groups) {
        groupList = groups;
        latitudes   = new Double[groupList.size()];
        longtitudes = new Double[groupList.size()];
        groupDes    = new String[groupList.size()];
        groupId     = new Long[groupList.size()];

        for (int i = 0; i < groupList.size(); i++) {
            latitudes[i]    = groupList.get(i).getRouteLatArray().get(0);
            longtitudes[i]  = groupList.get(i).getRouteLngArray().get(0);
            groupDes[i]     =  groupList.get(i).getGroupDescription();
            groupId[i]      =groupList.get(i).getId();
        }
        for (int i = 0; i< latitudes.length;i++){

            Toast.makeText(this, ""+"id: "+groupId[i]+" "+"latitude: "+latitudes[i]+" "+"longtitude: "+longtitudes[i]+"\n"+
                    "Description: "+groupDes[i]+" \n\n", Toast.LENGTH_SHORT).show();

            Log.i(TAG,"id: "+groupId[i]+" "+"latitude: "+latitudes[i]+" "+"longtitude: "+longtitudes[i]+"\n"+
                    "Description: "+groupDes[i]+" \n\n");
        }


    }

    private void setupTestButton() {
        Button btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToCreate = GroupTestActivity.makeIntent(GroupActivity.this);
                startActivity(intentToCreate);
            }
        });
    }



    public static Intent makeIntent(Context context){
        return new Intent(context, GroupActivity.class);
    }
}
