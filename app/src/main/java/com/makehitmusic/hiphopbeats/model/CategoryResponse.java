package com.makehitmusic.hiphopbeats.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CategoryResponse {

    @SerializedName("Categories")
    private List<Category> categoryResults;

    @SerializedName("Products")
    private ArrayList<BeatsObject> beatsResults;

    @SerializedName("Producers")
    private ArrayList<ProducersObject> producersResults;

    public List<Category> getCategoryResults() {
        return categoryResults;
    }

    public void setCategoryResults(List<Category> categoryResults) {
        this.categoryResults = categoryResults;
    }

    public ArrayList<BeatsObject> getBeatsResults() {
        return beatsResults;
    }

    public void setBeatsResults(ArrayList<BeatsObject> beatsResults) {
        this.beatsResults = beatsResults;
    }

    public ArrayList<ProducersObject> getProducersResults() {
        return producersResults;
    }

    public void setProducersResults(ArrayList<ProducersObject> producersResults) {
        this.producersResults = producersResults;
    }

}
