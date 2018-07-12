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

import com.google.android.gms.maps.model.LatLng;

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

    double tempLat;
    double tempLng;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {

        Log.d(TAG, "onCreate");
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                    Intent intent = new Intent("UpdateLocation");
                    tempLat = location.getLatitude();
                    tempLng = location.getLongitude();
                    intent.putExtra("UpdateLat", tempLat);
                    intent.putExtra("UpdateLng", tempLng);

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


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }
}
