package com.makehitmusic.hiphopbeats.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Category {

    @SerializedName("category_name")
    private String categoryName;
    @SerializedName("category_image_large")
    private String categoryImageLarge;
    @SerializedName("category_image")
    private String categoryImage;
    @SerializedName("category_id")
    private  int categoryId;
    @SerializedName("category_order")
    private  int categoryOrder;

    public Category(String categoryName, String categoryImageLarge, String categoryImage, int categoryId, int categoryOrder) {
        this.categoryName = categoryName;
        this.categoryImage = categoryImage;
        this.categoryImageLarge = categoryImageLarge;
        this.categoryId = categoryId;
        this.categoryOrder = categoryOrder;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryImageLarge(String categoryImageLarge) {
        this.categoryImageLarge = categoryImageLarge;
    }

    public String getCategoryImageLarge() {
        return categoryImageLarge;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryOrder(int categoryOrder) {
        this.categoryOrder = categoryOrder;
    }

    public int getCategoryOrder() {
        return categoryOrder;
    }

}
