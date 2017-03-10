package com.example.abhiraj.cardviewwithrecyclerview.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
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
import android.widget.Toast;

import com.example.abhiraj.cardviewwithrecyclerview.BuildConfig;
import com.example.abhiraj.cardviewwithrecyclerview.Constants;
import com.example.abhiraj.cardviewwithrecyclerview.Godlike;
import com.example.abhiraj.cardviewwithrecyclerview.R;
import com.example.abhiraj.cardviewwithrecyclerview.fragments.BottomNavOfferFragment;
import com.example.abhiraj.cardviewwithrecyclerview.fragments.OfferFragment;
import com.example.abhiraj.cardviewwithrecyclerview.fragments.OfferSkyOfferFragment;
import com.example.abhiraj.cardviewwithrecyclerview.write.AddToFirebase;

import java.util.ArrayList;
import java.util.List;

public class HomeDrawerActivity extends AppCompatActivity
        implements OfferFragment.OffersFragmentListener, NavigationView.OnNavigationItemSelectedListener, BottomNavOfferFragment.OnFragmentInteractionListener, OfferSkyOfferFragment.OnFragmentInteractionListener, SearchView.OnQueryTextListener, View.OnClickListener {

    private static final String TAG = HomeDrawerActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private FloatingActionButton mFloatingActionButton;
    private HomeDrawerActivity.MyFragmentPagerAdapter myFragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_drawer);

        mToolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        Godlike.getShops(getApplicationContext(), "UP_0522_FR");


        mViewPager  =(ViewPager) findViewById(R.id.pager);

        myFragmentPagerAdapter = new HomeDrawerActivity.MyFragmentPagerAdapter(getSupportFragmentManager());

        myFragmentPagerAdapter.addFragment(BottomNavOfferFragment.newInstance(Constants.BOTTOM_MENU_FOOD_MENU,
                "chaat","icecream","drinks"), "Food");
        myFragmentPagerAdapter.addFragment(BottomNavOfferFragment.newInstance(Constants.BOTTOM_MENU_CLOTHES_MENU,
                "formals","shoes","sarees"), "Clothes");

        //myFragmentPagerAdapter.addFragment(new OfferSkyOfferFragment(), "Offer2");

        mViewPager.setAdapter(myFragmentPagerAdapter);

        mTabLayout.setupWithViewPager(mViewPager);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Log.d(TAG, "in onOptionsItemSelected");
        switch(item.getItemId())
        {
            case R.id.action_favourite:
                Toast.makeText(this, "You're my favourite", Toast.LENGTH_SHORT)
                        .show();
                /*mBottomNavigationView.inflateMenu(R.menu.bottom_navigation_menu_food);*/
                return true;

            case R.id.action_settings:
                Toast.makeText(this, "Settings under construction", Toast.LENGTH_SHORT)
                        .show();
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

        Fragment currentFragment = myFragmentPagerAdapter.getRegisteredFragment(mViewPager.getCurrentItem());
        if(currentFragment != null)
        {
            if(currentFragment instanceof OfferFragment)
            {
                OfferFragment offerFrag = (OfferFragment) currentFragment;
                offerFrag.beginSearch(query);
            }
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        Fragment bottomFragment = myFragmentPagerAdapter.getRegisteredFragment(mViewPager.getCurrentItem());
        Fragment currentFragment = null;
        if(bottomFragment instanceof BottomNavOfferFragment)
        {
            if (BuildConfig.DEBUG)
                Log.d(TAG, "instance Of BottomNavOfferFragment");

            currentFragment = ((BottomNavOfferFragment) bottomFragment).getCurrentFragment();
        }
        if(currentFragment != null)
        {
            if(currentFragment instanceof OfferFragment)
            {
                if (BuildConfig.DEBUG)
                    Log.d(TAG, "instance Of OfferFragment");

                OfferFragment offerFrag = (OfferFragment) currentFragment;

                if (BuildConfig.DEBUG)
                    Log.d(TAG, "category of frag obtained" + offerFrag.getCategory());
                offerFrag.beginSearch(newText);
            }
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        if(view == mFloatingActionButton)
        {
            Toast.makeText(this, "Clicked fab", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

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
