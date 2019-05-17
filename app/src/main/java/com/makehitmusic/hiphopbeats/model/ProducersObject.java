package com.makehitmusic.hiphopbeats.model;

import com.google.gson.annotations.SerializedName;

public class ProducersObject {

    @SerializedName("producer_id")
    private String producerId;
    @SerializedName("producer_name")
    private String producerName;
    @SerializedName("producer_company_name")
    private String producerCompanyName;
    @SerializedName("producer_description")
    private String producerDescription;
    @SerializedName("producer_image")
    private String producerImage;

    public ProducersObject(String producerId, String producerName, String producerCompanyName, String producerDescription, String producerImage) {
        this.producerId = producerId;
        this.producerName = producerName;
        this.producerCompanyName = producerCompanyName;
        this.producerDescription = producerDescription;
        this.producerImage = producerImage;
    }

    public String getProducerId() {
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

}
