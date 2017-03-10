package com.example.abhiraj.cardviewwithrecyclerview.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.abhiraj.cardviewwithrecyclerview.BuildConfig;
import com.example.abhiraj.cardviewwithrecyclerview.Constants;
import com.example.abhiraj.cardviewwithrecyclerview.Godlike;
import com.example.abhiraj.cardviewwithrecyclerview.R;
import com.example.abhiraj.cardviewwithrecyclerview.adapters.ShopAdapter;
import com.example.abhiraj.cardviewwithrecyclerview.models.Shop;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OffersFragmentListener} interface
 * to handle interaction events.
 * Use the {@link OfferFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OfferFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private static String TAG = OfferFragment.class.getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String CATEGORY_KEY = "CATEGORY1";

    private RecyclerView recyclerView;
   // private SwipeRefreshLayout swipeRefreshLayout;

    //private DatabaseReference mRef;

    //private FirebaseRecyclerAdapter mAdapter;

    private List<Shop> filteredShops = new ArrayList<>();

    private ShopAdapter mShopAdapter;

    private BroadcastReceiver shopUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(BuildConfig.DEBUG)
                Log.d(TAG, "received shop data changed broadcast");
            filteredShops.clear();
            filteredShops.addAll(filterData(category));
            //Log.d(TAG, "shopsdata " + Godlike.mShopsList);
            //Log.d(TAG, "filtereddata " + filteredShops);
            mShopAdapter.notifyDataSetChanged();
        }
    };



    // TODO: Rename and change types of parameters
    private String category;
    private String mParam2;

    private OffersFragmentListener mListener;

    public OfferFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param category Parameter 1.
     * @return A new instance of fragment OfferFragment.
     */

    public static OfferFragment newInstance(String category) {
        OfferFragment fragment = new OfferFragment();
        Bundle args = new Bundle();
        args.putString(CATEGORY_KEY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getString(CATEGORY_KEY);

            if(BuildConfig.DEBUG)
            {
                Log.d(TAG, "category="+category);

            }
        }


        Log.d(TAG, "initializing mRef for database in onCreate");

        // Get the reference to the firebase database
        //mRef = FirebaseDatabase.getInstance().getReference("testcoupons");

        // Filter the data according to the category and pass it to the adapter.
        filteredShops.clear();
        filteredShops.addAll(filterData(category));
        mShopAdapter = new ShopAdapter(getContext(), filteredShops);

        if(BuildConfig.DEBUG)
            Log.d(TAG, "registering shopUpdateReceiver");

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(shopUpdateReceiver, new IntentFilter(Constants.Broadcasts.BROADCAST_SHOP_UPDATE));

    }

    public void beginSearch(String query)
    {
        if(BuildConfig.DEBUG) Log.d(TAG, " in Begin Search");
        filteredShops.clear();
        filteredShops.addAll(filterData(query));
        mShopAdapter.notifyDataSetChanged();
        //recyclerView.scrollToPosition(0);
    }



    private List<Shop> filterData(String query)
    {
        if(BuildConfig.DEBUG) Log.d(TAG, " in filterData with query = " + query);

        String lowerCaseQuery = query.toLowerCase();
        List<Shop> filteredData = new ArrayList<>();
        if(lowerCaseQuery == null)
        {
            if(BuildConfig.DEBUG)
                Log.e(TAG, "pass category to the offerFragment");

            return null;
        }
        //filteredData.clear();
        for (Shop shop : Godlike.mShopsList)
        {
            if(shop.getCategories().contains(lowerCaseQuery))
            {
                Log.d(TAG, "adding for " + lowerCaseQuery + " shop " + shop.getName() );
                filteredData.add(shop);
            }
        }
        return filteredData;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.offer_fragment, container, false);

        Log.d(TAG, "in OnCreateView setting up recycler view firebase");
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.offer_fragment_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mShopAdapter);

        // TODO: remove this code after testing the shopAdapter




        /*// Initialize the adapter with the firebase recycler adapter
        mAdapter = new FirebaseRecyclerAdapter<OfferSkyOffers, OfferHolder>(OfferSkyOffers.class, R.layout.offer_cardview, OfferHolder.class, mRef){

            @Override
            protected void populateViewHolder(OfferHolder viewHolder, OfferSkyOffers model, int position) {
                Log.d(TAG, "populating the offer view holder");
               *//*viewHolder.setmBrandImageIv(model.getShopURL());
                viewHolder.setmMainImageIv(model.getPhotoURL());
                viewHolder.setmBrandTitleTv(model.getBrand());
                viewHolder.setmDescriptionTv(model.getDescription());*//*
                viewHolder.bindViews(model);
            }
        };

        // Set the adapter to the recycler
        recyclerView.setAdapter(mAdapter);*/

        return rootView;
    }

    public String getCategory()
    {
        return category;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OffersFragmentListener) {
            mListener = (OffersFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OffersFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(BuildConfig.DEBUG)
        {
            Log.d(TAG, "category="+category);

        }
    }

    @Override
    public void onDestroy()
    {
        if(BuildConfig.DEBUG)
            Log.d(TAG, "onDestroy");
        super.onDestroy();
        if(shopUpdateReceiver != null)
        {
            if(BuildConfig.DEBUG)
                Log.d(TAG, "unregistering shopUpdateReceiver");
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(shopUpdateReceiver);
        }
    }

    @Override
    public void onRefresh() {

    }

    /*


        /**
         * This interface must be implemented by activities that contain this
         * fragment to allow an interaction in this fragment to be communicated
         * to the activity and potentially other fragments contained in that
         * activity.
         * <p>
         * See the Android Training lesson <a href=
         * "http://developer.android.com/training/basics/fragments/communicating.html"
         * >Communicating with Other Fragments</a> for more information.
         */
    public interface OffersFragmentListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
