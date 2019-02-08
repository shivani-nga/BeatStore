package com.makehitmusic.hiphopbeats.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Category {

    @SerializedName("category_name")
    private String categoryName;
    @SerializedName("category_image")
    private String categoryImage;

    public Category(String categoryName, String categoryImage) {
        this.categoryName = categoryName;
        this.categoryImage = categoryImage;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

}
