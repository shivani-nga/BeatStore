package com.makehitmusic.hiphopbeats.model;

public class ProducersObject {

    private String producerCover;
    private String producerTitle;
    private String producerAuthor;

    public ProducersObject(String producerTitle, String producerAuthor, String producerCover) {
        this.producerCover = producerCover;
        this.producerAuthor = producerAuthor;
        this.producerTitle = producerTitle;
    }

    public String getProducerCover() {
        return producerCover;
    }

    public String getProducerAuthor() {
        return producerAuthor;
    }

    public String getProducerTitle() {
        return producerTitle;
    }

}
