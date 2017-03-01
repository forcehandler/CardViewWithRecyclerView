package com.example.abhiraj.cardviewwithrecyclerview.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.abhiraj.cardviewwithrecyclerview.Constants;
import com.example.abhiraj.cardviewwithrecyclerview.database.MyFireBaseDatabase;
import com.example.abhiraj.cardviewwithrecyclerview.database.MySharedPreferences;
import com.example.abhiraj.cardviewwithrecyclerview.R;
import com.example.abhiraj.cardviewwithrecyclerview.RecyclerAdapter;
import com.example.abhiraj.cardviewwithrecyclerview.models.Coupon;
import com.example.abhiraj.cardviewwithrecyclerview.models.User;
import com.example.abhiraj.cardviewwithrecyclerview.signup.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.ISODateTimeFormat;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static Context contextOfApplication;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerAdapter mAdapter;
    private DatabaseReference mFirebaseRef;

    //Created dummy visitor value
    //TODO: Remove dummy visitor value
    int visitor_no = 5;

    private String TAG = "MainActivity";

    private ArrayList<Coupon> downloadedCoupons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contextOfApplication = getApplicationContext();

        mFirebaseRef = FirebaseDatabase
                .getInstance()
                .getReference(Constants.FirebaseKeys.COUPON_KEY);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        getCoupons();
    }

    public static Context getContextOfApplication()
    {
        return contextOfApplication;
    }



    public void getCoupons() {
        //TODO: Implement the logic that if coupons have been downloaded less than
        //24 hours ago then do not download them
        //instead show the user downloaded coupons only
        Log.i(TAG, "In getCoupons()");
        mFirebaseRef.addValueEventListener(new ValueEventListener() {
            ArrayList<Coupon> coupons = new ArrayList<>();

            public void onDataChange(DataSnapshot snapshot) {
                coupons.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Coupon couponSnapshot = postSnapshot.getValue(Coupon.class);
                    String coupon_key = postSnapshot.getKey();
                    couponSnapshot.setKey(coupon_key);
                    coupons.add(couponSnapshot);
                }
                Log.i(TAG, "Stored the coupons, calling notifyDataSetChanged()");

                //initializing adapter with selected coupons
                ArrayList<Coupon> selected_coupons = selectCouponsToAllot(coupons);
                mAdapter = new RecyclerAdapter(MainActivity.this, selected_coupons);
                mRecyclerView.setAdapter(mAdapter);

                //add selected coupons to the list of coupons allotted to user at some date
                allotCouponsToUser(selected_coupons);

                //To store all downloaded coupons (removed for memory reasons)
                //storeDownloadedCoupons(coupons);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
        Log.i(TAG, "returning from getCoupons");

    }

    //TODO: test allotCouponsToUser function
    public void allotCouponsToUser(ArrayList<Coupon> selected_coupons)
    {
        //time while allotting coupons
        DateTime now = new DateTime();

        //Set date time format
        DateTimeFormatter date_format = new DateTimeFormatterBuilder().append(ISODateTimeFormat.dateTimeNoMillis()).toFormatter().withOffsetParsed();

        //get the date time string according to the format specified
        String dateTimeNow = date_format.print(now);

        Log.i(TAG,date_format.print(now));

        //create array list of keys of coupons allotted
        ArrayList<String> allotted_coupon_keys = new ArrayList<>();
        for(Coupon coupon : selected_coupons)
        {
            allotted_coupon_keys.add(coupon.getKey());
        }

        //create a hash map of key as date and value as list of allotted coupon keys
        HashMap<String, ArrayList<String>> allotted_coupons_map = new HashMap<>();
        allotted_coupons_map.put(dateTimeNow, allotted_coupon_keys);

        //get the user object and assign to it the allotted coupon hash map
        User user = MySharedPreferences.getUserPreference(Constants.USER_PREF_KEY, getApplicationContext());
        //TODO: this only stores the recently allotted coupons
        user.setCouponsShown(allotted_coupons_map);

        //store the user back in shared preferences
        MySharedPreferences.setUserPreference(Constants.USER_PREF_KEY, user, getApplicationContext());

        //sync user data with firebase
        MyFireBaseDatabase.updateUserDatabase(user);
    }

    //TODO: test this function
    public ArrayList<Coupon> selectCouponsToAllot(ArrayList<Coupon> coupons)
    {
        ArrayList<Coupon> selectedCoupons = new ArrayList<>();
        int index = 4*(visitor_no-1)%coupons.size();
        for(int count = 0; count < 4;  index = (index+1)%coupons.size(),count++)
        {
            selectedCoupons.add(coupons.get(index));
        }
        return selectedCoupons;
    }

    /*public void storeDownloadedCoupons(ArrayList<Coupon> coupons)
    {
        MySharedPreferences.setPreferences(Constants.PREF_COUPON_KEY, coupons, this);

    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if(id == R.id.action_logout)
        {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    public void logout()
    {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}


