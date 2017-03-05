package com.example.abhiraj.cardviewwithrecyclerview.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.abhiraj.cardviewwithrecyclerview.R;
import com.example.abhiraj.cardviewwithrecyclerview.ViewHolder.OfferHolder;
import com.example.abhiraj.cardviewwithrecyclerview.models.OfferSkyOffers;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


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

    private RecyclerView recyclerView;
   // private SwipeRefreshLayout swipeRefreshLayout;

    private DatabaseReference mRef;

    private FirebaseRecyclerAdapter mAdapter;



    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OffersFragmentListener mListener;

    public OfferFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OfferFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OfferFragment newInstance(String param1, String param2) {
        OfferFragment fragment = new OfferFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        Log.d(TAG, "initializing mRef for database in onCreate");

        // Get the reference to the firebase database
        mRef = FirebaseDatabase.getInstance().getReference("testcoupons");


        // Get reference to the swipe refresh layout

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.offer_fragment, container, false);

        Log.d(TAG, "in OnCreateView setting up recycler view firebase");
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.offer_fragment_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        //recyclerView.setHasFixedSize(true);


        /*swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        if(swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(this);
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);

                }
            });
        }*/

        // Initialize the adapter with the firebase recycler adapter
        mAdapter = new FirebaseRecyclerAdapter<OfferSkyOffers, OfferHolder>(OfferSkyOffers.class, R.layout.offer_cardview, OfferHolder.class, mRef){

            @Override
            protected void populateViewHolder(OfferHolder viewHolder, OfferSkyOffers model, int position) {
                Log.d(TAG, "populating the offer view holder");
               /*viewHolder.setmBrandImageIv(model.getShopURL());
                viewHolder.setmMainImageIv(model.getPhotoURL());
                viewHolder.setmBrandTitleTv(model.getBrand());
                viewHolder.setmDescriptionTv(model.getDescription());*/
                viewHolder.bindViews(model);
            }
        };

        // Set the adapter to the recycler
        recyclerView.setAdapter(mAdapter);

        return rootView;
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
