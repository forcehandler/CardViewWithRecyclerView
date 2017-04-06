package com.example.abhiraj.cardviewwithrecyclerview.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

/**
 * Created by Abhiraj on 03-04-2017.
 */

public class MyBroadcast extends BroadcastReceiver {

    private static final String TAG = GpsCheckReceiver.class.getSimpleName();

    private boolean isGPSEnabled = false;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("MyBroadcast", "fired the new my broadcast");

        checkGpsStatus(context);
        Log.d(TAG, "returned form gps check status");
        if(isGPSEnabled){
            Log.d(TAG, "gsp is enabled if statement");
                Log.d(TAG, "gps is on");
            // if the gps is on recreate the geofence

        }
        else{
            Log.d(TAG, "gps is disabled if statement");
                Log.d(TAG, "gps is off");
        }
    }

    private void checkGpsStatus(Context context) {
        Log.d(TAG, "in gps check status");
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

        isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Log.d(TAG, isGPSEnabled + "");

        Log.d(TAG, "returning form gps check status");
    }
}

