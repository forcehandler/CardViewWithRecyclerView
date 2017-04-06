package com.example.abhiraj.cardviewwithrecyclerview.geofencing;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.abhiraj.cardviewwithrecyclerview.BuildConfig;
import com.example.abhiraj.cardviewwithrecyclerview.Constants;
import com.example.abhiraj.cardviewwithrecyclerview.R;
import com.example.abhiraj.cardviewwithrecyclerview.ui.HomeDrawerActivity;

/**
 * Created by Abhiraj on 12-03-2017.
 */

public class StepListener extends Service implements SensorEventListener {

    private static final String TAG = StepListener.class.getSimpleName();
    private static int steps;

    private static NotificationCompat.Builder notificationBuilder;

    private static PowerManager powerManager;
    private static PowerManager.WakeLock wakeLock;


    private final IBinder mBinder = new StepBinder();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class StepBinder extends Binder {
        public StepListener getService() {
            // Return this instance of LocalService so clients can call public methods
            return StepListener.this;
        }
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // Check for false values

        if(sensorEvent.values[0] > Integer.MAX_VALUE)
        {
            if (BuildConfig.DEBUG)
                Log.d(TAG, "probably a wrong value " + sensorEvent.values[0]);
            return;
        }

        else
        {
            //steps = (int) sensorEvent.values[0];
            if (BuildConfig.DEBUG)
                Log.d(TAG, "no of steps received " + steps);

            steps++;
            //publishSteps();
            //updateSteps(steps);

        }
    }

    private void updateSteps(int steps) {
        SharedPreferences sharedPref = getSharedPreferences(Constants
                .SharedPreferences.STEPS_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(Constants.SharedPreferences.STEPS, steps);
        editor.commit();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onCreate()
    {
        Log.d(TAG, "onCreate of StepListener");
        Log.d(TAG, "onCreate");
        super.onCreate();
        if(BuildConfig.DEBUG)
            Log.d(TAG, "StepListener Oncreate");

        //reRegisterSensor();

        // prevent service from stopping when the phone goes to deep sleep in api >= 23
        ignoreDozeOptimization();

        SharedPreferences sharedPref = getSharedPreferences(Constants.SharedPreferences.STEPS_FILE, Context.MODE_PRIVATE);
        steps = 0;      // Whenever the service is created it starts off with 0 steps.

        if(BuildConfig.DEBUG)
            Log.d(TAG, "steps from sharedPref " + steps);

        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag");
        wakeLock.acquire();

        if(BuildConfig.DEBUG)
            Log.d(TAG, "acquired partial wakelock");

        notificationBuilder = new NotificationCompat.Builder(this);

        // Register broadcast receiver for gps state change.
        /*gpsCheckReceiver = new GpsCheckReceiver();
        Log.d(TAG, "registering gps check broadcast receiver");
        registerReceiver(gpsCheckReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));*/

    }

    @Override
    public void onDestroy()
    {

        if(BuildConfig.DEBUG)
            Log.d(TAG, "StepListener onDestroy");
        unRegisterSensor();
        releaseWakeLock();
        /*if(gpsCheckReceiver != null){
            Log.d(TAG, "unregistering gps check broadcast receiver");
            unregisterReceiver(gpsCheckReceiver);
        }*/
        super.onDestroy();

    }

    public void unRegisterSensor()
    {
        if(BuildConfig.DEBUG)
            Log.d(TAG, "un-register sensor listener");
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        try{
            sm.unregisterListener(this);
        } catch (Exception e){
            if(BuildConfig.DEBUG) Log.d(TAG, "error in un registering sensor listener");
            e.printStackTrace();
        }
    }

    public void reRegisterSensor()
    {
        if(BuildConfig.DEBUG)
            Log.d(TAG, "re-register sensor listener");

        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);

        unRegisterSensor();

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "step sensors: " + sm.getSensorList(Sensor.TYPE_STEP_COUNTER).size());
            if (sm.getSensorList(Sensor.TYPE_STEP_COUNTER).size() < 1) return; // emulator
            Log.d(TAG, "default: " + sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER).getName());
        }

        boolean hasStepCounter = getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER);
        if(hasStepCounter)
        {
            if(BuildConfig.DEBUG)
            {
                Log.d(TAG, "Ya'ay your device supports step counter");
            }

        }
        else
        {
            if(BuildConfig.DEBUG)
            {
                Log.d(TAG, "Your device does not support step counter");
            }
        }
        // Enable batching with delay of max  1 second
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
                    SensorManager.SENSOR_DELAY_NORMAL, 1000);
        }
        else
        {
            if(BuildConfig.DEBUG)
                Log.d(TAG, "You need to have device >= kitkat");
        }

    }

    private void publishSteps()
    {
        if(BuildConfig.DEBUG)
            Log.d(TAG, "publish steps");
        Intent intent = new Intent(Constants.Broadcasts.BROADCAST_STEPS);
        intent.putExtra(Constants.Broadcasts.STEPS, steps+"");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        //sendNotification(steps+"");
    }

    public static void releaseWakeLock()
    {
        if(wakeLock != null) {
            if(wakeLock.isHeld()) {     //required to avoid crash, sometimes wakelock is not held and
                                        // the internal reference counter goes negative causing the crash
                wakeLock.release();
            }
        }
    }


    public void sendNotification(String title, String msg ) {
        Log.i(TAG, "sendNotification: " + msg );

        // Intent to start the main Activity
        Intent notificationIntent = new Intent(getApplicationContext(), HomeDrawerActivity.class);


        PendingIntent notificationPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Creating and sending Notification
        NotificationManager notificationMng =
                (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );

        Notification notification = createNotification(title, msg, notificationPendingIntent);
        notificationMng.notify(
                1337,
                notification);

        startForeground(1337, notification);

    }

    private Notification createNotification(String title, String msg, PendingIntent notificationPendingIntent) {

        notificationBuilder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(Color.RED)
                .setContentTitle(title)
                .setContentText(msg)
                .setContentIntent(notificationPendingIntent)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setAutoCancel(true);
        return notificationBuilder.build();
    }

    private void ignoreDozeOptimization() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        String packageName = getPackageName();
        if (Build.VERSION.SDK_INT >= 23 && !pm.isIgnoringBatteryOptimizations(packageName)) {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + packageName));
        }
    }

}
