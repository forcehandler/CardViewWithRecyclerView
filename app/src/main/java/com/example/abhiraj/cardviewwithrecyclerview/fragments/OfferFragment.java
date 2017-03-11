package com.example.abhiraj.cardviewwithrecyclerview.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.abhiraj.cardviewwithrecyclerview.BuildConfig;
import com.example.abhiraj.cardviewwithrecyclerview.Godlike;
import com.example.abhiraj.cardviewwithrecyclerview.R;
import com.example.abhiraj.cardviewwithrecyclerview.adapters.OfferAdapter;
import com.example.abhiraj.cardviewwithrecyclerview.models.Coupon;
import com.example.abhiraj.cardviewwithrecyclerview.models.Shop;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OfferFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OfferFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

// TODO: Attach Broadcast Listener to listen for changes in coupon.

public class OfferFragment extends Fragment implements OfferAdapter.CouponClickListener {

    private static final String TAG = OfferFragment.class.getSimpleName();

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String SHOP_KEY = "SHOP_KEY";
    private static final String ARG_PARAM2 = "param2";


    private String mShopKey;
    private String mParam2;

    private RecyclerView mCouponRecyclerView;
    private OnFragmentInteractionListener mListener;

    private OfferAdapter mOfferAdapter;
    private List<Coupon> mCouponList;

    public OfferFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @return A new instance of fragment OfferFragment.
     */

    public static OfferFragment newInstance(String shop_pos) {
        OfferFragment fragment = new OfferFragment();
        Bundle args = new Bundle();
        args.putString(SHOP_KEY, shop_pos);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mShopKey = getArguments().getString(SHOP_KEY);
        }

        if(BuildConfig.DEBUG) Log.d(TAG, "shop no = " + mShopKey);

        // Find the shop with the provided shop key
        Shop shop = new Shop();

        for(Shop s : Godlike.mShopsList)
        {
            if(s.getKey().equals(mShopKey))
            {
                shop = s;
            }
        }

        // If by chance we cannot find shop with the given key (Say the shop changed when the guy clicked
        // then provide some default shop)
        // TODO: Handle it more gracefully
        if(shop == null)
        {
            shop = new Shop();
            shop.setName("selected shop dne");
            shop.setBrandImageURL("http://res.cloudinary.com/crosscharge/image/upload/v1485304866/nike_logo_z0fp3h.webp");
            shop.setShopImageURL("http://res.cloudinary.com/crosscharge/image/upload/v1485304866/nike_logo_z0fp3h.webp");
            List<String> foodCategories = new ArrayList<>();
            foodCategories.add("food");
            shop.setCategories(foodCategories);
        }


        if(BuildConfig.DEBUG) Log.d(TAG, "shop name = " + shop.getName());

        mCouponList = new ArrayList<>();

        if(shop.getCoupons() == null)
        {
            if(BuildConfig.DEBUG) Log.d(TAG, "the shop has no coupons!");
        }
        else {
            mCouponList.addAll(shop.getCoupons());
        }


        mOfferAdapter = new OfferAdapter(getActivity(), mCouponList);
        mOfferAdapter.setCouponClickListener(this);

        // TODO: Coupon filtering according to category
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_offer, container, false);
        mCouponRecyclerView = (RecyclerView) root.findViewById(R.id.offer_fragment_rv);
        mCouponRecyclerView.setLayoutManager(new LinearLayoutManager(root.getContext(), LinearLayoutManager.HORIZONTAL, false));
        mCouponRecyclerView.setAdapter(mOfferAdapter);

        return root;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCouponClick(View v, int position) {
         if(BuildConfig.DEBUG) Log.d(TAG, "coupon clicked!! at pos = " + position);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
