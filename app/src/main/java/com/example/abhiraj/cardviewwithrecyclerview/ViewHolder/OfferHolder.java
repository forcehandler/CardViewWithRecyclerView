package com.example.abhiraj.cardviewwithrecyclerview.ViewHolder;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.abhiraj.cardviewwithrecyclerview.R;
import com.example.abhiraj.cardviewwithrecyclerview.models.OfferSkyOffers;
import com.example.abhiraj.cardviewwithrecyclerview.ui.DetailsActivity;
import com.squareup.picasso.Picasso;

/**
 * Created by Abhiraj on 27-01-2017.
 */

public class OfferHolder extends RecyclerView.ViewHolder {

    private final String TAG = OfferHolder.class.getSimpleName();
    private ImageView mMainImageIv;
    private ImageView mBrandImageIv;
    private TextView mBrandTitleTv;
    private TextView mDescriptionTv;
    private Button mSeeMoreBtn;

    public OfferHolder(final View itemView) {
        super(itemView);
        mMainImageIv = (ImageView) itemView.findViewById(R.id.main_image);
        mBrandImageIv = (ImageView) itemView.findViewById(R.id.brand_image);
        mBrandTitleTv = (TextView) itemView.findViewById(R.id.brand_text);
        mDescriptionTv = (TextView) itemView.findViewById(R.id.description_text);
        mSeeMoreBtn  =(Button) itemView.findViewById(R.id.see_more_btn);

        mSeeMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Toast.makeText(itemView.getContext(), "Coming Soon", Toast.LENGTH_LONG).show();*/
                Intent intent = new Intent(itemView.getContext(), DetailsActivity.class);
                itemView.getContext().startActivity(intent);
            }
        });
    }

    public void bindViews(OfferSkyOffers model)
    {
        setmBrandImageIv(model.getLogoURI());
        setmMainImageIv(model.getUid());
        setmBrandTitleTv(model.getTitle());
        setmDescriptionTv(model.getOffer());
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
