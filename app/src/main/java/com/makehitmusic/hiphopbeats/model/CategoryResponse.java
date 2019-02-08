package com.makehitmusic.hiphopbeats.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CategoryResponse {

    @SerializedName("Categories")
    private List<Category> results;

    public List<Category> getResults() {
        return results;
    }

    public void setResults(List<Category> results) {
        this.results = results;
    }

}
