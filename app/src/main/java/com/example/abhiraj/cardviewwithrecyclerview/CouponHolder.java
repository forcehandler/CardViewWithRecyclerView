package com.example.abhiraj.cardviewwithrecyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Abhiraj on 17-01-2017.
 */

public class CouponHolder extends RecyclerView.ViewHolder {

    private TextView mTitleTv, mDetailTv;
    private ImageView mImage;
    private Button mRedeemBtn;
    //private OnCouponClick mListener;
    private Context mContext;
    private int offerCode;
    //removed OnCouponClickListener from constructor's params
    public CouponHolder (View v)
    {
        super(v);
       // mListener = listener;
        mTitleTv = (TextView) v.findViewById(R.id.title);
        mDetailTv = (TextView) v.findViewById(R.id.details);
        mImage = (ImageView) v.findViewById(R.id.shop_image);
        mRedeemBtn = (Button) v.findViewById(R.id.redeemBtn);
        mContext = v.getContext();

        mRedeemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               mRedeemBtn.setText(offerCode+"");

            }
        });
        //mImage.setOnClickListener(this);
        //mRedeemBtn.setOnClickListener(this);
    }
    public void setTitle(String name)
    {
        Log.i("Couponholder", name + " is name fdsa");
        mTitleTv.setText(name);
    }
    public void setDetail(String detail)
    {
        Log.i("Couponholder", detail + " is detail fdsa");
        mDetailTv.setText(detail);
    }
    public void setImage(String url)
    {
        Log.i("Couponholder", url + " is url fdsa");
        Picasso.with(mContext).load(url)
                .error(R.drawable.error)
                .placeholder(R.drawable.placeholder)
                .resize(200,200)
                .centerCrop()
                .into(mImage);
    }
    public void setButtonValue()
    {
        mRedeemBtn.setText("Redeem");
    }
    public void setRedeemCode(int offer)
    {
        offerCode = offer;
    }


    /*public void onClick(View v)
    {
        if(v instanceof ImageView)
        {
            mListener.onShopImage((ImageView) v);
        }
        if(v instanceof Button)
        {
            mListener.onRedeemButton((Button) v);
        }
    }*/
}
