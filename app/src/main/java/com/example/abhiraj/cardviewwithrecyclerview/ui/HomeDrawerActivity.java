package com.example.abhiraj.cardviewwithrecyclerview.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.abhiraj.cardviewwithrecyclerview.BuildConfig;
import com.example.abhiraj.cardviewwithrecyclerview.Constants;
import com.example.abhiraj.cardviewwithrecyclerview.Godlike;
import com.example.abhiraj.cardviewwithrecyclerview.R;
import com.example.abhiraj.cardviewwithrecyclerview.fragments.BottomNavOfferFragment;
import com.example.abhiraj.cardviewwithrecyclerview.fragments.OfferSkyOfferFragment;
import com.example.abhiraj.cardviewwithrecyclerview.fragments.SearchFragment;
import com.example.abhiraj.cardviewwithrecyclerview.fragments.ShopFragment;
import com.example.abhiraj.cardviewwithrecyclerview.geofencing.Geofencing;
import com.example.abhiraj.cardviewwithrecyclerview.geofencing.SetLatLngRad;
import com.example.abhiraj.cardviewwithrecyclerview.geofencing.StepListener;
import com.example.abhiraj.cardviewwithrecyclerview.write.AddToFirebase;

import java.util.ArrayList;
import java.util.List;

public class HomeDrawerActivity extends AppCompatActivity
        implements SearchFragment.OnFragmentInteractionListener, ShopFragment.OffersFragmentListener, NavigationView.OnNavigationItemSelectedListener, BottomNavOfferFragment.OnFragmentInteractionListener, OfferSkyOfferFragment.OnFragmentInteractionListener, SearchView.OnQueryTextListener, View.OnClickListener {

    private static final String TAG = HomeDrawerActivity.class.getSimpleName();
    private Toolbar mToolbar;
    //private TabLayout mTabLayout;
    //private ViewPager mViewPager;
    private FloatingActionButton mFloatingActionButton;
    FragmentTransaction fragmentTransaction;
   // private HomeDrawerActivity.MyFragmentPagerAdapter myFragmentPagerAdapter;

    private MenuItem food_clothes_select_menu;

    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_drawer);

        mToolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        //mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        if(mToolbar != null)
        {
            setSupportActionBar(mToolbar);
        }

        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        mFloatingActionButton.setOnClickListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SharedPreferences.USER_PREF_FILE,
                Context.MODE_PRIVATE);
        String mallId = sharedPreferences.getString(Constants.SharedPreferences.MALL_ID, "MH_0253_CCM");
        Godlike.getShops(getApplicationContext(), mallId);


        //mViewPager  =(ViewPager) findViewById(R.id.pager);

        //myFragmentPagerAdapter = new HomeDrawerActivity.MyFragmentPagerAdapter(getSupportFragmentManager());

        //myFragmentPagerAdapter.addFragment(BottomNavOfferFragment.newInstance(Constants.BOTTOM_MENU_FOOD_MENU,
               // "chaat","icecream","drinks"), "Food");
        //myFragmentPagerAdapter.addFragment(BottomNavOfferFragment.newInstance(Constants.BOTTOM_MENU_CLOTHES_MENU,
               // "formals","shoes","sarees"), "Clothes");


        //mViewPager.setAdapter(myFragmentPagerAdapter);

        //mTabLayout.setupWithViewPager(mViewPager);


        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        BottomNavOfferFragment foodFragment = BottomNavOfferFragment.newInstance(Constants.BOTTOM_MENU_FOOD_MENU,
                Constants.FoodCategoryKeys.FOOD_CATEGORY_1, Constants.FoodCategoryKeys.FOOD_CATEGORY_2,
                Constants.FoodCategoryKeys.FOOD_CATEGORY_3);
        fragmentTransaction.add(R.id.food_clothes_frag_container, foodFragment);
        fragmentTransaction.commit();

        // For handling back button on the search fragment
        /*getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {

            @Override
            public void onBackStackChanged() {
                if(BuildConfig.DEBUG) Log.d(TAG, "BackStackChanged");
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                } else {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                }
            }
        });*/

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
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

    private void setIcon()
    {
        if(getFragment() instanceof BottomNavOfferFragment)
        {

                BottomNavOfferFragment fragment = (BottomNavOfferFragment) getFragment();
                if (fragment.getType().equals(Constants.BOTTOM_MENU_FOOD_MENU)) {
                    food_clothes_select_menu.setIcon(R.drawable.ic_shopping_basket_white_24dp);

                } else if (fragment.getType().equals(Constants.BOTTOM_MENU_CLOTHES_MENU)) {
                    food_clothes_select_menu.setIcon(R.drawable.ic_local_pizza_white_24dp);
                }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Log.d(TAG, "in onOptionsItemSelected");
        switch(item.getItemId())
        {
            case R.id.action_favourite:
                /*Toast.makeText(this, "You're my favourite", Toast.LENGTH_SHORT)
                        .show();*/
                /*mBottomNavigationView.inflateMenu(R.menu.bottom_navigation_menu_food);*/
                if(getFragment() instanceof BottomNavOfferFragment)
                {
                    BottomNavOfferFragment fragment = (BottomNavOfferFragment) getFragment();
                    if(fragment.getType().equals(Constants.BOTTOM_MENU_FOOD_MENU)){
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        BottomNavOfferFragment clothesFragment = BottomNavOfferFragment.newInstance(Constants.BOTTOM_MENU_CLOTHES_MENU,
                                Constants.ClothesCategoryKeys.CLOTHES_CATEGORY_1, Constants.ClothesCategoryKeys.CLOTHES_CATEGORY_2,
                                Constants.ClothesCategoryKeys.CLOTHES_CATEGORY_3);
                        fragmentTransaction.replace(R.id.food_clothes_frag_container, clothesFragment);
                        fragmentTransaction.commit();
                        food_clothes_select_menu.setIcon(R.drawable.ic_shopping_basket_white_24dp);

                        navigationView.getMenu().clear();
                        navigationView.inflateMenu(R.menu.clothes_drawer);

                    }
                    else if(fragment.getType().equals(Constants.BOTTOM_MENU_CLOTHES_MENU))
                    {
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        BottomNavOfferFragment foodFragment = BottomNavOfferFragment.newInstance(Constants.BOTTOM_MENU_FOOD_MENU,
                                Constants.FoodCategoryKeys.FOOD_CATEGORY_1, Constants.FoodCategoryKeys.FOOD_CATEGORY_2,
                                Constants.FoodCategoryKeys.FOOD_CATEGORY_3);
                        fragmentTransaction.replace(R.id.food_clothes_frag_container, foodFragment);
                        fragmentTransaction.commit();
                        food_clothes_select_menu.setIcon(R.drawable.ic_local_pizza_white_24dp);
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
        if(bottomFragment instanceof BottomNavOfferFragment)
        {
            if (BuildConfig.DEBUG)
                Log.d(TAG, "instance Of BottomNavOfferFragment");

            currentFragment = ((BottomNavOfferFragment) bottomFragment).getCurrentFragment();
        }
        if(currentFragment != null)
        {
            if(currentFragment instanceof ShopFragment)
            {
                if (BuildConfig.DEBUG)
                    Log.d(TAG, "instance Of ShopFragment");

                ShopFragment offerFrag = (ShopFragment) currentFragment;

                if (BuildConfig.DEBUG)
                    Log.d(TAG, "category of frag obtained" + offerFrag.getCategory());
                ((BottomNavOfferFragment) bottomFragment).hideBottomNavBar();

                if(query.isEmpty())
                {
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
        if(bottomFragment instanceof BottomNavOfferFragment)
        {
            if (BuildConfig.DEBUG)
                Log.d(TAG, "instance Of BottomNavOfferFragment");

            currentFragment = ((BottomNavOfferFragment) bottomFragment).getCurrentFragment();
        }
        if(currentFragment != null)
        {
            if(currentFragment instanceof ShopFragment)
            {
                if (BuildConfig.DEBUG)
                    Log.d(TAG, "instance Of ShopFragment");

                ShopFragment shopFrag = (ShopFragment) currentFragment;

                if (BuildConfig.DEBUG)
                    Log.d(TAG, "category of frag obtained" + shopFrag.getCategory());


                ((BottomNavOfferFragment) bottomFragment).hideBottomNavBar();

                if(newText.isEmpty())
                {
                    ((BottomNavOfferFragment) bottomFragment).showBottomNavBar();
                }
                if (BuildConfig.DEBUG) Log.d(TAG, "new query is = " + newText);
                if (BuildConfig.DEBUG) Log.d(TAG, "new query length is + " + newText.length());
                shopFrag.beginSearch(newText);
            }
        }
        return true;
    }

    double lat, lon;
    float rad;
    @Override
    public void onClick(View view) {
        if(view == mFloatingActionButton)
        {
            //Toast.makeText(this, "Clicked fab", Toast.LENGTH_SHORT).show();

            // get radius lat and lon
            Intent getGeoFence = new Intent(this, SetLatLngRad.class);
            startActivityForResult(getGeoFence, 1);
        }
    }

    private void startGeofence()
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
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
    }

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
            fragmentTransaction.replace(R.id.food_clothes_frag_container, searchFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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


    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {
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
    }

}
