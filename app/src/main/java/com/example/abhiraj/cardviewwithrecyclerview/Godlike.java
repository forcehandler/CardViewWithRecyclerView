package com.example.abhiraj.cardviewwithrecyclerview;

import android.app.Application;
import android.util.Log;

import com.example.abhiraj.cardviewwithrecyclerview.models.Coupon;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by Abhiraj on 11-02-2017.
 */

public class Godlike extends Application {

    private static final String TAG = Godlike.class.getSimpleName();
    private static DatabaseReference mRef;

    public static List<Coupon> mCouponList;

    @Override
    public void onCreate()
    {
        // Enable firebase offline caching
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        // Initialize mRef
        mRef = FirebaseDatabase.getInstance().getReference();


    }

    public static void getCoupons(String mallID /* add age and gender later*/)
    {
        mRef = FirebaseDatabase.getInstance().getReference();
        Query query = mRef.child(mallID).orderByChild("segment");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    if(ds.getValue(Coupon.class) instanceof Coupon)
                    {
                        Coupon coupon = ds.getValue(Coupon.class);
                        mCouponList.add(coupon);
                        Log.d(TAG, "coupon " + coupon.getBrand());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error while fetching coupons from firebase");
            }
        });

    }
}
