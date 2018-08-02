package ca.cmpt276.walkinggroup.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ca.cmpt276.walkinggroup.app.DialogFragment.MyToast;
import ca.cmpt276.walkinggroup.dataobjects.EarnedRewards;
import ca.cmpt276.walkinggroup.dataobjects.GpsLocation;
import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.Session;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

/**
 * Based button access to different activities
 * Check if user login at first beginning
 * logout button show after login only
 * the onResume receive the Loaction Service, get the GPS information every 10 seconds
 */


public class MainActivity extends AppCompatActivity {
    private static final Handler handler = new Handler();
    public static final int POINTS_GET = 5;
    private String token;
    private String TAG = "MainActivity";
    private WGServerProxy proxy;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private User user;

    private Long userId;

    private List<Group> groups = new ArrayList<>();
    private List<String> groupsDestination = new ArrayList<>();
    private List<Long> groupID = new ArrayList<>();

    private List<User> monitorsUsers = new ArrayList<>();

    // LocationUpdate values and widges
    private BroadcastReceiver mBroadcastReceiver;
    private Button btnUpdate;
    private Button btnStop;
    //private TextView locationInfo;
    private Boolean mStopSignal = false;
    private LatLng tempUserLocation;
    private List<Group> upGroups = new ArrayList<>();
    private Session session;
    private EarnedRewards current;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConstraintLayout layout = findViewById(R.id.main_layout);
        layout.setBackground(getResources().getDrawable(R.drawable.background0));

        SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref", 0);
        token = dataToGet.getString("userToken", "");
        userId = dataToGet.getLong("userId",0);

        session = Session.getInstance();
        session.setToken(token);
        session.setProxy(token);
        proxy = session.getProxy();
        //user = new User();
        Button btnLogout = (Button) findViewById(R.id.btnLogout);


        if (session.getToken() == "") {
            btnLogout.setVisibility(View.GONE);
            proxy = session.getProxy();
        } else {
            btnLogout.setVisibility(View.VISIBLE);
            populate();
            showChildGPS();
            backGround();
        }

        setGroupBtn();
        setLogoutBtn();
        setMonitoringBtn();
        setMonitorBtn();
        setInfoBtn();
        setMsgBtn();
        setPanicBtn();
        setParentBtn();
        setPermissionBtn();
        setLeaderBoardBtn();
        setRewardBtn();

        //set up location update
        btnUpdate = findViewById(R.id.start_update_location);
        btnStop = findViewById(R.id.stop_location_update);
        //locationInfo = findViewById(R.id.location_Information);
        if (session.getToken() == "") {
            btnUpdate.setVisibility(View.GONE);
            btnStop.setVisibility(View.GONE);
            //    locationInfo.setVisibility(View.GONE);
        }else {
            btnUpdate.setVisibility(View.VISIBLE);
            btnStop.setVisibility(View.VISIBLE);
            //    locationInfo.setVisibility(View.VISIBLE);
        }
        if (!runtime_permissions()) {
            setLocationUpdate();
        }

        if (isServicesOK()) {
            setMapButton();
        }




    }

//    private void setTitleTextView(User tempUser) {
//        TextView title = findViewById(R.id.user_title);
//        title.setText(tempUser.getRewards().getTitle());
//        title.setTextColor(tempUser.getRewards().getTitleColor());
//    }

    private void backGround() {
        Call<User> caller = proxy.getUserById(userId);
        ProxyBuilder.callProxy(MainActivity.this,caller,returned -> responseForGet(returned));
    }

    private void responseForGet(User returned) {
        Gson gson = new Gson();
        String json = returned.getCustomJson();
        current = gson.fromJson(json, EarnedRewards.class);
        changeBackGround();
    }

    private void changeBackGround(){
        TextView title = findViewById(R.id.user_title);
        title.setText(current.getTitle());
        ConstraintLayout layout = findViewById(R.id.main_layout);
//        MyToast.makeText(this,""+current.getSelectedBackground(),Toast.LENGTH_SHORT).show();
        if(current.getSelectedBackground() == 0){
            layout.setBackground(getResources().getDrawable(R.drawable.background0));
        }
        if(current.getSelectedBackground() == 1){
            layout.setBackground(getResources().getDrawable(R.drawable.background1));
        }
        if(current.getSelectedBackground() == 2){
            layout.setBackground(getResources().getDrawable(R.drawable.background2));
        }
        if(current.getSelectedBackground() == 3){
            layout.setBackground(getResources().getDrawable(R.drawable.background3));
        }
        if(current.getSelectedBackground() == 4){
            layout.setBackground(getResources().getDrawable(R.drawable.background4));
        }
        if(current.getSelectedBackground() == 5){
            layout.setBackground(getResources().getDrawable(R.drawable.background5));
        }
        if(current.getSelectedBackground() == 6){
            layout.setBackground(getResources().getDrawable(R.drawable.background6));
        }

//        User tempUser = session.getUser();
//        if (tempUser.getTotalPointsEarned() != null) {
//            if (tempUser.getTotalPointsEarned() > 100) {
//                current.setTitle("Little bird");
//                current.setTitleColor(Color.GREEN);
//            } else if (tempUser.getTotalPointsEarned() > 500) {
//                current.setTitle("Warrior");
//                current.setTitleColor(Color.CYAN);
//            } else if (tempUser.getTotalPointsEarned() > 1000) {
//                current.setTitle("Warlord ");
//                current.setTitleColor(Color.LTGRAY);
//            } else if (tempUser.getTotalPointsEarned() > 3000) {
//                current.setTitle("Paladin");
//                current.setTitleColor(Color.WHITE);
//            } else if (tempUser.getTotalPointsEarned() > 6000) {
//                current.setTitle("High lord");
//                current.setTitleColor(Color.YELLOW);
//            } else if (tempUser.getTotalPointsEarned() > 10000) {
//                current.setTitle("Dragon slayer");
//                current.setTitleColor(Color.RED);
//            }
//            tempUser.setRewards(current);
//            Call<User> callerUpdate = proxy.editUserById(session.getUser().getId(), tempUser);
//            ProxyBuilder.callProxy(MainActivity.this, callerUpdate, returnedUser -> responseUserUpdate(returnedUser));
//            setTitleTextView(tempUser);
//        }
    }

    private void showChildGPS() {
        Call<List<User>> caller = proxy.getMonitorsUsers(userId);
        ProxyBuilder.callProxy(MainActivity.this,caller,returnedList -> responseForGPS(returnedList));
    }

    private void responseForGPS(List<User> returnedList) {
        monitorsUsers = returnedList;
        String[] items = new String[monitorsUsers.size()];
        for (int i = 0; i < monitorsUsers.size(); i++) {
            items[i] = monitorsUsers.get(i).getName() + " - " + monitorsUsers.get(i).getEmail() + " " + monitorsUsers.get(i).getLastGpsLocation();
        }
        //   ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.child_gps, items);
        //   ListView list = (ListView) findViewById(R.id.list_child_gps);
        //   list.setAdapter(adapter);
    }


    private void populate() {
        Call<User> caller = proxy.getUserById(userId);
        ProxyBuilder.callProxy(MainActivity.this, caller, returnedUser -> responseForMain(returnedUser));
    }

    private void responseForMain(User user) {
        session.setUser(user);
        groups.clear();
        groups.addAll(user.getLeadsGroups());
        groups.addAll(user.getMemberOfGroups());
        try {
            groupsDestination.clear();
            groupID.clear();
            if (groups.size() > 0) {
                for (int i = 0; i < groups.size(); i++) {
                    Call<Group> caller = proxy.getGroupById(groups.get(i).getId());
                    ProxyBuilder.callProxy(MainActivity.this, caller, returnedGroup -> responseGroup(returnedGroup));
                }
            } else {
                //    populateGroup();
            }
        } catch (Exception e) {

        }
    }

    private void responseGroup(Group returnedGroup) {
        groupID.add(returnedGroup.getId());
        groupsDestination.add(getString(R.string.Group_) + " " + returnedGroup.getGroupDescription() + " [" + returnedGroup.getRouteLatArray().get(0) + " , " + returnedGroup.getRouteLngArray().get(0) + "]");
        upGroups.add(returnedGroup);
        if (groupID.size() == groups.size()) {
            //   populateGroup();
        }
    }


//    private void populateGroup() {
//        ArrayAdapter<String> adapterLeader = new ArrayAdapter<>(this, R.layout.group_destination, groupsDestination);
//        ListView listLeader = (ListView) findViewById(R.id.list_group_destination);
//        listLeader.setAdapter(adapterLeader);
//    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBroadcastReceiver == null) {
            mBroadcastReceiver = new BroadcastReceiver() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onReceive(Context context, Intent intent) {
                    mStopSignal = false;
                    double tempLat = intent.getDoubleExtra("UpdateLat", 0);
                    double tempLng = intent.getDoubleExtra("UpdateLng", 0);
                    tempUserLocation = new LatLng(tempLat, tempLng);
                    //       locationInfo.setText("LatLLng: " + tempLat + ", " + tempLng + " " + mStopSignal);

                    GpsLocation gpsLocation = new GpsLocation();
                    gpsLocation.setLat(tempLat);
                    gpsLocation.setLng(tempLng);
                    gpsLocation.setTimestamp(Calendar.getInstance().getTime());

                    Call<GpsLocation> caller = proxy.setLastGpsLocation(userId, gpsLocation);
                    ProxyBuilder.callProxy(MainActivity.this, caller, returnedUser -> response(returnedUser));

                }
            };
        }
        // get Intent from LocationService
        registerReceiver(mBroadcastReceiver, new IntentFilter("UpdateLocation"));

//        if(session.getToken() != ""){
//            populate();
//            showChildGPS();
//        }

    }

   /* Runnable toastRunnable = new Runnable() {
        @Override
        public void run() {

            Toast.makeText(MainActivity.this, "mStopSignal: " + mStopSignal, Toast.LENGTH_SHORT).show();
            handler.postDelayed(this, 5000);
        }
    };
*/

    Runnable checkChangeRunnable = new Runnable() {
        @Override
        public void run() {
            if (tempUserLocation != null && upGroups != null) {
                for (int i = 0; i < upGroups.size(); i++) {
                    List<Double> tempLatArray = upGroups.get(i).getRouteLatArray();
                    List<Double> tempLngArray = upGroups.get(i).getRouteLngArray();
                    LatLng tempPosition = new LatLng(tempLatArray.get(0), tempLngArray.get(0));
                    float results[] = new float[2];
                    Location.distanceBetween(tempPosition.latitude, tempPosition.longitude, tempUserLocation.latitude, tempUserLocation.longitude, results);

                    if (results[0] <= 200f) {
                        mStopSignal = true;
                        break;
                    }

                    mStopSignal = false;
                }
            }

            MyToast.makeText(MainActivity.this, getString(R.string.latlng)+" " + tempUserLocation + ", " + mStopSignal, Toast.LENGTH_SHORT).show();

            if (mStopSignal) {
                User tempUser = session.getUser();
                if (tempUser.getTotalPointsEarned() == null){
                    tempUser.setTotalPointsEarned(0);
                }
                if (tempUser.getCurrentPoints() == null){
                    tempUser.setCurrentPoints(0);
                }

                tempUser.setCurrentPoints(session.getUser().getCurrentPoints() + 5);
                tempUser.setTotalPointsEarned(session.getUser().getTotalPointsEarned() + 5);
                Call<User> callerUpdate = proxy.editUserById(session.getUser().getId(), tempUser);
                ProxyBuilder.callProxy(MainActivity.this, callerUpdate, returnedUser -> responseUserUpdate(returnedUser));

                MyToast.makeText(MainActivity.this, getString(R.string.arrive_target_place) + getString(R.string.points_) + tempUser.getCurrentPoints() + ", " + tempUser.getTotalPointsEarned(), Toast.LENGTH_SHORT).show();
                stopService(new Intent(getApplicationContext(), LocationService.class));

                //handler.removeCallbacks(toastRunnable);
                handler.removeCallbacks(checkChangeRunnable);
            }else {
                handler.postDelayed(this, 600000);
            }
        }
    };

    private void responseUserUpdate(User returnedUser) {
    }

    private void response(GpsLocation location) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }
    }


    private boolean runtime_permissions() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission
                .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission
                .ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);

            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                setLocationUpdate();
            } else {
                runtime_permissions();
            }
        }
    }

    private void setLocationUpdate() {
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyToast.makeText(MainActivity.this, getString(R.string.start_update_location), Toast.LENGTH_SHORT).show();
                //handler.postDelayed(toastRunnable, 5000);
                handler.postDelayed(checkChangeRunnable, 600000);

                startService(new Intent(getApplicationContext(), LocationService.class));
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tempUserLocation != null && upGroups != null) {
                    for (int i = 0; i < upGroups.size(); i++) {
                        List<Double> tempLatArray = upGroups.get(i).getRouteLatArray();
                        List<Double> tempLngArray = upGroups.get(i).getRouteLngArray();
                        LatLng tempPosition = new LatLng(tempLatArray.get(0), tempLngArray.get(0));
                        float results[] = new float[2];
                        Location.distanceBetween(tempPosition.latitude, tempPosition.longitude, tempUserLocation.latitude, tempUserLocation.longitude, results);

                        if (results[0] <= 200f) {
                            mStopSignal = true;
                            break;
                        }

                        mStopSignal = false;
                    }
                }

                MyToast.makeText(MainActivity.this, getString(R.string.latlng)+" " + tempUserLocation + ", " + mStopSignal, Toast.LENGTH_SHORT).show();

                if (mStopSignal) {
                    User tempUser = session.getUser();
                    if (tempUser.getTotalPointsEarned() == null){
                        tempUser.setTotalPointsEarned(0);
                    }
                    if (tempUser.getCurrentPoints() == null){
                        tempUser.setCurrentPoints(0);
                    }

                    tempUser.setCurrentPoints(session.getUser().getCurrentPoints() + POINTS_GET);
                    tempUser.setTotalPointsEarned(session.getUser().getTotalPointsEarned() + POINTS_GET);
                    Call<User> callerUpdate = proxy.editUserById(session.getUser().getId(), tempUser);
                    ProxyBuilder.callProxy(MainActivity.this, callerUpdate, returnedUser -> responseUserUpdate(returnedUser));

                    MyToast.makeText(MainActivity.this, getString(R.string.arrive_target_place) + getString(R.string.points_) + tempUser.getCurrentPoints() + ", " + tempUser.getTotalPointsEarned(), Toast.LENGTH_SHORT).show();
                }

                MyToast.makeText(MainActivity.this, getString(R.string.stop_update_location), Toast.LENGTH_SHORT).show();
                stopService(new Intent(getApplicationContext(), LocationService.class));

                //handler.removeCallbacks(toastRunnable);
                handler.removeCallbacks(checkChangeRunnable);
            }
        });
    }

    private void setParentBtn() {
        Button btnParent = (Button) findViewById(R.id.btnParent);
        btnParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (session.getToken()) {
                    case "":
                        Intent intentToLogin = LoginActivity.makeIntent(MainActivity.this);
                        startActivity(intentToLogin);
                        break;
                    default:
                        Intent intentParent = ParentActivity.makeIntent(MainActivity.this);
                        startActivity(intentParent);

                }
            }
        });
    }

    private void setInfoBtn() {
        Button btnInfo = (Button) findViewById(R.id.btnInfo);
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (session.getToken()) {
                    case "":
                        Intent intentToLogin = LoginActivity.makeIntent(MainActivity.this);
                        startActivity(intentToLogin);
                        finish();
                        break;
                    default:
                        Intent intentInfo = UserInfoActivity.makeIntent(MainActivity.this);
                        startActivityForResult(intentInfo,99);

                }

            }
        });
    }


    private void setMonitorBtn() {
        Button btnMonitor = (Button) findViewById(R.id.btnMonitor);
        btnMonitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (session.getToken()) {
                    case "":
                        Intent intentToLogin = LoginActivity.makeIntent(MainActivity.this);
                        startActivity(intentToLogin);
                        finish();
                        break;
                    default:
                        Intent intentMonitor = MonitorActivity.makeIntent(MainActivity.this);
                        startActivity(intentMonitor);

                }

            }
        });
    }

    private void setMonitoringBtn() {
        Button btnMonitoring = (Button) findViewById(R.id.btnMonitoring);
        btnMonitoring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (session.getToken()) {
                    case "":
                        Intent intentToLogin = LoginActivity.makeIntent(MainActivity.this);
                        startActivity(intentToLogin);
                        finish();
                        break;
                    default:
                        Intent intentMonitoring = MonitoringActivity.makeIntent(MainActivity.this, 0);
                        startActivity(intentMonitoring);

                }

            }
        });
    }


    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            // everything is fine and user can make map result
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            // an error occurred but we can resolve it
            Log.d(TAG, "isServicesOK: an error occurred but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            MyToast.makeText(this, R.string.cant_make_map, Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void setMapButton() {
        Button btn = findViewById(R.id.google_map);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (session.getToken()) {
                    case "":
                        Intent intentToLogin = LoginActivity.makeIntent(MainActivity.this);
                        startActivity(intentToLogin);
                        finish();
                        break;
                    default:
                        startActivity(new Intent(MainActivity.this, GoogleMapsActivity.class));

                }

            }
        });
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    private void setGroupBtn() {
        Button btnGroup = (Button) findViewById(R.id.btnGroup);
        btnGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (session.getToken()) {
                    case "":
                        Intent intentToLogin = LoginActivity.makeIntent(MainActivity.this);
                        startActivity(intentToLogin);
                        finish();
                        break;
                    default:
                        Intent intent = GroupActivity.makeIntent(MainActivity.this);
                        startActivity(intent);

                }

            }
        });
    }

    private void setLogoutBtn() {
        Button btnLogout = (Button) findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(getApplicationContext(), LocationService.class));
                //handler.removeCallbacks(toastRunnable);
                handler.removeCallbacks(checkChangeRunnable);

                SharedPreferences dataToSave = getApplicationContext().getSharedPreferences("userPref", 0);
                SharedPreferences.Editor PrefEditor = dataToSave.edit();
                PrefEditor.putString("userToken", "");

                PrefEditor.apply();
                session.setToken("");
                proxy = session.getProxy();
                MyToast.makeText(MainActivity.this, R.string.log_out_success, Toast.LENGTH_LONG).show();
                Intent intentToLogin = LoginActivity.makeIntent(MainActivity.this);
                startActivity(intentToLogin);
                finish();

            }
        });
    }

    private void setMsgBtn() {
        Button btnGroup = (Button) findViewById(R.id.btnMsg);
        btnGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (session.getToken()) {
                    case "":
                        Intent intent = LoginActivity.makeIntent(MainActivity.this);
                        startActivity(intent);
                        finish();
                        break;
                    default:
                        Intent intenttomsg = MessagesActivity.makeIntent(MainActivity.this);
                        startActivity(intenttomsg);

                }

            }
        });
    }

    private void setPanicBtn() {
        Button btnGroup = (Button) findViewById(R.id.btnPanic);
        btnGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (session.getToken()) {
                    case "":
                        Intent intent = LoginActivity.makeIntent(MainActivity.this);
                        startActivity(intent);
                        finish();
                        break;
                    default:
                        Intent intenttoEmsg = MessagesEmergencyActivity.makeIntent(MainActivity.this);
                        startActivity(intenttoEmsg);

                }

            }
        });
    }

    private void setLeaderBoardBtn() {
        Button btnLB = (Button) findViewById(R.id.btnLB);
        btnLB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (session.getToken()) {
                    case "":
                        Intent intent = LoginActivity.makeIntent(MainActivity.this);
                        startActivity(intent);
                        finish();
                        break;
                    default:
                        Intent intentToLB = LeaderBoardActivity.makeIntent(MainActivity.this);
                        startActivity(intentToLB);

                }
            }
        });
    }

    private void setPermissionBtn() {
        Button btnMonitor = (Button) findViewById(R.id.btnPermission);
        btnMonitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (session.getToken()) {
                    case "":
                        Intent intentToLogin = LoginActivity.makeIntent(MainActivity.this);
                        startActivity(intentToLogin);
                        finish();
                        break;
                    default:
                        Intent intentMonitor = PermissionActivity.makeIntent(MainActivity.this);
                        startActivity(intentMonitor);

                }

            }
        });
    }

    private void setRewardBtn() {
        Button btnReward = findViewById(R.id.btnReward);
        btnReward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        switch (session.getToken()) {
                            case "":
                                Intent intentToLogin = LoginActivity.makeIntent(MainActivity.this);
                                startActivity(intentToLogin);
                                finish();
                                break;
                            default:
                                Intent intentToReward = RewardActivity.makeIntent(MainActivity.this);
                                startActivityForResult(intentToReward,920);
                    }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case 99:
            if (resultCode == RESULT_OK) {
                this.finish();
            }
            case 920:
                if (resultCode == RESULT_OK) {
//                    session = Session.getInstance();
//                    proxy = session.getProxy();
//                    user = session.getUser();
//                    userId = user.getId();
                    Gson gson = new Gson();
                    String json = data.getStringExtra("rewards");
                    current = gson.fromJson(json, EarnedRewards.class);
                    changeBackGround();
                    SharedPreferences dataToSave = getApplicationContext().getSharedPreferences("userPref", 0);
                    SharedPreferences.Editor PrefEditor = dataToSave.edit();
                    PrefEditor.putInt("bg", current.getSelectedBackground());
                    PrefEditor.apply();
                    //user.setRewards(current);
                    //Toast.makeText(this,""+user.getCurrentPoints()+" "+ user.getName(),Toast.LENGTH_LONG).show();
//                    Toast.makeText(this,""+user.getBirthYear(),Toast.LENGTH_LONG).show();
                }
        }
    }
}
