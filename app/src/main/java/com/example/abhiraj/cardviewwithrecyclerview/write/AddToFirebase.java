package com.example.abhiraj.cardviewwithrecyclerview.write;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.abhiraj.cardviewwithrecyclerview.R;

public class AddToFirebase extends AppCompatActivity implements View.OnClickListener{

    private Button state, city, mall, shop, coupon, categories;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_firebase);

        state = (Button) findViewById(R.id.add_state_btn);
        city = (Button) findViewById(R.id.add_city_btn);
        mall = (Button) findViewById(R.id.add_mall_btn);
        shop = (Button) findViewById(R.id.add_shop_btn);
        coupon = (Button) findViewById(R.id.add_coupon_btn);
        categories = (Button) findViewById(R.id.add_categories_btn);

        state.setOnClickListener(this);
        city.setOnClickListener(this);
        mall.setOnClickListener(this);
        shop.setOnClickListener(this);
        coupon.setOnClickListener(this);
        categories.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == state)
        {
            Intent intent= new Intent(this, AddState.class);
            startActivity(intent);
        }

        if(view == city)
        {
            Intent intent= new Intent(this, AddCity.class);
            startActivity(intent);
        }

        if(view == mall)
        {
            Intent intent= new Intent(this, AddMall.class);
            startActivity(intent);
        }

        if(view == shop)
        {
            Intent intent= new Intent(this, AddShop.class);
            startActivity(intent);
        }

        if(view == coupon)
        {
            Intent intent= new Intent(this, AddCoupons.class);
            startActivity(intent);
        }

        if(view == categories)
        {
            Intent intent= new Intent(this, AddCategories.class);
            startActivity(intent);
        }
    }
}
