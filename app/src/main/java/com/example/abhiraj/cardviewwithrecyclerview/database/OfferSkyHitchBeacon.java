package com.example.abhiraj.cardviewwithrecyclerview.database;

import android.app.Application;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.abhiraj.cardviewwithrecyclerview.OfferSkyLruBitmapCache;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Abhiraj on 01-02-2017.
 */

public class OfferSkyHitchBeacon extends Application {

    private final String TAG = OfferSkyHitchBeacon.class.getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    OfferSkyLruBitmapCache mLruBitmapCache;
    private static OfferSkyHitchBeacon mInstance;

    @Override
    public void onCreate() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mInstance = this;}

    public static synchronized OfferSkyHitchBeacon getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        Log.d(TAG, "getRequestQueue");
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        Log.d(TAG, "getImageLoader");
        getRequestQueue();
        if (mImageLoader == null) {
            getLruBitmapCache();
            mImageLoader = new ImageLoader(this.mRequestQueue, mLruBitmapCache);
        }

        return this.mImageLoader;
    }

    public OfferSkyLruBitmapCache getLruBitmapCache() {
        Log.d(TAG, "getLruBitmapCache");
        if (mLruBitmapCache == null)
            mLruBitmapCache = new OfferSkyLruBitmapCache();
        return this.mLruBitmapCache;
    }
}
