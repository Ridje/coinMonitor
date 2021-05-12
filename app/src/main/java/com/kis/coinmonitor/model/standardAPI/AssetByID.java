package com.kis.coinmonitor.model.standardAPI;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.sql.Timestamp;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "data",
        "timestamp"
})
public class AssetByID {

    @JsonProperty("data")
    private Asset asset = null;
    @JsonProperty("timestamp")
    private Timestamp timestamp;

    @JsonProperty("data")
    public Asset getAsset() {
        return asset;
    }

    @JsonProperty("data")
    public void setAsset(Asset data) {
        this.asset = data;
    }

    @JsonProperty("timestamp")
    public Timestamp getTimestamp() {
        return timestamp;
    }

    @JsonProperty("timestamp")
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

}
