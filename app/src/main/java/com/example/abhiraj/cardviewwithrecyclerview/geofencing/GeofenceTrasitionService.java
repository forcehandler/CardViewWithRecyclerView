package com.example.abhiraj.cardviewwithrecyclerview.geofencing;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.example.abhiraj.cardviewwithrecyclerview.BuildConfig;
import com.example.abhiraj.cardviewwithrecyclerview.R;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abhiraj on 12-03-2017.
 */

public class GeofenceTrasitionService extends IntentService {

    private static final String TAG = GeofenceTrasitionService.class.getSimpleName();

    public static final int GEOFENCE_NOTIFICATION_ID = 0;

    public GeofenceTrasitionService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(BuildConfig.DEBUG)
        {
            Log.d(TAG, "onHandleIntent");
        }
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        // Handling errors
        if ( geofencingEvent.hasError() ) {
            String errorMsg = getErrorString(geofencingEvent.getErrorCode() );
            Log.e( TAG, errorMsg );
            return;
        }

        int geoFenceTransition = geofencingEvent.getGeofenceTransition();
        // Check if the transition type is of interest
        if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT ) {
            // Get the geofence that were triggered
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            String geofenceTransitionDetails = getGeofenceTrasitionDetails(geoFenceTransition, triggeringGeofences );

            // Send notification details as a String
            sendNotification( geofenceTransitionDetails );
        }
    }


    private String getGeofenceTrasitionDetails(int geoFenceTransition, List<Geofence> triggeringGeofences) {

        if(BuildConfig.DEBUG)
            Log.d(TAG, "getGeofenceTransitionDetails");
        // get the ID of each geofence triggered
        ArrayList<String> triggeringGeofencesList = new ArrayList<>();
        for ( Geofence geofence : triggeringGeofences ) {
            triggeringGeofencesList.add( geofence.getRequestId() );
        }

        String status = null;
        if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ) {
            status = "Entering ";

            if(BuildConfig.DEBUG)
                Log.d(TAG, "Transition enter");
            // Start measuring steps, Initiate the pedometer service
            // TODO: Add dwell time before starting and stopping pedometer service
            startPedometer();
        }
        else if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT ) {
            status = "Exiting ";

            if(BuildConfig.DEBUG)
                Log.d(TAG, "Transition exit");
            // Stop pedometer service
            stopPedometer();
        }
        return status + TextUtils.join( ", ", triggeringGeofencesList);
    }

    private void startPedometer() {
        if(BuildConfig.DEBUG)
            Log.d(TAG, "start pedometer");
        startService(new Intent(getApplicationContext(), StepListener.class));
    }

    private void stopPedometer() {
        if(BuildConfig.DEBUG) {
            Log.d(TAG, "stop pedometer");
            Log.d(TAG, "releasing wakelock");
        }
        StepListener.releaseWakeLock();
        stopService(new Intent(getApplicationContext(), StepListener.class));
    }

    private void sendNotification( String msg ) {
        Log.i(TAG, "sendNotification: " + msg );

        // Intent to start the main Activity
        Intent notificationIntent = Geofencing.makeNotificationIntent(
                getApplicationContext(), msg
        );

        // TODO: Notification leads to the geofencing class,
        // Redirect it to the corresponding coupon and build back stack
        // Needs min API 16.

        /*TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(Geofencing.class);
        stackBuilder.addNextIntent(notificationIntent);*/

        PendingIntent notificationPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Creating and sending Notification
        NotificationManager notificationMng =
                (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
        notificationMng.notify(
                GEOFENCE_NOTIFICATION_ID,
                createNotification(msg, notificationPendingIntent));

    }

    // Create notification
    private Notification createNotification(String msg, PendingIntent notificationPendingIntent) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(Color.RED)
                .setContentTitle(msg)
                .setContentText("Geofence Notification!")
                .setContentIntent(notificationPendingIntent)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setAutoCancel(true);
        return notificationBuilder.build();
    }


    private static String getErrorString(int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "GeoFence not available";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Too many GeoFences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "Too many pending intents";
            default:
                return "Unknown error.";
        }
    }
}
