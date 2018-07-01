package ca.cmpt276.walkinggroup.app;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiActivity;

import ca.cmpt276.walkinggroup.dataobjects.User;

public class MainActivity extends AppCompatActivity {
    private String token;
    private String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref",0);
        token = dataToGet.getString("userToken","");
        Button btnLogout = (Button)findViewById(R.id.btnLogout);
        user = User.getInstance();
        if (token==""){
            Toast.makeText(MainActivity.this,"no token",Toast.LENGTH_LONG).show();
            btnLogout.setVisibility(View.GONE);
        }else{
            Toast.makeText(MainActivity.this,"has token, Email: "+user.getEmail(),Toast.LENGTH_LONG).show();
            btnLogout.setVisibility(View.VISIBLE);
        }

        setGroupBtn();
        setDevBtn();
        setLogoutBtn();
        setMonitoringBtn();
        setMonitorBtn();

        if (isServicesOK()){
            setMapButton();
        }
    }

    private void setMonitorBtn() {
        Button btnMonitor = (Button)findViewById(R.id.btnMonitor);
        btnMonitor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                switch (token){
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
        Button btnMonitoring = (Button)findViewById(R.id.btnMonitoring);
        btnMonitoring.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                switch (token){
                    case "":
                        Intent intentToLogin = LoginActivity.makeIntent(MainActivity.this);
                        startActivity(intentToLogin);
                        break;
                    default:
                       Intent intentMonitoring = MonitoringActivity.makeIntent(MainActivity.this);
                        startActivity(intentMonitoring);

                }

            }
        });
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS){
            // everything is fine and user can make map result
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            // an error occurred but we can resolve it
            Log.d(TAG, "isServicesOK: an error occurred but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else {
            Toast.makeText(this,"We can't make map requests",Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void setMapButton() {
        Button btn = findViewById(R.id.google_map);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,GoogleMapsActivity.class));
            }
        });
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, MainActivity.class);
    }
    private void setGroupBtn(){
        if (token==""){
            Toast.makeText(MainActivity.this,"no token",Toast.LENGTH_LONG).show();
        }
        Button btnDev = (Button)findViewById(R.id.btnGroup);
        btnDev.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                switch (token){
                    case "":
                        Intent intentToLogin = LoginActivity.makeIntent(MainActivity.this);
                        startActivity(intentToLogin);
                        break;
                    default:
                        Intent intent = UserinfoActivity.makeIntent(MainActivity.this);
                        startActivity(intent);

                }

            }
        });
    }

    private void setLogoutBtn(){
        Button btnLogout = (Button)findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                SharedPreferences dataToSave = getApplicationContext().getSharedPreferences("userPref",0);
                SharedPreferences.Editor PrefEditor = dataToSave.edit();
                PrefEditor.putString("userToken","");
                PrefEditor.apply();
                Toast.makeText(MainActivity.this,"Log out success",Toast.LENGTH_LONG).show();
                Intent intentToLogin = LoginActivity.makeIntent(MainActivity.this);
                startActivity(intentToLogin);
                finish();

            }
        });
    }

    private void setDevBtn(){
        Button btnDev = (Button)findViewById(R.id.btnDev);
        btnDev.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this,ServerTestActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
