package ca.cmpt276.walkinggroup.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class GroupCreateActivity extends AppCompatActivity {
    private String token;
    private String TAG = "GroupCreateActivity";
    private WGServerProxy proxy;
    private String editDestination;
    private String editDescription;
    private String editMeetPlace;
    private User user;
    private long userId;
    private Group group;



    public static final int REQUEST_CODE_TARGET = 64;

    public static final String LATITUDE = "latitude";
    public static final String LONGTITUDE = "longtitude";
    public static final String PLACENAME = "placename";

    private List<Double> routeLatArray;
    private List<Double> routeLngArray;


    private double latitude;
    private double longtitude;
    private String placeName;
    private List<LatLng> latlng;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);
        SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref",0);
        token = dataToGet.getString("userToken","");
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
        routeLatArray = new ArrayList<>();
        routeLngArray = new ArrayList<>();

        Intent intent =getIntent();

        latitude    = intent.getDoubleExtra(LATITUDE,0);
        longtitude  = intent.getDoubleExtra(LONGTITUDE,0);
        placeName   = intent.getStringExtra(PLACENAME);

        TextView editDest = (TextView) findViewById(R.id.editDestination);
        editDest.setText(placeName);


        setOKBtn();
        setCancelBtn();
    }

    private void setOKBtn() {

        Button btn = (Button) findViewById(R.id.btnOK);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Extract data from UI

                EditText editDesc= (EditText) findViewById(R.id.editDescription);
                editDescription = editDesc.getText().toString();
                EditText editMeet= (EditText) findViewById(R.id.editMeetPlace);
                editMeetPlace = editMeet.getText().toString();


                user = User.getInstance();
                Call<User> caller = proxy.getUserByEmail(user.getEmail());
                ProxyBuilder.callProxy(GroupCreateActivity.this, caller, returnedUser -> response(returnedUser));

                Intent intenttomap = GroupActivity.makeIntent(GroupCreateActivity.this);
                startActivity(intenttomap);
                finish();
            }
        });
    }

    private void setCancelBtn() {

        Button btn = (Button) findViewById(R.id.btnCancel_Map);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Extract data from UI


                Intent intenttomap = GoogleMapsActivity.makeIntent(GroupCreateActivity.this);
                startActivity(intenttomap);
                finish();
            }
        });
    }





    private void response(User user) {

        group = new Group();
        group.setLeader(user);

        routeLatArray.add(latitude);
        routeLngArray.add(longtitude);

        group.setRouteLatArray(routeLatArray);
        group.setRouteLngArray(routeLngArray);

        group.setGroupDescription(editDescription);
        Call<Group> caller = proxy.createGroup(group);
        ProxyBuilder.callProxy(GroupCreateActivity.this, caller, returnedGroups -> response(returnedGroups));
    }

    private void response(Group groups) {

        Toast.makeText(GroupCreateActivity.this, ""+group.toString(), Toast.LENGTH_SHORT).show();

    }

    public static Intent makeIntent(Context context){
        return new Intent(context, GroupCreateActivity.class);
    }
}
