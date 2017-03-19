package com.example.abhiraj.cardviewwithrecyclerview.ui;

import android.app.SearchManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.abhiraj.cardviewwithrecyclerview.Godlike;
import com.example.abhiraj.cardviewwithrecyclerview.R;
import com.example.abhiraj.cardviewwithrecyclerview.fragments.BottomNavOfferFragment;
import com.example.abhiraj.cardviewwithrecyclerview.fragments.OfferSkyOfferFragment;
import com.example.abhiraj.cardviewwithrecyclerview.fragments.ShopFragment;

import java.util.ArrayList;
import java.util.List;

public class OffersAndCouponsActivity extends AppCompatActivity implements ShopFragment.OffersFragmentListener, SearchView.OnQueryTextListener,
        BottomNavOfferFragment.OnFragmentInteractionListener, OfferSkyOfferFragment.OnFragmentInteractionListener, View.OnClickListener {

    private static final String TAG = OffersAndCouponsActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private FloatingActionButton mFloatingActionButton;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;

   /* private BottomNavigationView mBottomNavigationView;

    private FragNavController mFragNavController;

    //indices to fragments
    private final int TAB_FIRST = FragNavController.TAB1;
    private final int TAB_SECOND = FragNavController.TAB2;
    private final int TAB_THIRD = FragNavController.TAB3;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers_and_coupons);

        Godlike.getCoupons(getApplicationContext(), "GA_0832_MDG");

        mToolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        if(mToolbar != null)
        {
            setSupportActionBar(mToolbar);
        }

        mViewPager  =(ViewPager) findViewById(R.id.pager);
        //mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        //mFloatingActionButton.setOnClickListener(this);

        /*mBottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);*/

        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        myFragmentPagerAdapter.addFragment(new BottomNavOfferFragment(), "Bottom1");
        myFragmentPagerAdapter.addFragment(new BottomNavOfferFragment(), "Bottom2");
        //myFragmentPagerAdapter.addFragment(new OfferSkyOfferFragment(), "Offer2");

        mViewPager.setAdapter(myFragmentPagerAdapter);

        mTabLayout.setupWithViewPager(mViewPager);

/*        // FragNav

        List<Fragment> bottomFragments = new ArrayList<>(3);

        // Add frags to the list
        bottomFragments.add(new OfferSkyOfferFragment());
        bottomFragments.add(new OfferSkyOfferFragment());
        bottomFragments.add(new OfferSkyOfferFragment());

        // Link fragments to container
        mFragNavController = new FragNavController(savedInstanceState, getSupportFragmentManager(),
                R.id.bottomFragsContainer, bottomFragments, TAB_FIRST);

        mBottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_favorites:
                                mFragNavController.switchTab(TAB_FIRST);
                                break;
                            case R.id.action_schedules:
                                mFragNavController.switchTab(TAB_SECOND);
                                break;
                            case R.id.action_music:
                                mFragNavController.switchTab(TAB_THIRD);
                                break;
                        }
                        return true;
                    }
                });*/


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
                /*Toast.makeText(this, "You're my favourite", Toast.LENGTH_SHORT)
                        .show();*/
                /*mBottomNavigationView.inflateMenu(R.menu.bottom_navigation_menu_food);*/
                return true;

            case R.id.action_settings:
                /*Toast.makeText(this, "Settings under construction", Toast.LENGTH_SHORT)
                        .show();*/
                /*mBottomNavigationView.inflateMenu(R.menu.bottom_navigation_clothes);*/
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onClick(View view) {
        if(view == mFloatingActionButton)
        {
            /*Toast.makeText(this, "Clicked fab", Toast.LENGTH_SHORT).show();*/
        }
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
