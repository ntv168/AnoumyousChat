package com.example.sam.anoumyouschat.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sam on 7/7/2017.
 */

public class ResponseData {
    @SerializedName("data")
    private Data data;

    public ResponseData() {
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
