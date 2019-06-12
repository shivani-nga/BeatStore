package com.makehitmusic.hiphopbeats.model;

import com.google.gson.annotations.SerializedName;

public class FavouriteResponse {

    @SerializedName("message")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
