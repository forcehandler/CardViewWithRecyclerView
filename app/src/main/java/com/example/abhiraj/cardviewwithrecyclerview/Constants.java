package com.example.abhiraj.cardviewwithrecyclerview;

/**
 * Created by Abhiraj on 17-01-2017.
 */

public class Constants {

    public static String USER_PREF_KEY = "my_key";
    public static String PREF_COUPON_KEY = "stored_coupons";
    public static String USER_DEFAULT_PASSWORD = "phone authentication1";
    public static String USER_DEFAULT_EMAIL_DOMAIN = "@offersky.com";
    public static String BOTTOM_MENU_CLOTHES_MENU = "clothes_menu";
    public static String BOTTOM_MENU_FOOD_MENU = "food_menu";

    public interface FirebaseKeys
    {
        static String COUPON_KEY = "coupons";
        static String USER_KEY = "users";
    }

    public interface Warnings
    {

        static String CANNOT_FETCH_COUPONS = "cannot fetch coupons";
    }

    public interface Broadcasts
    {
        static String BROADCAST_SHOP_UPDATE = "shop_update";
    }

    public interface IntentKeys
    {
        static String CLICKED_SHOP_KEY = "shop_position";
    }
}
