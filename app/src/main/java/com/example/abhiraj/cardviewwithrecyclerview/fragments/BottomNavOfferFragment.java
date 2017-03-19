package com.example.abhiraj.cardviewwithrecyclerview.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.abhiraj.cardviewwithrecyclerview.BuildConfig;
import com.example.abhiraj.cardviewwithrecyclerview.Constants;
import com.example.abhiraj.cardviewwithrecyclerview.R;
import com.ncapdevi.fragnav.FragNavController;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BottomNavOfferFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BottomNavOfferFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BottomNavOfferFragment extends Fragment implements OfferSkyOfferFragment.OnFragmentInteractionListener{

    private static final String TAG = BottomNavOfferFragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String BOTTOM_MENU_LAYOUT_SELECT = "bottom_menu_select";
    private static final String CATEGORY1_KEY = "param1";
    private static final String CATEGORY2_KEY = "param2";
    private static final String CATEGORY3_KEY = "param3";

    private BottomNavigationView mBottomNavigationView;

    private FragNavController mFragNavController;

    private List<Fragment> bottomFragments;

    //indices to fragments
    private final int TAB_FIRST = FragNavController.TAB1;
    private final int TAB_SECOND = FragNavController.TAB2;
    private final int TAB_THIRD = FragNavController.TAB3;

    // TODO: Rename and change types of parameters
    private String bottom_menu_layout_select;
    private String category1;
    private String category2;
    private String category3;

    private OnFragmentInteractionListener mListener;

    public BottomNavOfferFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param category1 Parameter 1.
     * @param category2 Parameter 2.
     * @return A new instance of fragment BottomNavOfferFragment.
     */

    public static BottomNavOfferFragment newInstance(String bottom_menu_layout, String category1, String category2, String category3) {
        BottomNavOfferFragment fragment = new BottomNavOfferFragment();
        Bundle args = new Bundle();
        args.putString(BOTTOM_MENU_LAYOUT_SELECT, bottom_menu_layout);
        args.putString(CATEGORY1_KEY, category1);
        args.putString(CATEGORY2_KEY, category2);
        args.putString(CATEGORY3_KEY, category3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bottom_menu_layout_select = getArguments().getString(BOTTOM_MENU_LAYOUT_SELECT);
            category1 = getArguments().getString(CATEGORY1_KEY);
            category2 = getArguments().getString(CATEGORY2_KEY);
            category3 = getArguments().getString(CATEGORY3_KEY);

            if(BuildConfig.DEBUG)
            {
                Log.d(TAG, "category1="+category1);
                Log.d(TAG, "category2="+category2);
                Log.d(TAG, "category3="+category3);
            }
        }


        // FragNav

        bottomFragments = new ArrayList<>(3);

        // Add frags to the list
        //bottomFragments.add(new OfferSkyOfferFragment());
        bottomFragments.add(ShopFragment.newInstance(category1));
        bottomFragments.add(ShopFragment.newInstance(category2));
        bottomFragments.add(ShopFragment.newInstance(category3));

        // Link fragments to container
        mFragNavController = new FragNavController(savedInstanceState, getChildFragmentManager(),
                R.id.bottomFragsContainer, bottomFragments, TAB_FIRST);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_bottom_nav_offer, container, false);
        mBottomNavigationView = (BottomNavigationView)
                root.findViewById(R.id.bottom_navigation);

        // to show shop and food icon toolbar
        setHasOptionsMenu(true);

        // Select the bottom nav item menu
        if(bottom_menu_layout_select == Constants.BOTTOM_MENU_CLOTHES_MENU)
        {
            mBottomNavigationView.inflateMenu(R.menu.bottom_navigation_clothes);
        }
        else if(bottom_menu_layout_select == Constants.BOTTOM_MENU_FOOD_MENU) {
            mBottomNavigationView.inflateMenu(R.menu.bottom_navigation_menu_food);
        }



        mBottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_first:
                                mFragNavController.switchTab(TAB_FIRST);
                                break;
                            case R.id.action_second:
                                mFragNavController.switchTab(TAB_SECOND);
                                break;
                            case R.id.action_third:
                                mFragNavController.switchTab(TAB_THIRD);
                                break;
                        }
                        return true;
                    }
                });


        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater)
    {
        if(bottom_menu_layout_select == Constants.BOTTOM_MENU_CLOTHES_MENU){
            menuInflater.inflate(R.menu.tool_bar_menu, menu);

            menu.findItem(R.id.action_favourite);


        }
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
    public void onFragmentInteraction(Uri uri) {

    }

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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public Fragment getCurrentFragment()
    {
        return mFragNavController.getCurrentFrag();

    }

    public void hideBottomNavBar()
    {
        if (BuildConfig.DEBUG) Log.d(TAG, "hideBottomNavBar");
        mBottomNavigationView.setVisibility(View.INVISIBLE);
    }

    public void showBottomNavBar()
    {
        if (BuildConfig.DEBUG) Log.d(TAG, "showBottomNavBar");
        mBottomNavigationView.setVisibility(View.VISIBLE);
    }

    public String getType()
    {
        return bottom_menu_layout_select;
    }
}
