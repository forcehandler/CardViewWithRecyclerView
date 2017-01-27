package com.example.abhiraj.cardviewwithrecyclerview;

import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by Abhiraj on 17-01-2017.
 */

public class OnCouponClickListener implements OnCouponClick {
    @Override
    public void onShopImage(ImageView imageView) {
        return;
    }

    @Override
    public void onRedeemButton(Button button) {
        button.setText("Done");
    }
}
