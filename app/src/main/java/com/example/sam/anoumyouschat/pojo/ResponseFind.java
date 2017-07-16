package com.example.sam.anoumyouschat.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sam on 7/7/2017.
 */

public class ResponseFind {
    @SerializedName("data")
    private Data data;

    public ResponseFind() {
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
