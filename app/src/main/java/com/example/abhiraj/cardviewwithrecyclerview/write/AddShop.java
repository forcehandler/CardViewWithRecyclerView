package com.example.abhiraj.cardviewwithrecyclerview.write;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.abhiraj.cardviewwithrecyclerview.R;
import com.example.abhiraj.cardviewwithrecyclerview.models.Shop;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddShop extends AppCompatActivity implements View.OnClickListener {

    EditText state_key_et, city_key_et, mall_key_et, name_et, phone_et, email_et, location_et, shopURL_et, brandURL_et, shop_key_et;
    Button okBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shop);

        state_key_et = (EditText) findViewById(R.id.state_key);
        mall_key_et = (EditText) findViewById(R.id.mall_key);
        city_key_et = (EditText) findViewById(R.id.city_key);
        shop_key_et = (EditText) findViewById(R.id.shop_key);
        name_et = (EditText) findViewById(R.id.shop_name);
        phone_et = (EditText) findViewById(R.id.shop_phone);
        email_et = (EditText) findViewById(R.id.shop_email);
        location_et = (EditText) findViewById(R.id.shop_location);
        shopURL_et = (EditText) findViewById(R.id.shop_image_url);
        brandURL_et = (EditText) findViewById(R.id.brand_image_url);

        okBtn = (Button) findViewById(R.id.okBtn);

        okBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        if(view == okBtn)
        {
            String state_key = state_key_et.getText().toString();
            String city_key = city_key_et.getText().toString();
            String mall_key = mall_key_et.getText().toString();
            String final_key = state_key + "_" + city_key + "_" + mall_key;

            String shop_key = shop_key_et.getText().toString();
            String name = name_et.getText().toString();
            String phone = phone_et.getText().toString();
            String email =  email_et.getText().toString();
            String location = location_et.getText().toString();
            String shopUrl = shopURL_et.getText().toString();
            String brandUrl = brandURL_et.getText().toString();

            Shop shop = new Shop();
            shop.setName(name);
            shop.setLocation(location);
            shop.setPhone(phone);
            shop.setEmail(email);
            shop.setBrandImageURL(brandUrl);
            shop.setShopImageURL(shopUrl);

            DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("malls");
            mRef.child(final_key).child("shops").child(shop_key).setValue(shop);
        }
    }




}



   /* TextInputEditText mNameEt;
    TextInputEditText mPhoneEt;
    TextInputEditText mEmailEt;
    Button done;
    Button addCoupon;

    String name;
    String phone;
    String email;
    List<Coupon> coupons;
    String location;
    List<String> categories;
    String shopImageURL;
    String brandImageURL;

    private static int shopId = 0;*/

 /*mNameEt = (TextInputEditText) findViewById(R.id.shopNameET);
        mPhoneEt = (TextInputEditText) findViewById(R.id.phoneET);
        mEmailEt = (TextInputEditText) findViewById(R.id.emailET);
        done = (Button) findViewById(R.id.doneBtn);
        addCoupon = (Button) findViewById(R.id.addCouponBtn);

        done.setOnClickListener(this);
        addCoupon.setOnClickListener(this);

        coupons = new ArrayList<>();

        phone = "156456";
        email = "fdsaf@fdsa.com";
        name = "NikeShop";
        location = "goa";
        categories = new ArrayList<>();
        categories.add("clothes");
        Coupon coupon = new Coupon();
        coupon.setBrand("Nike");
        coupon.setCategory("clothes");
        coupon.setcode("getnike20");
        coupon.setDescription("buyone get one");
        coupon.setPrice("150");
        coupon.setPhotoURL("fdsadsa");
        coupon.setShopURL("hfsahfsag");

        Coupon coupon1 = new Coupon();
        coupon1.setBrand("Nike");
        coupon1.setCategory("Clothes");
        coupon1.setcode("getnike20");
        coupon1.setDescription("buyone get one");
        coupon1.setPrice("150");
        coupon1.setPhotoURL("fdsadsa");
        coupon1.setShopURL("hfsahfsag");

        coupons.add(coupon);
        coupons.add(coupon1);


        Shop shop = new Shop();
        shop.setCoupons(coupons);
        shop.setName(name);
        shop.setPhone(phone);
        shop.setBrandImageURL("http://res.cloudinary.com/crosscharge/image/upload/v1485304866/nike_logo_z0fp3h.webp");
        shop.setShopImageURL("http://res.cloudinary.com/crosscharge/image/upload/v1485304866/nike_logo_z0fp3h.webp");
        List<String> foodCategories = new ArrayList<>();
        foodCategories.add("food");
        shop.setCategories(foodCategories);
        shop.setLocation(location);

        Shop shop1 = new Shop();
        shop1.setCoupons(coupons);
        shop1.setName("FoodShop");
        shop1.setPhone(phone);
        shop1.setBrandImageURL("http://res.cloudinary.com/crosscharge/image/upload/v1485304866/nike_logo_z0fp3h.webp");
        shop1.setShopImageURL("http://res.cloudinary.com/crosscharge/image/upload/v1485304866/nike_logo_z0fp3h.webp");
        shop1.setCategories(categories);
        shop1.setLocation(location);

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        mRef.child("GA_0832_MDG").child("shops").child(shopId + "").setValue(shop);
        shopId++;
        mRef.child("GA_0832_MDG").child("shops").child(shopId + "").setValue(shop1);
        mRef.child("yolo").push().setValue("gdsagdsa");
        Log.d("addshop", "done");*/