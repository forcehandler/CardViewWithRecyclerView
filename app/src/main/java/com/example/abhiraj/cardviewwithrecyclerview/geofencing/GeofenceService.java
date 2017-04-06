package com.example.abhiraj.cardviewwithrecyclerview.geofencing;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.abhiraj.cardviewwithrecyclerview.BuildConfig;
import com.example.abhiraj.cardviewwithrecyclerview.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Abhiraj on 06-04-2017.
 */

public class GeofenceService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, ResultCallback<Status>{

    private static String TAG = GeofenceService.class.getSimpleName();

    private static Geofence sGeofence;
    public static GoogleApiClient googleApiClient;

    private IBinder mBinder = new GeofenceBinder();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate(){
        Log.d(TAG, "onCreate");
        super.onCreate();
    }

    public class GeofenceBinder extends Binder {
        public GeofenceService getService() {
            // Return this instance of LocalService so clients can call public methods
            return GeofenceService.this;
        }
    }


//--------------------------------------------------------------------------------------------------------------======================
    public void enableLocationUpdates(){
        // Call GoogleApiClient connection when starting the Activity
        Log.d(TAG, "enabling location updates and creating google api");
        createGoogleApi();
        if(googleApiClient != null) {
            googleApiClient.connect();
        }

    }

    // Create GoogleApiClient instance
    // gets called once in onCreate of homedraweractivity activity
    // removed the call from enable location update to avoid possibility of creation of multiple
    // instances of apiClient
    public void createGoogleApi() {
        Log.d(TAG, "createGoogleApi()");
        if ( googleApiClient == null ) {
            googleApiClient = new GoogleApiClient.Builder( this )
                    .addConnectionCallbacks( this )
                    .addOnConnectionFailedListener( this )
                    .addApi( LocationServices.API )
                    .build();
        }
        if(googleApiClient.isConnected())
        {
            if(BuildConfig.DEBUG) Log.d(TAG, "already connected to geofenceGoogleApiClient");
            sendAPIConnectedBroadcast();
        }
    }

    private void sendAPIConnectedBroadcast() {
        Intent intent = new Intent();
        intent.setAction("APIConnected");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if(BuildConfig.DEBUG) Log.d(TAG, "connected to the location services");
        if(checkPermission()){
            startLocationUpdates();
        }
        else{
            //askPermission();          // TODO: check the affect of this statement
        }
        sendAPIConnectedBroadcast();
        // GPS check will occur only if GOOGLE API is connected.
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "api Connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "api Connection failed");
    }


    //---------------------------------------------------------------------------------------------

    // Check for permission to access Location
    public boolean checkPermission() {
        Log.d(TAG, "checkPermission()");
        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED );
    }

    // Asks for permission


    // Verify user's response of the permission requested


    // App cannot work without the permissions
    private void permissionsDenied() {
        Log.w(TAG, "permissionsDenied()");
        // TODO close app and warn user
    }

    // Check if the GPS is turned on
   /* public void checkGpsSettings(LocationRequest locationRequest) {

        if(BuildConfig.DEBUG)  Log.d(TAG, "Checking GPS settings");

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true);

        // Check whether the current location settings are satisfied
        PendingResult<LocationSettingsResult> result = LocationServices
                .SettingsApi.checkLocationSettings(geofenceGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result)
            {
                final Status status = result.getStatus();
                final LocationSettingsStates states = result.getLocationSettingsStates();

                switch (status.getStatusCode())
                {
                    case LocationSettingsStatusCodes.SUCCESS:
                        if(BuildConfig.DEBUG)  Log.d(TAG, "GPS is already enabled");
                        //sendGPSEnabledBroadcast();
                        break;

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        if(BuildConfig.DEBUG)  Log.d(TAG, "Need to turn GPS on");
                        try
                        {
                            status.startResolutionForResult(
                                    BaseActivity.this, Constants.REQUEST_CHECK_LOCATION_SETTINGS);
                        } catch (IntentSender.SendIntentException e){}
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Toast.makeText(BaseActivity.this, "Cannot change GPS settings", Toast.LENGTH_SHORT)
                                .show();
                        break;
                }
            }
        });
    }*/


    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(BuildConfig.DEBUG)
            Log.d(TAG, "onActivityResult() called with: " + "requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");

        switch (requestCode)
        {
            case Constants.REQUEST_CHECK_LOCATION_SETTINGS:
                if(resultCode == Activity.RESULT_OK)
                {
                    Log.d(TAG, "gps enabled, sending broadcast");
                    //sendGPSEnabledBroadcast();
                }
                else{
                    //user has denied turning on the gps, show the user reason to use gps.
                    // 1. Instantiate an AlertDialog.Builder with its constructor
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    // 2. Chain together various setter methods to set the dialog characteristics
                    builder.setMessage(R.string.GPS_required_message)
                            .setTitle(R.string.GPS_required_title);

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                            checkGpsSettings(locationRequest);      // since gpsCheck has been called
                            // before => that the locationRequest object which is a class object is
                            // ready for use and hence we can supply the object to the gpsCheck.
                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                            Log.d(TAG, "gps enable clarification denied");
                        }
                    });
                    // 3. Get the AlertDialog from create()
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                break;


        }

    }*/

    // GPs state listener for gps state change
    /*private void sendGPSEnabledBroadcast() {

        Intent intent = new Intent();
        intent.setAction(Constants.Location.GPS_ENABLED);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }*/


    //---------------------------------------------------------------------------------------------


    // Start location updates

    private LocationRequest locationRequest;
    // Defined in milli seconds.
    // This number in extremely low, and should be used only for debug
    private final int UPDATE_INTERVAL =  1000;     // update every 1 second
    private final int FASTEST_INTERVAL = 500;     // if available then check after every 0.5 second

    // Start location Updates
    private void startLocationUpdates(){
        Log.i(TAG, "startLocationUpdates()");
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        //checkGpsSettings(locationRequest);        //remove location updates
        // already checking gps after api connect in home class

        if ( checkPermission() )
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if(BuildConfig.DEBUG) {

            Log.d(TAG, "Location accuracy " + location.getAccuracy());
        }

    }



    //---------------------------------------------------------------------------------------------

    // Start Geofence creation process
    public void startGeofence(MallGeoFence mMallGeoFence, boolean allowLoitering) {
        Log.i(TAG, "startGeofence()");
        if( mMallGeoFence != null ) {
            if(sGeofence == null) {
                sGeofence = createGeofence(mMallGeoFence, allowLoitering);
            }
            else{
                clearGeofence();
                sGeofence = createGeofence(mMallGeoFence, allowLoitering);
            }
            GeofencingRequest geofenceRequest = createGeofenceRequest( sGeofence );
            addGeofence( geofenceRequest );
        } else {
            Log.e(TAG, "Geofence marker is null");
        }
    }

    private static final long GEO_DURATION = 3 * 60 * 60 * 1000;    // Geo fence expiration duration is set as 3 hr


    // Create a Geofence
    private Geofence createGeofence(MallGeoFence geoFence, boolean allowLoitering ) {
        Log.d(TAG, "createGeofence");
        if(!allowLoitering) {
            return new Geofence.Builder()
                    .setRequestId(Constants.Geofence.GEOFENCE_REQUEST_ID)
                    .setCircularRegion(geoFence.getLatitude(), geoFence.getLongitude(), geoFence.getRadius())
                    .setExpirationDuration(GEO_DURATION)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                            | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();
        }
        else{
            return new Geofence.Builder()
                    .setRequestId(Constants.Geofence.GEOFENCE_REQUEST_ID)
                    .setCircularRegion(geoFence.getLatitude(), geoFence.getLongitude(), geoFence.getRadius())
                    .setExpirationDuration(GEO_DURATION)
                    .setLoiteringDelay(5*60*1000)                          // let the user loiter for 5 minutes
                    .setNotificationResponsiveness(3*60*1000)           // check every 3 minutes
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                            | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();
        }
    }

    // Create a Geofence Request
    private GeofencingRequest createGeofenceRequest( Geofence geofence ) {
        Log.d(TAG, "createGeofenceRequest");
        return new GeofencingRequest.Builder()
                .setInitialTrigger( GeofencingRequest.INITIAL_TRIGGER_ENTER )
                .addGeofence( geofence )
                .build();
    }

    private PendingIntent geoFencePendingIntent;
    private final int GEOFENCE_REQ_CODE = 0;
    private PendingIntent createGeofencePendingIntent() {
        Log.d(TAG, "createGeofencePendingIntent");
        if ( geoFencePendingIntent != null )
            return geoFencePendingIntent;

        Intent intent = new Intent( this, GeofenceTrasitionService.class);
        return PendingIntent.getService(
                this, GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT );
    }

    // Add the created GeofenceRequest to the device's monitoring list
    private void addGeofence(GeofencingRequest request) {
        Log.d(TAG, "addGeofence");
        if (checkPermission())
            LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    request,
                    createGeofencePendingIntent()
            ).setResultCallback(this);
    }

    @Override
    public void onResult(@NonNull Status status) {
        Log.i(TAG, "onResult: " + status);
        if ( status.isSuccess() ) {
            if(BuildConfig.DEBUG) Log.d(TAG, "Successfully created the geofence");
            //saveGeofence();
            //drawGeofence();
            Intent intent = new Intent();
            intent.setAction("GeofenceCreated");
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        } else {
            // inform about fail
            if(BuildConfig.DEBUG){
                Log.d(TAG, "could not create the geofence");
            }
        }
    }

    // Clear Geofence
    public void clearGeofence() {
        Log.d(TAG, "clearGeofence()");
        LocationServices.GeofencingApi.removeGeofences(
                googleApiClient,
                createGeofencePendingIntent()
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if ( status.isSuccess() ) {
                    // remove drawing
                    if(BuildConfig.DEBUG) Log.d(TAG, "Successfully removed the geo fence");
                }
            }
        });
        // Stop pedometer service

    }
}
