package com.example.abhiraj.cardviewwithrecyclerview.models;

/**
 * Created by Abhiraj on 27-01-2017.
 */

public class Offer {

    private String key;
    private String brand;
    private String description;
    private String shopURL;
    private String category;
    private String photoURL;

    public Offer(String key, String brand, String description, String shopURL, String category, String photoURL) {
        this.key = key;
        this.brand = brand;
        this.description = description;
        this.shopURL = shopURL;
        this.category = category;
        this.photoURL = photoURL;
    }

    public Offer(){}

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShopURL() {
        return shopURL;
    }

    public void setShopURL(String shopURL) {
        this.shopURL = shopURL;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


}
