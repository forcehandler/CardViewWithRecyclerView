package com.example.abhiraj.cardviewwithrecyclerview.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhiraj.cardviewwithrecyclerview.R;
import com.example.abhiraj.cardviewwithrecyclerview.models.Shop;
import com.example.abhiraj.cardviewwithrecyclerview.ui.DetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by Abhiraj on 06-03-2017.
 */

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder> {

    private Context mContext;
    List<Shop> mShops;

    public ShopAdapter(Context context, List<Shop> shops)
    {
        mContext = context;
        mShops = shops;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        private final String TAG = ShopAdapter.ViewHolder.class.getSimpleName();
        private ImageView mMainImageIv;
        private ImageView mBrandImageIv;
        private TextView mBrandTitleTv;
        private TextView mDescriptionTv;
        private Button mSeeMoreBtn;

        public ViewHolder(final View itemView) {
            super(itemView);

            mMainImageIv = (ImageView) itemView.findViewById(R.id.main_image);
            mBrandImageIv = (ImageView) itemView.findViewById(R.id.brand_image);
            mBrandTitleTv = (TextView) itemView.findViewById(R.id.brand_text);
            mDescriptionTv = (TextView) itemView.findViewById(R.id.description_text);
            mSeeMoreBtn  =(Button) itemView.findViewById(R.id.see_more_btn);

            mSeeMoreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(itemView.getContext(), "Coming Soon", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(itemView.getContext(), DetailsActivity.class);
                    itemView.getContext().startActivity(intent);
                }
            });
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
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.offer_cardview, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindViews(mShops.get(position));
    }




    @Override
    public int getItemCount() {
        return mShops.size();
    }
}
