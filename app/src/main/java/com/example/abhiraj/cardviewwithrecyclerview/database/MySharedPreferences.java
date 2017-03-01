package com.example.abhiraj.cardviewwithrecyclerview.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.abhiraj.cardviewwithrecyclerview.models.Coupon;
import com.example.abhiraj.cardviewwithrecyclerview.models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Abhiraj on 18-01-2017.
 */

public class MySharedPreferences {

    public static void setPreferences(String key, String value, Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void setPreferences(String key, ArrayList<Coupon> coupons, Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(coupons);

        editor.putString(key, json);
        editor.commit();
    }

    public static String getPreferences(String key, Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(key, null);
    }

    public static ArrayList<Coupon> getCouponsPreferences(String key, Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key,null);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Coupon>>() {}.getType();
        ArrayList<Coupon> coupons = gson.fromJson(json, type);
        return coupons;
    }

    //testing code below
    public static void setUserPreference(String key, User user, Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString(key, json);
        editor.commit();
    }

    public static User getUserPreference(String key, Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key, null);
        Type type = new TypeToken<User>(){}.getType();
        Gson gson = new Gson();
        User user = gson.fromJson(json, type);
        return user;
    }
}
