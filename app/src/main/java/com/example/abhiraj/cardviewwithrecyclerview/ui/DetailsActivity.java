package com.example.abhiraj.cardviewwithrecyclerview.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.FrameLayout;

import com.example.abhiraj.cardviewwithrecyclerview.BuildConfig;
import com.example.abhiraj.cardviewwithrecyclerview.Constants;
import com.example.abhiraj.cardviewwithrecyclerview.R;
import com.example.abhiraj.cardviewwithrecyclerview.fragments.OfferFragment;
import com.example.abhiraj.cardviewwithrecyclerview.fragments.ShopFragment;

public class DetailsActivity extends AppCompatActivity implements ShopFragment.OffersFragmentListener, OfferFragment.OnFragmentInteractionListener{

    private static final String TAG = DetailsActivity.class.getSimpleName();

    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    private Toolbar mToolbar;

    private FrameLayout coupon_frag_container;

    private String mShopKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        if(mCollapsingToolbarLayout!=null)
        {
            mCollapsingToolbarLayout.setTitle("Details Activity");
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if(mToolbar != null)
        {
            setSupportActionBar(mToolbar);
        }

        // Get the position of the shop which was clicked
        Intent intent = getIntent();
        mShopKey = intent.getStringExtra(Constants.IntentKeys.CLICKED_SHOP_KEY);

        if(BuildConfig.DEBUG) Log.d(TAG, "shop position recieved = " + mShopKey);
        // add the coupon fragment
        coupon_frag_container = (FrameLayout) findViewById(R.id.coupon_frag_container);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        OfferFragment offerFragment = OfferFragment.newInstance(mShopKey);

        fragmentTransaction.add(coupon_frag_container.getId(), offerFragment);
        fragmentTransaction.commit();


    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
