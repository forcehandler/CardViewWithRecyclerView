package com.example.abhiraj.cardviewwithrecyclerview.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.abhiraj.cardviewwithrecyclerview.R;
import com.example.abhiraj.cardviewwithrecyclerview.models.Coupon;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Abhiraj on 11-03-2017.
 */

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.ViewHolder> {

    private List<Coupon> mCouponList;
    private Context mContext;
    private CouponClickListener mCouponClickListener;

    public OfferAdapter(Context context, List<Coupon> couponList)
    {
        mContext = context;
        mCouponList = couponList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mOfferImageView;
        private TextView mOfferTextView;
        private CouponClickListener mClickListener;

        public ViewHolder(View itemView, CouponClickListener clickListener) {
            super(itemView);
            mOfferImageView = (ImageView) itemView.findViewById(R.id.offer_imagev);
            mOfferTextView = (TextView) itemView.findViewById(R.id.offer_tv);
            mClickListener = clickListener;
            itemView.setOnClickListener(this);
        }

        public void bindViews(Coupon coupon)
        {
            setOfferImageView(coupon.getPhotoURL());
            setOfferTextView(coupon.getDescription());
        }

        private void setOfferImageView(String url)
        {
            Picasso.with(itemView.getContext())
                    .load(url)
                    .error(R.drawable.error)
                    .placeholder(R.drawable.placeholder)
                    .fit()
                    .into(mOfferImageView);
        }

        private void setOfferTextView(String text)
        {
            mOfferTextView.setText(text);
        }

        @Override
        public void onClick(View view) {

            if(mClickListener != null)
            {
                mClickListener.onCouponClick(view, getAdapterPosition());
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view  = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.offer_cardview, parent, false);
        return new ViewHolder(view, mCouponClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Coupon coupon = mCouponList.get(position);
        holder.bindViews(coupon);
    }

    @Override
    public int getItemCount() {
        return mCouponList.size();
    }

    public void setCouponClickListener(CouponClickListener clickListener)
    {
        this.mCouponClickListener = clickListener;
    }

    public interface CouponClickListener
    {
        public void onCouponClick(View v, int position);
    }


}
