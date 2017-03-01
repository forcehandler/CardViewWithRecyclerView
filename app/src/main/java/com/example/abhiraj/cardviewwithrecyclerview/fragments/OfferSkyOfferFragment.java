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

import com.example.abhiraj.cardviewwithrecyclerview.database.OfferSkyHitchBeacon;
import com.example.abhiraj.cardviewwithrecyclerview.OfferSkyOfferVH;
import com.example.abhiraj.cardviewwithrecyclerview.R;
import com.example.abhiraj.cardviewwithrecyclerview.models.OfferSkyOffers;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OfferSkyOfferFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OfferSkyOfferFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OfferSkyOfferFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private final String TAG = OfferSkyOfferFragment.class.getSimpleName();

    private FirebaseRecyclerAdapter mAdapter;

    private RecyclerView recyclerView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public OfferSkyOfferFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OfferSkyOfferFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OfferSkyOfferFragment newInstance(String param1, String param2) {
        OfferSkyOfferFragment fragment = new OfferSkyOfferFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "in offersky frag on create");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "in offersky frag on create view");
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_offer_sky_offer, container, false);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.offersky_fragment_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        if(OfferSkyHitchBeacon.getInstance() != null)
        {
            Log.d(TAG, "ImageLoader returned is null");
        }
        mAdapter = new FirebaseRecyclerAdapter<OfferSkyOffers, OfferSkyOfferVH>(OfferSkyOffers.class,
                R.layout.offersky_offer_cardview, OfferSkyOfferVH.class,
                FirebaseDatabase.getInstance().getReference("testcoupons")){

            @Override
            protected void populateViewHolder(OfferSkyOfferVH viewHolder, OfferSkyOffers model, int position) {
                viewHolder.bindViews(model, position, OfferSkyHitchBeacon.getInstance().getImageLoader());
            }
        };


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
}