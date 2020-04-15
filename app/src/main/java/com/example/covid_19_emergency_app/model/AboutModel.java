package com.example.covid_19_emergency_app.model;

public class AboutModel {
    private String imageUrl;
    private String title;
    private String desc;

    public AboutModel(String image, String title, String desc) {
        this.imageUrl = image;
        this.title = title;
        this.desc = desc;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

}
