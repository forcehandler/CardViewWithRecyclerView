package com.example.abhiraj.cardviewwithrecyclerview;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.abhiraj.cardviewwithrecyclerview.models.Coupon;
import com.example.abhiraj.cardviewwithrecyclerview.models.Mall;
import com.example.abhiraj.cardviewwithrecyclerview.models.Shop;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abhiraj on 11-02-2017.
 */

public class Godlike extends Application{

    private static final String TAG = Godlike.class.getSimpleName();
    private static DatabaseReference mRef;

    public static List<Coupon> mCouponList;
    public static List<Shop> mShopsList;
    public static Mall sMall;

    private static Context sContext;


    @Override
    public void onCreate() {
        super.onCreate();
    /* Enable disk persistence  */
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    public static void getCoupons(Context context, String mallID /* add age and gender later*/)
    {
        sContext = context;
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

    public static void getShops(Context context, String mallID /* add age and gender later*/)
    {

        sContext = context;
        mShopsList = new ArrayList<>();
        mRef = FirebaseDatabase.getInstance().getReference("malls/"+mallID+"/shops");
        Query query = mRef;
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Shop> alteredShops = new ArrayList<Shop>();
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    if(ds.getValue(Shop.class) instanceof Shop)
                    {
                        Shop shop = ds.getValue(Shop.class);
                        shop.setKey(ds.getKey());

                        if(BuildConfig.DEBUG){
                            Log.d(TAG, "shop name = " + shop.getName());
                            Log.d(TAG, "shop key = " + shop.getKey());
                        }

                        alteredShops.add(shop);

                    }
                }
                mShopsList = alteredShops;
                LocalBroadcastManager.getInstance(sContext).sendBroadcast(new Intent(Constants.Broadcasts.BROADCAST_SHOP_UPDATE));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error while fetching coupons from firebase");
            }
        });

    }

    public static Mall getMall(Context context, String mallId){
        sContext = context;
        mShopsList = new ArrayList<>();
        mRef = FirebaseDatabase.getInstance().getReference("malls/"+mallId);
        Query query = mRef;
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue(Mall.class) instanceof Mall){
                    Log.d(TAG, "datasnapshot is an instance of mall");
                    sMall = dataSnapshot.getValue(Mall.class);
                }
                LocalBroadcastManager.getInstance(sContext).sendBroadcast(new Intent(Constants.Broadcasts.BROADCAST_SHOP_UPDATE));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error while fetching coupons from firebase");
            }
        });
        return sMall;
    }

}
