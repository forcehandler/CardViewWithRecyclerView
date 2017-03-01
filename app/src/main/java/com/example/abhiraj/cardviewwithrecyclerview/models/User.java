package com.example.abhiraj.cardviewwithrecyclerview.models;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Abhiraj on 18-01-2017.
 */

public class User {

    private String uid;
    private String name;
    private String age;
    //key is the date and time when the coupon was redeemed
    private HashMap<String, String> coupons_redeemed;
    private HashMap<String, ArrayList<String>> coupons_shown;
    private String phone_no;
    private String gender;
    private String blood_group;



    public User(){

    }

    public User(String uid, String name, String age, String phone_no, String gender, String blood_group)
    {
        this.uid = uid;
        this.name = name;
        this.age = age;
        this.coupons_shown = new HashMap<>();
        this.phone_no = phone_no;
        this.gender = gender;
        this.blood_group = blood_group;

        this.coupons_redeemed = new HashMap<String, String>();

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public HashMap<String, String> getCoupons_redeemed() {
        return coupons_redeemed;
    }

    public void setCoupons_redeemed(HashMap<String, String> coupons_redeemed) {
        this.coupons_redeemed = coupons_redeemed;
    }

    public String getBlood_group() {
        return blood_group;
    }

    public void setBlood_group(String blood_group) {
        this.blood_group = blood_group;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public HashMap<String, ArrayList<String>> getCoupons_shown() {
        return coupons_shown;
    }

    public void setCouponsShown(HashMap<String, ArrayList<String>> coupons_shown) {
        this.coupons_shown = coupons_shown;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }
}
