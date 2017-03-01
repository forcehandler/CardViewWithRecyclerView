package com.example.abhiraj.cardviewwithrecyclerview.database;

import android.util.Log;

import com.example.abhiraj.cardviewwithrecyclerview.Constants;
import com.example.abhiraj.cardviewwithrecyclerview.models.Coupon;
import com.example.abhiraj.cardviewwithrecyclerview.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Abhiraj on 17-01-2017.
 */

public class MyFireBaseDatabase {

    private static String TAG = "MyFirebaseDatabase";

    //Retrieves coupons available on server and returns it as an array list of Coupons
    //TODO: implement no_of_coupons in Database get function
    public static void getCouponsFromFirebase(final String childKey, int no_of_coupons)
    {
        DatabaseReference mRef = FirebaseDatabase
                .getInstance()
                .getReference(childKey);

        //stores coupons retrieved from firebase
        final ArrayList<Coupon> list_of_coupons = new ArrayList<>();

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot couponSnapshot : dataSnapshot.getChildren())
                {
                    Log.i(TAG, "fetching coupons in database");
                   list_of_coupons.add(couponSnapshot.getValue(Coupon.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, Constants.Warnings.CANNOT_FETCH_COUPONS);
            }
        });



    }


    public static void updateUserDatabase(User user)
    {
        DatabaseReference mRef = FirebaseDatabase
                .getInstance()
                .getReference();
        mRef.child(Constants.FirebaseKeys.USER_KEY).child(user.getPhone_no()).
                setValue(user);
    }
}
