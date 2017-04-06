package com.example.abhiraj.cardviewwithrecyclerview.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.abhiraj.cardviewwithrecyclerview.Constants;

/**
 * Created by Abhiraj on 03-04-2017.
 */

public class GpsCheckReceiver extends BroadcastReceiver {

    private static final String TAG = GpsCheckReceiver.class.getSimpleName();

    private boolean isGPSEnabled = false;
    @Override
    public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Gps check broadcast received");
        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            // Make an action or refresh an already managed state.

            checkGpsStatus(context);

            if(isGPSEnabled){
                Log.d(TAG, "gps is on");
                Intent gpsIntent = new Intent();
                gpsIntent.setAction(Constants.Location.GPS_STATE_ON_BROADCAST);
                LocalBroadcastManager.getInstance(context).sendBroadcast(gpsIntent);
            }
            else{
                Log.d(TAG, "gps is off");
                Intent gpsIntent = new Intent();
                gpsIntent.setAction(Constants.Location.GPS_STATE_OFF_BROADCAST);
                LocalBroadcastManager.getInstance(context).sendBroadcast(gpsIntent);
            }
        }

    }

    private void checkGpsStatus(Context context) {
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

        isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
