package com.example.abhiraj.cardviewwithrecyclerview.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.abhiraj.cardviewwithrecyclerview.R;
import com.example.abhiraj.cardviewwithrecyclerview.models.Shop;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by Abhiraj on 06-03-2017.
 */

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder> {

    private Context mContext;
    private List<Shop> mShops;
    private ShopClickListener mShopClickListener;

    public ShopAdapter(Context context, List<Shop> shops)
    {
        mContext = context;
        mShops = shops;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final String TAG = ShopAdapter.ViewHolder.class.getSimpleName();
        private ImageView mMainImageIv;
        private ImageView mBrandImageIv;
        private TextView mBrandTitleTv;
        private TextView mDescriptionTv;
        private Button mSeeMoreBtn;
        private ShopClickListener mClickListener;

        public ViewHolder(final View itemView, ShopClickListener clickListener) {
            super(itemView);

            mMainImageIv = (ImageView) itemView.findViewById(R.id.main_image);
            mBrandImageIv = (ImageView) itemView.findViewById(R.id.brand_image);
            mBrandTitleTv = (TextView) itemView.findViewById(R.id.brand_text);
            mDescriptionTv = (TextView) itemView.findViewById(R.id.description_text);
            mSeeMoreBtn  =(Button) itemView.findViewById(R.id.see_more_btn);
            mClickListener = clickListener;
            mSeeMoreBtn.setOnClickListener(this);
        }

        public void bindViews(Shop model)
        {
            setmBrandImageIv(model.getShopImageURL());
            setmMainImageIv(model.getBrandImageURL());
            setmBrandTitleTv(model.getName());

        }

        public void setmMainImageIv(String url)
        {
            Log.d(TAG, "setting the main image");
            Picasso.with(itemView.getContext())
                    .load(url)
                    .error(R.drawable.error)
                    .placeholder(R.drawable.placeholder)
                    .fit()
                    .into(mMainImageIv);
        }

        public void setmBrandImageIv(String url)
        {
            Log.d(TAG, "setting the main image");
            Picasso.with(itemView.getContext())
                    .load(url)
                    .fit()
                    .into(mBrandImageIv);
        }

        public void setmBrandTitleTv(String title)
        {
            mBrandTitleTv.setText(title);
        }

        public void setmDescriptionTv(String description)
        {
            mDescriptionTv.setText(description);
        }

        @Override
        public void onClick(View view) {

            if(mClickListener != null)
            {
                mClickListener.onShopClick(view, getAdapterPosition());
            }
        }
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shop_cardview, parent,false);
        return new ViewHolder(view, mShopClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindViews(mShops.get(position));
    }


    public void setShopClickListener(ShopClickListener clickListener)
    {
        this.mShopClickListener = clickListener;
    }

    @Override
    public int getItemCount() {
        return mShops.size();
    }

    public interface ShopClickListener
    {
        public void onShopClick(View view, int position);
    }
}
