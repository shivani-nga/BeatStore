package com.makehitmusic.hiphopbeats.model;

public class ReceiptRequest {

    private String orderId;
    private String restore;
    private String track_id;
    private String user_id;
    private String email;

    public ReceiptRequest(String orderId, String restore, String track_id,
                          String user_id, String email) {
        this.orderId = orderId;
        this.restore = restore;
        this.track_id = track_id;
        this.user_id = user_id;
        this.email = email;
    }

    public ReceiptRequest(String orderId, String track_id, String user_id, String email) {
        this.orderId = orderId;
        this.track_id = track_id;
        this.user_id = user_id;
        this.email = email;
    }

    public ReceiptRequest(String track_id, String user_id, String email) {
        this.track_id = track_id;
        this.user_id = user_id;
        this.email = email;
    }

}
