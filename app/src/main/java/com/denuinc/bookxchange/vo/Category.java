package com.denuinc.bookxchange.vo;

/**
 * Created by Florian on 3/25/2018.
 */

public class Category {

    private int imageId;
    private int descriptionId;
    private String search;

    public Category(int imageId, int descriptionId, String search) {
        this.imageId = imageId;
        this.descriptionId = descriptionId;
        this.search = search;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getDescriptionId() {
        return descriptionId;
    }

    public void setDescriptionId(int descriptionId) {
        this.descriptionId = descriptionId;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
