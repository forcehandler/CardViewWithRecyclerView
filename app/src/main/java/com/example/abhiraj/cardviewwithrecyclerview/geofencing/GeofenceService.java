package com.example.abhiraj.cardviewwithrecyclerview.geofencing;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
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

    private static double latitude;
    private static double longitude;
    private static float radius;

    private static MallGeoFence fence;

    private IBinder mBinder = new GeofenceBinder();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate(){
        Log.d(TAG, "onCreate");
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mGPSStateChangeBroadcast, new IntentFilter(Constants.Location.GPS_STATE_ON_BROADCAST));
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mGPSStateChangeBroadcast, new IntentFilter(Constants.Location.GPS_STATE_ON_BROADCAST));
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mGeofenceEnterBroadcast, new IntentFilter(Constants.Geofence.GEOFENCE_ENTER_BROADCAST));
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mGeofenceEnterBroadcast, new IntentFilter(Constants.Geofence.GEOFENCE_EXIT_BROADCAST));
        super.onCreate();
    }

    public class GeofenceBinder extends Binder {
        public GeofenceService getService() {
            // Return this instance of LocalService so clients can call public methods
            return GeofenceService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        Log.d(TAG, "onstart command");

        latitude = intent.getDoubleExtra("latitude", 0.0);
        longitude = intent.getDoubleExtra("longitude", 0.0);
        radius = intent.getFloatExtra("radius", 200.0f);

        fence = new MallGeoFence(latitude, longitude, radius);
        enableLocationUpdates();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy(){

        if(mBound){
            unbindService(mStepConnection);
        }
        stopPedometerService();
        googleApiClient.disconnect();

        super.onDestroy();
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
            Log.d(TAG, "google api client was null");
            googleApiClient = new GoogleApiClient.Builder( this )
                    .addConnectionCallbacks( this )
                    .addOnConnectionFailedListener( this )
                    .addApi( LocationServices.API )
                    .build();
        }
        if(googleApiClient.isConnected())
        {
            if(BuildConfig.DEBUG) Log.d(TAG, "already connected to geofenceGoogleApiClient");
            /*sendAPIConnectedBroadcast();*/
        }
        if(!googleApiClient.isConnected()){Log.d(TAG, "not connected to googelapiclient");}
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

            // assuminng that the gps is already on coz we check for it before starting the service

            startPedometerService();
            bindStepService();
        }
        else{
            //askPermission();          // TODO: check the effect of this statement
        }
        /*sendAPIConnectedBroadcast();*/
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

           /* Intent intent = new Intent();
            intent.setAction("GeofenceCreated");
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);*/


            // when the geofence is created without loitering, check for user's presence in mall
            if(!shouldAllowLoitering()){
                Log.d(TAG, "loitering is not allowed so check if user is in valid mall");
                checkIfUserIsInValidMall();
            }
            else if(shouldAllowLoitering()){
                if(mBound){
                    stepListener.reRegisterSensor();
                    Log.d(TAG, "issuing earning notification after starting step counter");
                    stepListener.sendNotification("Earning", "Keep " +
                            "exploring to earn coupons");
                    isEarningSessionInProgress = true;

                    sendUiUpdateBroadcast(true);
                }
            }

        } else {
            // inform about fail
            if(BuildConfig.DEBUG){
                Log.d(TAG, "could not create the geofence");
            }
        }
    }

    private void sendUiUpdateBroadcast(boolean isEarning) {

        Intent fabButtonIconIntent = new Intent();
        if(isEarning){
            Log.d(TAG, "sending is earning icon broadcast");
            fabButtonIconIntent.setAction(Constants.Geofence.SHOW_EARNING_ICON);
            LocalBroadcastManager.getInstance(getApplicationContext())
                    .sendBroadcast(fabButtonIconIntent);
        }
        else{
            Log.d(TAG, "sending default icon broadcast");
            fabButtonIconIntent.setAction(Constants.Geofence.SHOW_DEFAULT_ICON);
            LocalBroadcastManager.getInstance(getApplicationContext())
                    .sendBroadcast(fabButtonIconIntent);
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


    //------------------------------====================================================================================

    // Code from the home activity

    private StepListener stepListener;
    private boolean isEarningSessionInProgress = false;           // TODO: testing phase hence true, create a function to determine user's presence
    // in the geofence

    private boolean isEarningPaused = false;

    private boolean isInsideGeofence = false;

    private boolean mBound = false;


    /*private BroadcastReceiver mAPIConnectedBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BuildConfig.DEBUG) Log.d(TAG, "received api connect broadcast");

            // give base activity the instance of googleApiClient form geofence service
            // googleApiClient is not null becouse this broadcast is received after the geofenceService is connected
            geofenceGoogleApiClient = GeofenceService.googleApiClient;
            // check the gps status

            locationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(UPDATE_INTERVAL)
                    .setFastestInterval(FASTEST_INTERVAL);
            if(checkPermission()){
                checkGpsSettings(locationRequest);
            }

            // check if gps is already on coz it will not trigger gpsCheckReceiver
            LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

            if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                // start service(do not start counting steps now)
                startPedometerService();
                // bind to the service
                bindStepService();
            }
            *//*Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MallGeoFence mallGeoFence = getGeofenceDetails();
                    startGeofence(mallGeoFence, shouldAllowLoitering());      // api connect implies user
                }
            }, 3000);*//*

        }
    };*/

    private boolean shouldAllowLoitering() {

        if(isInsideGeofence){          // if the user is earning right now then allow loitering otherwise don't
            return true;
        }
        else{
            return false;
        }
    }


   /* private BroadcastReceiver mGeofenceCreatedBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Geofence created broadcast received");

            // when the geofence is created without loitering, check for user's presence in mall
            if(!shouldAllowLoitering()){
                Log.d(TAG, "loitering is not allowed so check if user is in valid mall");
                checkIfUserIsInValidMall();
            }
            else if(shouldAllowLoitering()){
                if(mBound){
                    stepListener.reRegisterSensor();
                    Log.d(TAG, "issuing earning notification after starting step counter");
                    stepListener.sendNotification("Earning", "Keep " +
                            "exploring to earn coupons");
                    isEarningSessionInProgress = true;
                }
            }
        }
    };*/

    private BroadcastReceiver mGeofenceEnterBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BuildConfig.DEBUG) Log.d(TAG, "received geofence enter broadcast");

            if (intent.getAction().equals(Constants.Geofence.GEOFENCE_ENTER_BROADCAST)) {
                isInsideGeofence = true;
                isEarningSessionInProgress = true;
                //mFloatingActionButton.setImageResource(R.drawable.ic_favorite_white_24dp);

            }
            else if (intent.getAction().equals(Constants.Geofence.GEOFENCE_EXIT_BROADCAST)) {
                isInsideGeofence = false;
                isEarningSessionInProgress = false;
                //mFloatingActionButton.setImageResource(R.drawable.ic_search_white_48dp);
                clearGeofence();
                if(mBound){
                    unbindService(mStepConnection);
                }
                stopPedometerService();
                stopSelf();
            }
        }
    };

    private BroadcastReceiver mGPSStateChangeBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d(TAG, "received gps state change broadcast");
            if(intent.getAction().equals(Constants.Location.GPS_STATE_ON_BROADCAST)){

                enableLocationUpdates();

            }

            else if(intent.getAction().equals(Constants.Location.GPS_STATE_OFF_BROADCAST)){
                isInsideGeofence = false;
                Log.d(TAG, "Gps turned off broadcast received");


                    Log.d(TAG, "user was earning when the gps was turned off");
                    // if the user is earning when the gps outage occurs, display a notificaiton
                    // informing user to turn on the gps, and meanwhile unregister the step sensor
                    // for the steplistener service.
                    if(mBound) {
                        Log.d(TAG, "sending earning paused notification and unregistering step sensor");
                        if(isEarningSessionInProgress){
                            stepListener.sendNotification("Earning Paused", "Please turn on the gps");
                        }
                        stepListener.unRegisterSensor();
                        unbindService(mStepConnection);
                    }

            }
        }
    };

    private void bindStepService(){
        Intent stepIntent = new Intent(getApplicationContext(), StepListener.class);
        bindService(stepIntent, mStepConnection, Context.BIND_AUTO_CREATE);
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mStepConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {

            if(BuildConfig.DEBUG) Log.d(TAG, "bound to the step listener service");
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            StepListener.StepBinder binder = (StepListener.StepBinder) service;
            stepListener = binder.getService();
            mBound = true;

            startGeofenceAccordingToUserState();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    private void checkIfUserIsInValidMall(){

        if(isInsideGeofence){
            Log.d(TAG, "user is in valid mall instantly");
            startGeofenceAccordingToUserState();
        }
        else{

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(isInsideGeofence){
                        Log.d(TAG, "inside valid mall after 8 second check");
                        startGeofenceAccordingToUserState();
                    }

                    else{
                        Log.d(TAG, "In invalid mall please select A VALID MALL");
                        if(mBound){
                            unbindService(mStepConnection);
                        }
                        stopPedometerService();
                        googleApiClient.disconnect();
                        sendUiUpdateBroadcast(false);
                    }
                }
            }, 8000);
        }
    }
    private void startGeofenceAccordingToUserState()
    {
        // if user is in the mall create geofence with loitering
        if(isInsideGeofence){
            clearGeofence();
            Log.d(TAG, "inside geofence, creating loitering geofence");
            startGeofence(fence, true);
        }
        else{
            Log.d(TAG, "outside geofence,  creating non loitering geofence");
            startGeofence(fence, false);
        }
    }

    private void startPedometerService() {
        if(BuildConfig.DEBUG)
            Log.d(TAG, "start pedometer");
        startService(new Intent(getApplicationContext(), StepListener.class));
    }

    private void stopPedometerService() {
        if(BuildConfig.DEBUG) {
            Log.d(TAG, "stop pedometer");
            Log.d(TAG, "releasing wakelock");
        }
        StepListener.releaseWakeLock();
        stopService(new Intent(getApplicationContext(), StepListener.class));
    }
}
