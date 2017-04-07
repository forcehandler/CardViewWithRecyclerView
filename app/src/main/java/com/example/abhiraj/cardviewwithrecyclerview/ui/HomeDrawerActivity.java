package com.example.abhiraj.cardviewwithrecyclerview.ui;

import android.app.Activity;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.abhiraj.cardviewwithrecyclerview.BaseActivity;
import com.example.abhiraj.cardviewwithrecyclerview.BuildConfig;
import com.example.abhiraj.cardviewwithrecyclerview.Constants;
import com.example.abhiraj.cardviewwithrecyclerview.Godlike;
import com.example.abhiraj.cardviewwithrecyclerview.R;
import com.example.abhiraj.cardviewwithrecyclerview.fragments.BottomNavOfferFragment;
import com.example.abhiraj.cardviewwithrecyclerview.fragments.OfferSkyOfferFragment;
import com.example.abhiraj.cardviewwithrecyclerview.fragments.SearchFragment;
import com.example.abhiraj.cardviewwithrecyclerview.fragments.ShopFragment;
import com.example.abhiraj.cardviewwithrecyclerview.geofencing.GeofenceService;
import com.example.abhiraj.cardviewwithrecyclerview.geofencing.MallGeoFence;
import com.example.abhiraj.cardviewwithrecyclerview.geofencing.StepListener;
import com.example.abhiraj.cardviewwithrecyclerview.models.Mall;
import com.example.abhiraj.cardviewwithrecyclerview.write.AddToFirebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

public class HomeDrawerActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
         SearchFragment.OnFragmentInteractionListener, ShopFragment.OffersFragmentListener, NavigationView.OnNavigationItemSelectedListener, BottomNavOfferFragment.OnFragmentInteractionListener, OfferSkyOfferFragment.OnFragmentInteractionListener, SearchView.OnQueryTextListener, View.OnClickListener {

    private static final String TAG = HomeDrawerActivity.class.getSimpleName();
    private Toolbar mToolbar;
    //private TabLayout mTabLayout;
    //private ViewPager mViewPager;
    private FloatingActionButton mFloatingActionButton;
    FragmentTransaction fragmentTransaction;
    // private HomeDrawerActivity.MyFragmentPagerAdapter myFragmentPagerAdapter;

    private StepListener stepListener;

    private GeofenceService mGeofenceService;

    private MenuItem food_clothes_select_menu;

    NavigationView navigationView;

    private DrawerLayout drawer;

    private ActionBarDrawerToggle toggle;

    private static String mallId;

    boolean doubleBackToExitPressedOnce = false;

    boolean isDrawerEnabled = false;

    private boolean isEarningSessionInProgress = false;           // TODO: testing phase hence true, create a function to determine user's presence
                                                // in the geofence

    private boolean isEarningPaused = false;

    private boolean isInsideGeofence = false;

    private boolean mBound = false;

    private boolean isUserWillingToEarn = false;

    private boolean isGpsTurnedOnForTheFirstTime = true;

    // Defined in milli seconds.
    // This number in extremely low, and should be used only for debug
    private final int UPDATE_INTERVAL =  1000;     // update every 1 second
    private final int FASTEST_INTERVAL = 500;     // if available then check after every 0.5 second
    private LocationRequest locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    .setInterval(UPDATE_INTERVAL)
    .setFastestInterval(FASTEST_INTERVAL);

    private BroadcastReceiver mAPIConnectedBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BuildConfig.DEBUG) Log.d(TAG, "received api connect broadcast");

            // give base activity the instance of googleApiClient form geofence service
            // googleApiClient is not null becouse this broadcast is received after the geofenceService is connected
            geofenceGoogleApiClient = GeofenceService.googleApiClient;
            // check the gps status

            if(checkPermission()){
                checkGpsSettings(locationRequest);
            }

            // check if gps is already on coz it will not trigger gpsCheckReceiver
            LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

            if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                // start service(do not start counting steps now)
                startPedometerService();
                // bind to the service
               /* bindStepService();*/
            }
            /*Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MallGeoFence mallGeoFence = getGeofenceDetails();
                    startGeofence(mallGeoFence, shouldAllowLoitering());      // api connect implies user
                }
            }, 3000);*/

        }
    };

    private boolean shouldAllowLoitering() {

        if(isInsideGeofence){          // if the user is earning right now then allow loitering otherwise don't
            return true;
        }
        else{
            return false;
        }
    }


    private BroadcastReceiver mGeofenceCreatedBroadcast = new BroadcastReceiver() {
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
    };

    private BroadcastReceiver mGeofenceEnterBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BuildConfig.DEBUG) Log.d(TAG, "received geofence enter broadcast");

            if (intent.getAction().equals(Constants.Geofence.GEOFENCE_ENTER_BROADCAST)) {
                isInsideGeofence = true;
                isEarningSessionInProgress = true;
                mFloatingActionButton.setImageResource(R.drawable.ic_favorite_white_24dp);

            }
            else if (intent.getAction().equals(Constants.Geofence.GEOFENCE_EXIT_BROADCAST)) {
                isInsideGeofence = false;
                isEarningSessionInProgress = false;
                mFloatingActionButton.setImageResource(R.drawable.ic_search_white_48dp);
                mGeofenceService.clearGeofence();

                stopPedometerService();
            }
        }
    };

    /*private BroadcastReceiver mGPSStateChangeBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d(TAG, "received gps state change broadcast");
            if(intent.getAction().equals(Constants.Location.GPS_STATE_ON_BROADCAST)){
                *//*if(isUserWillingToEarn){

                    Log.d(TAG, "the earning was paused, now gps is on so begin earning");
                    // if the earning was paused due to gps receiver outage,
                    // or if the gps was turned on then reconnect to the api
                    // which will then start geofence accordingly
                    mGeofenceService.enableLocationUpdates();

                    //check if the person is still in the geofence
                    // if(isUserInGeofence())
                    *//**//*if(mBound){
                        Log.d(TAG, "issuing earning notification");
                        stepListener.sendNotification("Earning", "keep exploring to earn coupons");
                        isEarningPaused = false;
                    }*//**//*

                }*//*

                if(isUserWillingToEarn && isGpsTurnedOnForTheFirstTime){
                    // start the main service
                    startGeofenceService();
                    isGpsTurnedOnForTheFirstTime = false;
                }

            }

            *//*else if(intent.getAction().equals(Constants.Location.GPS_STATE_OFF_BROADCAST)){
                isInsideGeofence = false;
                Log.d(TAG, "Gps turned off broadcast received");
                if(isEarningSessionInProgress){

                    Log.d(TAG, "user was earning when the gps was turned off");
                    // if the user is earning when the gps outage occurs, display a notificaiton
                    // informing user to turn on the gps, and meanwhile unregister the step sensor
                    // for the steplistener service.
                    if(mBound) {
                        Log.d(TAG, "sending earning paused notification and unregistering step sensor");
                        stepListener.sendNotification("Earning Paused", "Please turn on the gps");
                        stepListener.unRegisterSensor();
                        isEarningPaused = true;
                    }

                }
            }*//*
        }
    };*/

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

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mGeofenceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {

            if(BuildConfig.DEBUG) Log.d(TAG, "bound to the geofence listener service");
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            GeofenceService.GeofenceBinder binder = (GeofenceService.GeofenceBinder) service;
            mGeofenceService = binder.getService();

            // when connected to the geofence service start working on it
            mGeofenceService.enableLocationUpdates();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(TAG, "disconnected to geofence service");
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
                        Log.d(TAG, "inside valid mall after 5 second check");
                        startGeofenceAccordingToUserState();
                    }

                    else{
                        Log.d(TAG, "In invalid mall please select A VALID MALL");
                        stopPedometerService();
                    }
                }
            }, 8000);
        }
    }
    private void startGeofenceAccordingToUserState()
    {
        // if user is in the mall create geofence with loitering
        if(isInsideGeofence){
            mGeofenceService.clearGeofence();
            Log.d(TAG, "inside geofence, creating loitering geofence");
            mGeofenceService.startGeofence(getGeofenceDetails(), true);
        }
        else{
            Log.d(TAG, "outside geofence,  creating non loitering geofence");
            mGeofenceService.startGeofence(getGeofenceDetails(), false);
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

    private void startGeofenceService(){
        Log.d(TAG, "starting geofence service");
        Intent intent = new Intent(this, GeofenceService.class);
        intent.putExtra("latitude", 15.392144);
        intent.putExtra("longitude", 73.878643);
        intent.putExtra("radius", 800.0f);
        startService(intent);
    }

    private BroadcastReceiver fabIconReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d(TAG, "received fab icon update broadcast");
            if(intent.getAction().equals(Constants.Geofence.SHOW_EARNING_ICON)){
                mFloatingActionButton.setImageResource(R.drawable.ic_favorite_white_24dp);
            }
            else if(intent.getAction().equals(Constants.Geofence.SHOW_DEFAULT_ICON)){
                mFloatingActionButton.setImageResource(R.drawable.ic_search_white_48dp);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_drawer);

        Log.d(TAG, "isEarningSession in progress " + isEarningSessionInProgress);
        Log.d(TAG, "isInsideGeofence " + isInsideGeofence);



        // register googleAPIConnect broadcast receiver;
        /*LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mAPIConnectedBroadcast, new IntentFilter("APIConnected"));
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mGeofenceEnterBroadcast, new IntentFilter(Constants.Geofence.GEOFENCE_ENTER_BROADCAST));
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mGeofenceEnterBroadcast, new IntentFilter(Constants.Geofence.GEOFENCE_EXIT_BROADCAST));*/
        /*LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mGPSStateChangeBroadcast, new IntentFilter(Constants.Location.GPS_STATE_ON_BROADCAST));
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mGPSStateChangeBroadcast, new IntentFilter(Constants.Location.GPS_STATE_OFF_BROADCAST));*/
        /*LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mGeofenceCreatedBroadcast,new IntentFilter(Constants.Geofence.GEOFENCE_CREATED) );*/

        mToolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        //mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }

        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        mFloatingActionButton.setOnClickListener(this);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // get the mall that the user has selected. Its id is stored in the sharedprefs.
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SharedPreferences.USER_PREF_FILE,
                Context.MODE_PRIVATE);
        mallId = sharedPreferences.getString(Constants.SharedPreferences.MALL_ID, "MH_0253_CCM");
        Godlike.getShops(getApplicationContext(), mallId);


        // Populate frame layout with the food fragment with bottom navigation.
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        BottomNavOfferFragment foodFragment = BottomNavOfferFragment.newInstance(Constants.BOTTOM_MENU_FOOD_MENU,
                Constants.FoodCategoryKeys.FOOD_CATEGORY_1, Constants.FoodCategoryKeys.FOOD_CATEGORY_2,
                Constants.FoodCategoryKeys.FOOD_CATEGORY_3);
        fragmentTransaction.add(R.id.food_clothes_frag_container, foodFragment);
        fragmentTransaction.commit();


    }

    @Override
    public void onResume() {
        if (BuildConfig.DEBUG) Log.d(TAG, "on Resume");
        super.onResume();
        if(isUserWillingToEarn){
            Log.d(TAG, "in resume, binding geofence and step services if session in progress");
            //bindGeofenceService();
            //bindStepService();
        }
    }

    @Override
    public void onStart(){
        Log.d(TAG, "on start");
        IntentFilter fabIconIntentFilter = new IntentFilter();
        fabIconIntentFilter.addAction(Constants.Geofence.SHOW_DEFAULT_ICON);
        fabIconIntentFilter.addAction(Constants.Geofence.SHOW_EARNING_ICON);
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(fabIconReceiver, new IntentFilter(Constants.Geofence.SHOW_EARNING_ICON));
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(fabIconReceiver, new IntentFilter(Constants.Geofence.SHOW_DEFAULT_ICON));
        super.onStart();
    }

    @Override
    public void onStop(){

        super.onStop();
        Log.d(TAG, "on stop ");
        if(mBound){
            Log.d(TAG, "unbinding step service");
            // Unbind service with the same context that we used to bind to it
            /*unbindService(mStepConnection);*/

        }
        if(mGeofenceService != null)
        {
            Log.d(TAG, "Unbinding geofence service");
            /*unbindService(mGeofenceConnection);*/
        }
        try {
            LocalBroadcastManager.getInstance(getApplicationContext())
            .unregisterReceiver(fabIconReceiver);
        }
        catch (Exception e){
            Log.e(TAG, e.toString());
        }

    }



    @Override
    public void onBackPressed() {

        int backStackCount = getSupportFragmentManager().getBackStackEntryCount();

        if (BuildConfig.DEBUG) Log.d(TAG, "back stack count = " + backStackCount);

        if (!isDrawerEnabled) {
            isDrawerEnabled = true;
            setDrawerState(isDrawerEnabled);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        // Check if there are fragments open from where we wish to migrate to the main screen
        else if (backStackCount >= 1) {
            super.onBackPressed();
        }

        // Adding logic for double tap to exit;

        else if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        } else {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }

        Log.d(TAG, "onBackPressed");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "in onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.tool_bar_menu, menu);

        food_clothes_select_menu = menu.findItem(R.id.action_favourite);
        setIcon();
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();

        // Configure search view and add any event listeners
        // Associate searchable configuration with the search view
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    private void setIcon() {
        if (getFragment() instanceof BottomNavOfferFragment) {

            BottomNavOfferFragment fragment = (BottomNavOfferFragment) getFragment();
            if (fragment.getType().equals(Constants.BOTTOM_MENU_FOOD_MENU)) {
                food_clothes_select_menu.setIcon(R.drawable.ic_shopping_basket_white_24dp);

            } else if (fragment.getType().equals(Constants.BOTTOM_MENU_CLOTHES_MENU)) {
                food_clothes_select_menu.setIcon(R.drawable.ic_local_pizza_white_24dp);
            }

        }
    }
//--------------------------------------------------------------------------------------------------
    // Search

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "in onOptionsItemSelected");
        switch (item.getItemId()) {
            case android.R.id.home:
                if (BuildConfig.DEBUG) Log.d(TAG, "on up button pressed");
                super.onBackPressed();
                return true;
            case R.id.action_favourite:
                /*Toast.makeText(this, "You're my favourite", Toast.LENGTH_SHORT)
                        .show();*/
                /*mBottomNavigationView.inflateMenu(R.menu.bottom_navigation_menu_food);*/
                if (getFragment() instanceof BottomNavOfferFragment) {
                    BottomNavOfferFragment fragment = (BottomNavOfferFragment) getFragment();
                    if (fragment.getType().equals(Constants.BOTTOM_MENU_FOOD_MENU)) {
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        BottomNavOfferFragment clothesFragment = BottomNavOfferFragment.newInstance(Constants.BOTTOM_MENU_CLOTHES_MENU,
                                Constants.ClothesCategoryKeys.CLOTHES_CATEGORY_1, Constants.ClothesCategoryKeys.CLOTHES_CATEGORY_2,
                                Constants.ClothesCategoryKeys.CLOTHES_CATEGORY_3);
                        fragmentTransaction.replace(R.id.food_clothes_frag_container, clothesFragment);
                        fragmentTransaction.commit();
                        food_clothes_select_menu.setIcon(R.drawable.ic_shopping_basket_white_24dp);

                        navigationView.getMenu().clear();
                        navigationView.inflateMenu(R.menu.clothes_drawer);

                    } else if (fragment.getType().equals(Constants.BOTTOM_MENU_CLOTHES_MENU)) {
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        BottomNavOfferFragment foodFragment = BottomNavOfferFragment.newInstance(Constants.BOTTOM_MENU_FOOD_MENU,
                                Constants.FoodCategoryKeys.FOOD_CATEGORY_1, Constants.FoodCategoryKeys.FOOD_CATEGORY_2,
                                Constants.FoodCategoryKeys.FOOD_CATEGORY_3);
                        fragmentTransaction.replace(R.id.food_clothes_frag_container, foodFragment);
                        fragmentTransaction.commit();
                        food_clothes_select_menu.setIcon(R.drawable.ic_local_pizza_white_24dp);

                        navigationView.getMenu().clear();
                        navigationView.inflateMenu(R.menu.food_drawer);
                    }
                }

                return true;

            case R.id.action_settings:
                /*Toast.makeText(this, "Settings under construction", Toast.LENGTH_SHORT)
                        .show();*/
                Intent intent = new Intent(this, AddToFirebase.class);
                startActivity(intent);
                /*mBottomNavigationView.inflateMenu(R.menu.bottom_navigation_clothes);*/
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onQueryTextSubmit(String query) {

        //Fragment bottomFragment = myFragmentPagerAdapter.getRegisteredFragment(mViewPager.getCurrentItem());
        Fragment bottomFragment = getFragment();
        Fragment currentFragment = null;
        if (bottomFragment instanceof BottomNavOfferFragment) {
            if (BuildConfig.DEBUG)
                Log.d(TAG, "instance Of BottomNavOfferFragment");

            currentFragment = ((BottomNavOfferFragment) bottomFragment).getCurrentFragment();
        }
        if (currentFragment != null) {
            if (currentFragment instanceof ShopFragment) {
                if (BuildConfig.DEBUG)
                    Log.d(TAG, "instance Of ShopFragment");

                ShopFragment offerFrag = (ShopFragment) currentFragment;

                if (BuildConfig.DEBUG)
                    Log.d(TAG, "category of frag obtained" + offerFrag.getCategory());
                ((BottomNavOfferFragment) bottomFragment).hideBottomNavBar();

                if (query.isEmpty()) {
                    ((BottomNavOfferFragment) bottomFragment).showBottomNavBar();
                }
                if (BuildConfig.DEBUG) Log.d(TAG, "query is = " + query);
                if (BuildConfig.DEBUG) Log.d(TAG, "query length is + " + query.length());
                offerFrag.beginSearch(query);
            }
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        Fragment bottomFragment = getFragment();
        Fragment currentFragment = null;
        if (bottomFragment instanceof BottomNavOfferFragment) {
            if (BuildConfig.DEBUG)
                Log.d(TAG, "instance Of BottomNavOfferFragment");

            currentFragment = ((BottomNavOfferFragment) bottomFragment).getCurrentFragment();
        }
        if (currentFragment != null) {
            if (currentFragment instanceof ShopFragment) {
                if (BuildConfig.DEBUG)
                    Log.d(TAG, "instance Of ShopFragment");

                ShopFragment shopFrag = (ShopFragment) currentFragment;

                if (BuildConfig.DEBUG)
                    Log.d(TAG, "category of frag obtained" + shopFrag.getCategory());


                ((BottomNavOfferFragment) bottomFragment).hideBottomNavBar();

                if (newText.isEmpty()) {
                    ((BottomNavOfferFragment) bottomFragment).showBottomNavBar();
                }
                if (BuildConfig.DEBUG) Log.d(TAG, "new query is = " + newText);
                if (BuildConfig.DEBUG) Log.d(TAG, "new query length is + " + newText.length());
                shopFrag.beginSearch(newText);
            }
        }
        return true;
    }


    //---------------------------------------------------------------------------------------------

    @Override
    public void onClick(View view) {
        if (view == mFloatingActionButton) {

            //Toast.makeText(this, "Clicked fab", Toast.LENGTH_SHORT).show();
            // check network availability
            if (!isNetworkAvailable(this)) {
                Toast.makeText(this, getResources().getString(R.string.internet_not_available),
                        Toast.LENGTH_SHORT).show();
            } else {
                // check location status. if user is in the selected mall, start earning, else
                // present to the user a list of malls to select from


                // get radius lat and lon
                /*Intent getGeoFence = new Intent(this, SetLatLngRad.class);
                startActivityForResult(getGeoFence, 1);*/

                //start location updates and report on the accuracy of location obtained
                if(!checkPermission())
                {
                    askPermission();
                }
                else{
                    startEarningSequence();

                }

                /*if (BuildConfig.DEBUG) Log.d(TAG, "starting location updates");*/

                /*// connect to the geofence service
                startGeofenceService();
                bindGeofenceService();
                /*//*enableLocationUpdates();*/

            }
        }
    }

    private void startEarningSequence() {
        isUserWillingToEarn = true;
        // check for the gps state

        // if gps is on start the main service
        // check if gps is already on coz it will not trigger gpsCheckReceiver
        Log.d(TAG, "checking gps status and then starting geofence service if gps is already on");

        checkGpsSettings(locationRequest);

        /*LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            startGeofenceService();
        }*/

    }

   /* private void bindGeofenceService(){
        Intent geofenceIntent = new Intent(getApplicationContext(), GeofenceService.class);
        bindService(geofenceIntent, mGeofenceConnection, Context.BIND_AUTO_CREATE);
    }

    private void bindStepService(){
        Intent stepIntent = new Intent(getApplicationContext(), StepListener.class);
        bindService(stepIntent, mStepConnection, Context.BIND_AUTO_CREATE);
    }*/


    private MallGeoFence getGeofenceDetails() {
        Mall mall = Godlike.getMall(this, mallId);
        double latitude = 15.392144;
        double longitude = 73.878643;
        float radius = 200.0f;
        //Log.d(TAG, "lat = " + latitude + " lon = " + longitude + " address = " + mall.getAddress());
        MallGeoFence fence = new MallGeoFence(latitude, longitude, radius);
        return fence;
    }



    /*public void startGpsCheckAlarm() {

        if(BuildConfig.DEBUG)
            Log.d(TAG, "started alarm for gps check");
    *//* To listen to changes in GPS state *//*
        AlarmManager gpsCheckAlarmManager;

        Intent gpsCheckIntent = new Intent(this, MyBroadcast.class);

        PendingIntent gpsCheckPendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(),
                123, gpsCheckIntent, 0);

        gpsCheckAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        gpsCheckAlarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis()+(10 * 1000), 100, gpsCheckPendingIntent);
        Toast.makeText(this, "Alarm set in " + 10 + " seconds", Toast.LENGTH_LONG).show();
    }*/




    /*private void listenGPSStatus() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(checkPermission()){
            if(BuildConfig.DEBUG) Log.d(TAG, "got permissions to listen for gps status");
            if(Build.VERSION.SDK_INT >= 24){
                lm.registerGnssStatusCallback(new GnssStatus.Callback() {
                    @Override
                    public void onStarted() {
                        super.onStarted();
                    }

                    @Override
                    public void onStopped() {
                        super.onStopped();
                        Toast.makeText(HomeDrawerActivity.this, "Gps has been disabled",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

            else{

                if(BuildConfig.DEBUG) Log.d(TAG, "registering gps status listener for pre nougat devices");
                lm.addGpsStatusListener(new android.location.GpsStatus.Listener()
                {
                    public void onGpsStatusChanged(int event)
                    {
                        switch(event)
                        {
                            case GPS_EVENT_STARTED:
                                Toast.makeText(HomeDrawerActivity.this, "Gps has been enabled",
                                        Toast.LENGTH_SHORT).show();
                                if(mBound){
                                    stepListener.sendNotification("Earning", "Keep Exploring to earn");
                                    // stop counting steps
                                    stepListener.reRegisterSensor();
                                }
                                break;
                            case GPS_EVENT_STOPPED:
                                Toast.makeText(HomeDrawerActivity.this, "Gps has been disabled",
                                        Toast.LENGTH_SHORT).show();
                                if(mBound){
                                    stepListener.sendNotification("Earning Paused", "Please turn on the gps");
                                    // stop counting steps
                                    stepListener.unRegisterSensor();
                                }
                                break;
                        }
                    }
                });
            }
        }


    }*/

    //---------------------------------------------------------------------------------------------
    /*private void startGeofence()
    {

        if(BuildConfig.DEBUG){
            Log.d(TAG, "lat recieved = " + lat);
            Log.d(TAG, "lon recieved = " + lon);
            Log.d(TAG, "rad recieved = " + rad);
        }
        Intent intent = new Intent(this, Geofencing.class);
        intent.putExtra(Constants.Geofence.LATITUDE, lat);
        intent.putExtra(Constants.Geofence.LONGITUDE, lon);
        intent.putExtra(Constants.Geofence.RADIUS, rad);

        if(BuildConfig.DEBUG) Log.d(TAG, "in start Geofence");
        startActivity(intent);
    }*/

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==1)
        {

            lat = data.getDoubleExtra("latitude", 0.0);
            lon = data.getDoubleExtra("longitude", 0.0);
            rad = data.getFloatExtra("radius", 0.0f);

            startGeofence();
        }
    }*/

    //====================================================================================

    // Check for permission to access Location
    public boolean checkPermission() {
        Log.d(TAG, "checkPermission()");
        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED );
    }

    // Asks for permission
    public void askPermission() {
        Log.d(TAG, "askPermission()");

        // Show rationale if the permission has been denied for the first time
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION))
            {
                //Toast.makeText(this, "We need Gps to track you", Toast.LENGTH_SHORT)
                // .show();
            }
        }
        ActivityCompat.requestPermissions(
                this,
                new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION },
                Constants.Permission.ACCESS_FINE_LOCATION_PERMISSION
        );
    }

    // Verify user's response of the permission requested
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult()");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch ( requestCode ) {
            case Constants.Permission.ACCESS_FINE_LOCATION_PERMISSION: {
                if ( grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                    // Permission granted
                    // Check Location Settings for gps state
                    //startLocationUpdates();
                    startEarningSequence();

                } else {
                    // Permission denied
                    permissionsDenied();
                }
                break;
            }
        }
    }

    // App cannot work without the permissions
    private void permissionsDenied() {
        Log.w(TAG, "permissionsDenied()");
        // TODO close app and warn user
    }

    // Check if the GPS is turned on
    public void checkGpsSettings(LocationRequest locationRequest) {

        if(BuildConfig.DEBUG)  Log.d(TAG, "Checking GPS settings");

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true);

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder( this )
                .addConnectionCallbacks( this )
                .addOnConnectionFailedListener( this )
                .addApi( LocationServices.API )
                .build();

        googleApiClient.connect();

        // Check whether the current location settings are satisfied
        PendingResult<LocationSettingsResult> result = LocationServices
                .SettingsApi.checkLocationSettings(googleApiClient, builder.build());

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
                        startGeofenceService();
                        break;

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        if(BuildConfig.DEBUG)  Log.d(TAG, "Need to turn GPS on");
                        try
                        {
                            status.startResolutionForResult(
                                    HomeDrawerActivity.this, Constants.REQUEST_CHECK_LOCATION_SETTINGS);
                        } catch (IntentSender.SendIntentException e){}
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Toast.makeText(HomeDrawerActivity.this, "Cannot change GPS settings", Toast.LENGTH_SHORT)
                                .show();
                        break;
                }
            }
        });
    }


    @Override
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
                    startGeofenceService();
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

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if(BuildConfig.DEBUG) Log.d(TAG, "connected to the location services");

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "api Connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "api Connection failed");
    }


    //==============================================================================================

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (id == R.id.nav_camera) {

            // Clear this activity from back stack and finish the activity
            Intent intent = new Intent(this, MallSelectActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery){
            SearchFragment searchFragment = SearchFragment.newInstance("search1", "shoes");
            changeFragment(searchFragment, true);
            isDrawerEnabled = false;
            setDrawerState(isDrawerEnabled);

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changeFragment(Fragment frag, boolean saveInBackstack) {
        String backStateName = ((Object) frag).getClass().getName();

        try {
            FragmentManager manager = getSupportFragmentManager();
            boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

            if (!fragmentPopped && manager.findFragmentByTag(backStateName) == null) { //fragment not in back stack, create it.
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.food_clothes_frag_container, frag, ((Object) frag).getClass().getName());

                if (saveInBackstack) {
                    Log.d(TAG, "Change Fragment : addToBackTack");
                    transaction.addToBackStack(backStateName);
                } else {
                    Log.d(TAG, "Change Fragment : NO addToBackTack");
                }

                transaction.commit();
            } else {
                Log.d(TAG, "Change Fragment : nothing to do");
                // custom effect if fragment is already instantiated
            }
        } catch (IllegalStateException exception) {
            Log.e(TAG, "Unable to commit fragment, could be activity as been killed in background. " + exception.toString());
        }
    }

    public void setDrawerState(boolean isEnabled) {
        if ( isEnabled ) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            toggle.setDrawerIndicatorEnabled(true);
            toggle.syncState();

        }
        else {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            toggle.setDrawerIndicatorEnabled(false);

            toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSupportNavigateUp();
                }
            });
            toggle.syncState();
        }
    }

    private Fragment getFragment()
    {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.food_clothes_frag_container);
        return fragment;
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        stopService(new Intent(getApplicationContext(), StepListener.class));
    }

    @Override
    public boolean onSupportNavigateUp() {

        if(BuildConfig.DEBUG) Log.d(TAG, "on Support navigate up");
        //This method is called when the up button is pressed. Just the pop back stack.
        onBackPressed();
        return true;
    }


    //
   /* private class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private List<Fragment> mFragmentList = new ArrayList<>();
        private List<String> mFragmentTitleList = new ArrayList<>();
        SparseArray<Fragment> registeredFragments = new SparseArray<>();

        public void addFragment(Fragment fragment, String title)
        {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return mFragmentTitleList.get(position);
        }

        // Overridden instantiateItem and destroyItem() to maintain
        // a local copy of currently instantiated fragments for search queries

        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position)
        {
            return registeredFragments.get(position);
        }
    }*/

}
