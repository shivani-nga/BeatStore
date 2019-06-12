package com.makehitmusic.hiphopbeats.model;

public class FavouriteRequest {

    private String product_id;
    private String user_id;
    private boolean status;

    public FavouriteRequest(String product_id, String user_id, boolean status) {
        this.product_id = product_id;
        this.user_id = user_id;
        this.status = status;
    }

}
