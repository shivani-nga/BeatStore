package com.makehitmusic.hiphopbeats.presenter;

import com.google.gson.annotations.SerializedName;

public class JsonResponse {

    @SerializedName("youTubeVideoLink")
    private String youtubeLink;

    public String getYoutubeLink() {
        return youtubeLink;
    }

    public void setYoutubeLink(String youtubeLink) {
        this.youtubeLink = youtubeLink;
    }

}
