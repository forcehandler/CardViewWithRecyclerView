package com.example.abhiraj.cardviewwithrecyclerview;

/**
 * Created by Abhiraj on 01-02-2017.
 */

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class OfferSkyOffers {

    private String title;
    private String Offer;
    private Boolean discovered;
    private String hitchId;
    private String uid;
    private String logoURI;
    private String segment;

    public OfferSkyOffers() {
    }

    public OfferSkyOffers(String title, String Offer, Boolean discovered, String hitchId,String uid,String segment, String logoURI) {
        this.title = title;
        this.Offer = Offer;
        this.discovered = discovered;
        this.hitchId = hitchId;
        this.uid  = uid;
        this.segment = segment;
        this.logoURI = logoURI;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOffer() {
        return Offer;
    }

    public void setOffer(String Offer) {
        this.Offer = Offer;
    }


    public String getHitchId() {
        return hitchId;
    }

    public void setHitchId(String hitchId) {
        this.hitchId = hitchId;
    }


    public String getLogoURI() {
        return logoURI;
    }

    public void setLogoURI(String logoURI) {
        this.logoURI = logoURI;
    }

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }

    public String getUid() {return uid;}

    public void setUid(String uid) {this.uid = uid;}

    public Boolean getDiscovered() {
        return discovered;
    }

    public void setDiscovered(Boolean discovered) {
        this.discovered = discovered;
    }

}
