package ca.cmpt276.walkinggroup.app;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.GpsLocation;
import ca.cmpt276.walkinggroup.dataobjects.Message;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

// get the example from https://github.com/codepath/android_guides/issues/220


public class LocationService extends Service {
    private String TAG = "LocationListener";
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private static final int LOCATION_TIME = 3000;
    private GpsLocation gpsLocation;
    private User user;
    private String token;
    private WGServerProxy proxy;
    private long userId;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        user = User.getInstance();
        SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref",0);
        token = dataToGet.getString("userToken","");
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
        userId = dataToGet.getLong("userId",0);
        gpsLocation = new GpsLocation();

        Call<User> caller_user = proxy.getUserById(userId);
        ProxyBuilder.callProxy(LocationService.this, caller_user, returnedUser -> response(returnedUser));

        Log.d(TAG, "onCreate");
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                    Intent intent = new Intent("UpdateLocation");
                    double tempLat = location.getLatitude();
                    double tempLng = location.getLongitude();
                    intent.putExtra("UpdateLat", tempLat);
                    intent.putExtra("UpdateLng", tempLng);
                    gpsLocation.setLat(tempLat);
                    gpsLocation.setLng(tempLng);
                    sendBroadcast(intent);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        };

        mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        if (mLocationManager != null) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_TIME, 0, mLocationListener);
        }
    }

    private void response(User returnedUser) {
        returnedUser.setLastGpsLocation(gpsLocation);

        Call<User> caller = proxy.editUserById(userId,returnedUser);
        Toast.makeText(this, ""+gpsLocation, Toast.LENGTH_SHORT).show();
        ProxyBuilder.callProxy(LocationService.this,caller,returnedUserForUpdate -> responseGps(returnedUserForUpdate));
    }


    private void responseGps(User returnedUser){
        Log.i(TAG,returnedUser.toString());
        Toast.makeText(this, ""+user.getLastGpsLocation(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "successful gps", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }
}
