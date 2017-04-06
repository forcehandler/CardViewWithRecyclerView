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

    public static final int REQUEST_CHECK_LOCATION_SETTINGS = 121;


    public interface Permission
    {
        static final int ACCESS_FINE_LOCATION_PERMISSION = 999;
    }

    public interface Geofence
    {
        static final String LATITUDE = "latitude";
        static final String LONGITUDE = "longitude";
        static final String RADIUS = "radius";

        static final String GEOFENCE_REQUEST_ID = "my geofence";

        static final String GEOFENCE_ENTER_BROADCAST = "geofence_enter";
        static final String GEOFENCE_EXIT_BROADCAST = "geofence_exit";

        static final String GEOFENCE_CREATED = "GeofenceCreated";

        static final String GOOGLE_API_CONNECTED = "APIConnected";
    }

    public interface Location
    {
        static final String GPS_ENABLED = "GPSEnabled";
        static final String GPS_STATE_ON_BROADCAST = "GPS turned on";
        static final String GPS_STATE_OFF_BROADCAST = "GPS turned off";

    }

    public interface SharedPreferences
    {
        static final String STEPS_FILE = "steps_file";
        static final String STEPS = "stores_steps";
        static final String MALL_ID = "mallId";
        static final String USER_PREF_FILE = "user_pref_file";
    }

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
        static final String BROADCAST_STEPS = "steps_broadcast";
        static final String STEPS = "steps";
    }

    public interface IntentKeys
    {
        static String CLICKED_SHOP_KEY = "shop_position";
    }

    public interface FoodCategoryKeys
    {
        static String FOOD_CATEGORY_1 = "Chaat";
        static String FOOD_CATEGORY_2 = "Icecream";
        static String FOOD_CATEGORY_3 = "Drinks";
    }

    public interface ClothesCategoryKeys
    {
        static String CLOTHES_CATEGORY_1 = "Formals";
        static String CLOTHES_CATEGORY_2 = "Shoes";
        static String CLOTHES_CATEGORY_3 = "Sarees";
    }
}
