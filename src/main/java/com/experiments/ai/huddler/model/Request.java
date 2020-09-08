package com.experiments.ai.huddler.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Request {

    private long itemId;

    private long category;

    private int numOfResults;

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public long getCategory() {
        return category;
    }

    public void setCategory(long category) {
        this.category = category;
    }

    public int getNumOfResults() {
        return numOfResults;
    }

    public void setNumOfResults(int numOfResults) {
        this.numOfResults = numOfResults;
    }
}
