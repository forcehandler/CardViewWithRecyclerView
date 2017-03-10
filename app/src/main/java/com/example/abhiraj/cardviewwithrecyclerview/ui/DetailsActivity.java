package com.example.abhiraj.cardviewwithrecyclerview.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import com.example.abhiraj.cardviewwithrecyclerview.R;
import com.example.abhiraj.cardviewwithrecyclerview.fragments.OfferFragment;

public class DetailsActivity extends AppCompatActivity implements OfferFragment.OffersFragmentListener{

    private static final String TAG = DetailsActivity.class.getSimpleName();

    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    private Toolbar mToolbar;

    private FrameLayout horiz_offer_container;


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

        /*horiz_offer_container = (FrameLayout) findViewById(R.id.horiz_offer_container);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        OfferFragment offerFragment = OfferFragment.newInstance("chaat");

        fragmentTransaction.add(horiz_offer_container.getId(), offerFragment);
        fragmentTransaction.commit();*/


    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
