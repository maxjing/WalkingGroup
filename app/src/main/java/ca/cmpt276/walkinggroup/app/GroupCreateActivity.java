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


/**
 * Jump from map, and fetch the intent from map including target and meet place latitude and longtitude
 * Able to fetch both place name, not need to fill by typing, do this for users automatically
 */

public class GroupCreateActivity extends AppCompatActivity {
    private String token;
    private String TAG = "GroupCreateActivity";
    private WGServerProxy proxy;
    private String editDescription;
    private String editMeetPlace;
    private User user;
    private Group group;




    public static final String LATITUDE = "latitude";
    public static final String LONGTITUDE = "longtitude";
    public static final String PLACENAME = "placename";
    public static final String MEETINGPLACE = "meetingPlace";
    public static final String MEETLAT = "meetLat";
    public static final String MEETLNG = "meetLng";

    private List<Double> routeLatArray;
    private List<Double> routeLngArray;



    private double latitude;
    private double longtitude;
    private String placeName;
    private String meetingPlace;
    private double meetLat;
    private double meetLng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);
        SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref",0);
        token = dataToGet.getString("userToken","");
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);

        Intent intent =getIntent();

        latitude    = intent.getDoubleExtra(LATITUDE,0);
        longtitude  = intent.getDoubleExtra(LONGTITUDE,0);
        placeName   = intent.getStringExtra(PLACENAME);

        meetingPlace = intent.getStringExtra(MEETINGPLACE);
        meetLat      = intent.getDoubleExtra(MEETLAT,0);
        meetLng      = intent.getDoubleExtra(MEETLNG,0);


        TextView editDest = (TextView) findViewById(R.id.editDestination);
        editDest.setText(placeName);
        TextView editMeet= (TextView) findViewById(R.id.editMeetPlace);
        editMeet.setText(meetingPlace);


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

                routeLatArray = new ArrayList<>();
                routeLngArray = new ArrayList<>();




                user = User.getInstance();
                Call<User> caller = proxy.getUserByEmail(user.getEmail());
                ProxyBuilder.callProxy(GroupCreateActivity.this, caller, returnedUser -> response(returnedUser));

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
        routeLatArray.add(meetLat);

        routeLngArray.add(longtitude);
        routeLngArray.add(meetLng);



        group.setRouteLatArray(routeLatArray);
        group.setRouteLngArray(routeLngArray);

        group.setGroupDescription(editDescription);


        Call<Group> caller = proxy.createGroup(group);
        ProxyBuilder.callProxy(GroupCreateActivity.this, caller, returnedGroups -> response(returnedGroups));
    }

    private void response(Group groups) {



    }

    public static Intent makeIntent(Context context){
        return new Intent(context, GroupCreateActivity.class);
    }
}
