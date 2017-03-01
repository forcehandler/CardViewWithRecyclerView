package com.example.abhiraj.cardviewwithrecyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.abhiraj.cardviewwithrecyclerview.database.MyFireBaseDatabase;
import com.example.abhiraj.cardviewwithrecyclerview.database.MySharedPreferences;
import com.example.abhiraj.cardviewwithrecyclerview.models.Coupon;
import com.example.abhiraj.cardviewwithrecyclerview.models.User;
import com.example.abhiraj.cardviewwithrecyclerview.ui.MainActivity;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.ISODateTimeFormat;

import java.util.ArrayList;

/**
 * Created by Abhiraj on 16-01-2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.DataHolder> {
    private static Context mContext;
    private static ArrayList<Coupon> coupons = new ArrayList<>();
    private DatabaseReference databaseReference;
    private static String TAG = RecyclerAdapter.class.getSimpleName();

    private static User user = MySharedPreferences.getUserPreference(Constants.USER_PREF_KEY, MainActivity.getContextOfApplication());

    public RecyclerAdapter(Context context, ArrayList<Coupon> coupons)
    {
        mContext = context;
        this.coupons = coupons;
        Log.i(TAG, "Calling getCoupons()");

    }


    public static class DataHolder extends RecyclerView.ViewHolder
    {
        private TextView mTitleTv, mDescriptionTv;
        private ImageView mImage;
        private Button mRedeemBtn;
        private String coupon_code;



        public DataHolder (View v)
        {
            super(v);

            mTitleTv = (TextView) v.findViewById(R.id.title);
            mDescriptionTv = (TextView) v.findViewById(R.id.details);
            mImage = (ImageView) v.findViewById(R.id.shop_image);
            mRedeemBtn = (Button) v.findViewById(R.id.redeemBtn);
           // mImage.setOnClickListener(this);
            mRedeemBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button b = (Button) v;
                    setButtonTextOnClick(b, getAdapterPosition());

                }
            });
        }
        private void setButtonTextOnClick(Button button, int position)
        {
            //to check if this coupon is being redeemed for the first time
            //to prevent adding same keys multiple times
            //if button says redeem, it implies that the coupon has not been redeemed
            if(button.getText().equals(mContext.getString(R.string.redeem_btn))) {
                //get coupon key for which the code was revealed
                String coupon_key = coupons.get(position).getKey();
                //get date and time when the coupon was redeemed
                DateTime now = new DateTime();
                DateTimeFormatter date_format = new DateTimeFormatterBuilder().append(ISODateTimeFormat.dateTimeNoMillis()).toFormatter().withOffsetParsed();

                Log.i(TAG,date_format.print(now));
                user.getCoupons_redeemed().put(date_format.print(now), coupon_key);
                MySharedPreferences.setUserPreference(Constants.USER_PREF_KEY, user, mContext);
                MyFireBaseDatabase.updateUserDatabase(user);

                button.setText(coupon_code + "");
            }
        }


    }

    @Override
    public RecyclerAdapter.DataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item_row, parent,false);

        return new DataHolder(view);
    }

    @Override
    public void onBindViewHolder(DataHolder holder, int position) {
        holder.mTitleTv.setText(coupons.get(position).getBrand());
        holder.mDescriptionTv.setText(coupons.get(position).getDescription());
        Picasso.with(mContext).load(coupons.get(position).getShopURL())
                .error(R.drawable.error)
                .placeholder(R.drawable.placeholder)
                .fit()
                .centerCrop()
                .into(holder.mImage);
        holder.coupon_code = coupons.get(position).getcode();

        //checks if the user has already redeemed the coupon
        setButtonTextOnViewCreation(holder.mRedeemBtn, position);

    }

    @Override
    public int getItemCount() {
        return coupons.size();
    }

    private void setButtonTextOnViewCreation(Button redeem_btn, int position)
    {
        Coupon current_coupon = coupons.get(position);

        if(user.getCoupons_redeemed() == null)
        {
            redeem_btn.setText(mContext.getString(R.string.redeem_btn));
        }
        else if(user.getCoupons_redeemed().containsValue(current_coupon.getKey()))
        {
            redeem_btn.setText(current_coupon.getcode()+"");
        }
        else {
            redeem_btn.setText(mContext.getString(R.string.redeem_btn));
        }
    }



    /*private boolean isCouponAllottedToUser(int position)
    {
        //checks whether the coupon being populated has been allotted to the user
        String coupon_key = coupons.get(position).getKey();
        return user.getCoupons_shown().containsValue(coupon_key);
    }

    private void allotCouponToUser(int position)
    {
        if(!isCouponAllottedToUser(position))
        {
            DateTime now = new DateTime();
            String coupon_key = coupons.get(position).getKey();
            user.getCoupons_shown().put(now.toString(), coupon_key);
        }
    }*/
}
