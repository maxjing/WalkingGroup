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
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import ca.cmpt276.walkinggroup.dataobjects.GpsLocation;
import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

/**
 * Based button access to different activities
 * Check if user login at first beginning
 * logout button show after login only
 */


public class MainActivity extends AppCompatActivity {
    private String token;
    private String TAG = "MainActivity";
    private WGServerProxy proxy;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private User user;
    private String userEmail;
    private Long userId;

    private List<Group> groups = new ArrayList<>();
    private List<String> groupsDestination = new ArrayList<>();
    private List<Long> groupID = new ArrayList<>();

    // LocationUpdate values and widges
    private BroadcastReceiver mBroadcastReceiver;
    private Button btnUpdate;
    private Button btnStop;
    private TextView locationInfo;
    private Boolean mStopSignal = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref", 0);
        token = dataToGet.getString("userToken", "");
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
        userId = dataToGet.getLong("userId", 0);

        user = User.getInstance();


        Button btnLogout = (Button) findViewById(R.id.btnLogout);


        if (token == "") {
            btnLogout.setVisibility(View.GONE);
        } else {
            btnLogout.setVisibility(View.VISIBLE);


        }

        setGroupBtn();
        setLogoutBtn();
        setMonitoringBtn();
        setMonitorBtn();
        setInfoBtn();
        setMsgBtn();
        setPanicBtn();

        //set up location update
        btnUpdate = findViewById(R.id.start_update_location);
        btnStop = findViewById(R.id.stop_location_update);
        locationInfo = findViewById(R.id.location_Information);
        if (!runtime_permissions()) {
            setLocationUpdate();
        }

        if (isServicesOK()) {
            setMapButton();
        }

        populate();
    }

    private void populate() {
        Call<User> caller = proxy.getUserById(userId);
        ProxyBuilder.callProxy(MainActivity.this, caller, returnedUser -> responseForMain(returnedUser));
    }

    private void responseForMain(User user) {
        groups.clear();
        groups.addAll(user.getLeadsGroups());
        groups.addAll(user.getMemberOfGroups());
        try {
            groupsDestination.clear();
            groupID.clear();
            if(groups.size() > 0){
                for(int i = 0; i < groups.size(); i++){
                    Call<Group> caller = proxy.getGroupById(groups.get(i).getId());
                    ProxyBuilder.callProxy(MainActivity.this, caller, returnedGroup -> responseGroup(returnedGroup));
                }
            }else{
                populateGroup();
            }
        } catch (Exception e) {

        }


    }

    private void responseGroup(Group returnedGroup) {
        groupID.add(returnedGroup.getId());
        groupsDestination.add(getString(R.string.Group_)+" " + returnedGroup.getGroupDescription()+" ["+ returnedGroup.getRouteLatArray().get(0)+" , "+returnedGroup.getRouteLngArray().get(0)+"]");
        if(groupID.size() == groups.size()){
            populateGroup();
        }
    }


    private void populateGroup() {
        ArrayAdapter<String> adapterLeader = new ArrayAdapter<>(this, R.layout.group_destination, groupsDestination);
        ListView listLeader = (ListView) findViewById(R.id.list_group_destination);
        listLeader.setAdapter(adapterLeader);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(toastRunnable, 5000);
        handler.postDelayed(checkChangeRunnable, 10000);

        if (mBroadcastReceiver == null) {
            mBroadcastReceiver = new BroadcastReceiver() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onReceive(Context context, Intent intent) {
                    mStopSignal = false;
                    double tempLat = intent.getDoubleExtra("UpdateLat", 0);
                    double tempLng = intent.getDoubleExtra("UpdateLng", 0);
                    locationInfo.setText("LatLLng: " + tempLat + ", " + tempLng + " " + mStopSignal);

                    GpsLocation gpsLocation = new GpsLocation();
                    gpsLocation.setLat(tempLat);
                    gpsLocation.setLng(tempLng);

                    Call<GpsLocation> caller = proxy.setLastGpsLocation(userId, gpsLocation);
                    ProxyBuilder.callProxy(MainActivity.this, caller, returnedUser -> response(returnedUser));

                }
            };
        }
        // get Intent from LocationService
        registerReceiver(mBroadcastReceiver, new IntentFilter("UpdateLocation"));
    }


    Handler handler = new Handler();
    Runnable toastRunnable = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(MainActivity.this, "mStopSignal: " + mStopSignal, Toast.LENGTH_SHORT).show();
            handler.postDelayed(this, 5000);
        }
    };
    Runnable checkChangeRunnable = new Runnable() {
        @Override
        public void run() {
            mStopSignal = true;
            handler.postDelayed(this, 10000);
        }
    };

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
                startService(new Intent(getApplicationContext(), LocationService.class));
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(new Intent(getApplicationContext(), LocationService.class));
            }
        });
    }

    private void setInfoBtn() {
        Button btnInfo = (Button) findViewById(R.id.btnInfo);
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (token) {
                    case "":
                        Intent intentToLogin = LoginActivity.makeIntent(MainActivity.this);
                        startActivity(intentToLogin);
                        break;
                    default:
                        Intent intentInfo = UserInfoActivity.makeIntent(MainActivity.this);
                        startActivity(intentInfo);

                }

            }
        });
    }


    private void setMonitorBtn() {
        Button btnMonitor = (Button) findViewById(R.id.btnMonitor);
        btnMonitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (token) {
                    case "":
                        Intent intentToLogin = LoginActivity.makeIntent(MainActivity.this);
                        startActivity(intentToLogin);
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
                switch (token) {
                    case "":
                        Intent intentToLogin = LoginActivity.makeIntent(MainActivity.this);
                        startActivity(intentToLogin);
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
            Toast.makeText(this, R.string.cant_make_map, Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void setMapButton() {
        Button btn = findViewById(R.id.google_map);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (token) {
                    case "":
                        Intent intentToLogin = LoginActivity.makeIntent(MainActivity.this);
                        startActivity(intentToLogin);
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
                switch (token) {
                    case "":
                        Intent intentToLogin = LoginActivity.makeIntent(MainActivity.this);
                        startActivity(intentToLogin);
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
                SharedPreferences dataToSave = getApplicationContext().getSharedPreferences("userPref", 0);
                SharedPreferences.Editor PrefEditor = dataToSave.edit();
                PrefEditor.putString("userToken", "");
                PrefEditor.apply();
                Toast.makeText(MainActivity.this, R.string.log_out_success, Toast.LENGTH_LONG).show();
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
                switch (token) {
                    case "":
                        Intent intent = LoginActivity.makeIntent(MainActivity.this);
                        startActivity(intent);
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
                switch (token) {
                    case "":
                        Intent intent = LoginActivity.makeIntent(MainActivity.this);
                        startActivity(intent);
                        break;
                    default:
                        Intent intenttoEmsg= MessagesEmergencyActivity.makeIntent(MainActivity.this);
                        startActivity(intenttoEmsg);

                }

            }
        });
    }


}
