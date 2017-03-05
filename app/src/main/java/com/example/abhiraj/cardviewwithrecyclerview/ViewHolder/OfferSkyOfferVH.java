package com.example.abhiraj.cardviewwithrecyclerview.ViewHolder;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.example.abhiraj.cardviewwithrecyclerview.R;
import com.example.abhiraj.cardviewwithrecyclerview.models.OfferSkyOffers;
import com.example.abhiraj.cardviewwithrecyclerview.ui.FeedImageView;

/**
 * Created by Abhiraj on 01-02-2017.
 */

public class OfferSkyOfferVH extends RecyclerView.ViewHolder{

    TextView title, description;
    View lc;
    private FeedImageView offerImage;
    private FeedImageView offerImageLarge;

    public OfferSkyOfferVH(View itemView) {
        super(itemView);

        this.offerImageLarge = (FeedImageView) itemView.findViewById(R.id.offer_image);
        this.title = (TextView) itemView.findViewById(R.id.note_item_title);
        this.description = (TextView) itemView.findViewById(R.id.note_item_desc);
        this.offerImage = (FeedImageView) itemView.findViewById(R.id.imageViewfav);
        this.lc = itemView.findViewById(R.id.line_color);
    }


    public void bindViews(OfferSkyOffers offer, int position, ImageLoader loader) {
        title.setText(offer.getTitle());
        offerImageLarge.setImageUrl(offer.getUid(), loader);
        description.setText(offer.getOffer());
        offerImage.setImageUrl(offer.getLogoURI(), loader);

        switch(position%4)
        {
            case 0 : lc.setBackgroundColor(Color.parseColor("#F44336"));
                break;
            case 1 : lc.setBackgroundColor(Color.parseColor("#1976D2"));
                break;
            case 2 : lc.setBackgroundColor(Color.parseColor("#F57C00"));
                break;
            case 3 : lc.setBackgroundColor(Color.parseColor("#CDDC39"));
                break;
            default:lc.setBackgroundColor(Color.parseColor("#E040FB"));
        }
    }
}
