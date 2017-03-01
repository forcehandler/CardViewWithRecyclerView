package com.example.abhiraj.cardviewwithrecyclerview.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.abhiraj.cardviewwithrecyclerview.fragments.OfferFragment;
import com.example.abhiraj.cardviewwithrecyclerview.fragments.OfferSkyOfferFragment;
import com.example.abhiraj.cardviewwithrecyclerview.R;

public class TemporaryOfferActivity extends AppCompatActivity implements OfferSkyOfferFragment.OnFragmentInteractionListener {

    private final String TAG = TemporaryOfferActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "in on create");
        setContentView(R.layout.activity_temporary_offer);

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.offer_fragment_container) != null) {
            Log.d(TAG, "offer fragment container is not null");
            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            /*if (savedInstanceState != null) {
                return;
            }*/

            // Create a new Fragment to be placed in the activity layout
            //OfferFragment firstFragment = new OfferFragment();

            //TODO: Temporarily testing OfferSky fragment with imageloader
            OfferSkyOfferFragment offerSkyOfferFragment = new OfferSkyOfferFragment();
            OfferFragment offerFragment = new OfferFragment();
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            //firstFragment.setArguments(getIntent().getExtras());

            Log.d(TAG, "getting Support Fragment Manager");
            // Adding fragment to the frame Layout
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.offer_fragment_container, offerSkyOfferFragment).commit();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
