package ca.cmpt276.walkinggroup.app;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

// get the example from https://github.com/codepath/android_guides/issues/220


public class LocationService extends Service {
    private String TAG = "LocationListener";
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private static final int LOCATION_TIME = 30000;

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
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_TIME, 100f, mLocationListener);
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
