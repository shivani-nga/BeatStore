package com.makehitmusic.hiphopbeats.model;

public class BeatsObject {

    private String beatCover;
    private String beatTitle;
    private String beatAuthor;

    public BeatsObject(String beatTitle, String beatAuthor, String beatCover) {
        this.beatCover = beatCover;
        this.beatAuthor = beatAuthor;
        this.beatTitle = beatTitle;
    }

    public String getBeatCover() {
        return beatCover;
    }

    public String getBeatAuthor() {
        return beatAuthor;
    }

    public String getBeatTitle() {
        return beatTitle;
    }

}
