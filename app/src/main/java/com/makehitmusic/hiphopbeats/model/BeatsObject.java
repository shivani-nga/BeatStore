package com.makehitmusic.hiphopbeats.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BeatsObject {

    @SerializedName("item_id")
    private int itemId;
    @SerializedName("item_name")
    private String itemName;
    @SerializedName("item_description")
    private String itemDescription;
    @SerializedName("item_date")
    private  String itemDate;
    @SerializedName("item_duration")
    private  String itemDuration;
    @SerializedName("item_price")
    private  String itemPrice;
    @SerializedName("item_sample_path")
    private  String itemSamplePath;
    @SerializedName("item_image_small")
    private  String itemImageSmall;
    @SerializedName("item_image_big")
    private  String itemImageBig;
    @SerializedName("item_rating")
    private  int itemRating;
    @SerializedName("producer_id")
    private  int producerId;
    @SerializedName("producer_name")
    private  String producerName;
    @SerializedName("producer_company_name")
    private  String producerCompanyName;
    @SerializedName("producer_description")
    private  String producerDescription;
    @SerializedName("producer_image")
    private  String producerImage;
    @SerializedName("producer_friendly_url")
    private  String producerFriendlyUrl;
    @SerializedName("isLiked")
    private  String isLiked;
    @SerializedName("categories")
    private List<Category> beatsCategory;


    public BeatsObject(int itemId, String itemName, String itemDescription, String itemDate, String itemDuration,
                       String itemPrice, String itemSamplePath, String itemImageSmall, String itemImageBig, int itemRating,
                       int producerId, String producerName, String producerCompanyName, String producerDescription,
                       String producerImage, String producerFriendlyUrl, String isLiked, List<Category> beatsCategory) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.itemDate = itemDate;
        this.itemDuration = itemDuration;
        this.itemPrice = itemPrice;
        this.itemSamplePath = itemSamplePath;
        this.itemImageSmall = itemImageSmall;
        this.itemImageBig = itemImageBig;
        this.itemRating = itemRating;
        this.producerId = producerId;
        this.producerName = producerName;
        this.producerCompanyName = producerCompanyName;
        this.producerDescription = producerDescription;
        this.producerImage = producerImage;
        this.producerFriendlyUrl = producerFriendlyUrl;
        this.isLiked = isLiked;
        this.beatsCategory = beatsCategory;
    }

    public int getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public String getItemDate() {
        return itemDate;
    }

    public String getItemDuration() {
        return itemDuration;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public String getItemSamplePath() {
        return itemSamplePath;
    }

    public String getItemImageSmall() {
        return itemImageSmall;
    }

    public String getItemImageBig() {
        return itemImageBig;
    }

    public int getItemRating() {
        return itemRating;
    }

    public int getProducerId() {
        return producerId;
    }

    public String getProducerName() {
        return producerName;
    }

    public String getProducerCompanyName() {
        return producerCompanyName;
    }

    public String getProducerDescription() {
        return producerDescription;
    }

    public String getProducerImage() {
        return producerImage;
    }

    public String getProducerFriendlyUrl() {
        return producerFriendlyUrl;
    }

    public String getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(boolean isLiked) {
        this.isLiked = String.valueOf(isLiked);
    }

    public List<Category> getBeatsCategory() {
        return beatsCategory;
    }

}
