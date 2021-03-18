package com.kis.coinmonitor.model.standardAPI;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.sql.Timestamp;
import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "data",
        "timestamp"
})
public class Assets {

    @JsonProperty("data")
    private List<Asset> assets = null;
    @JsonProperty("timestamp")
    private Timestamp timestamp;

    @JsonProperty("data")
    public List<Asset> getData() {
        return assets;
    }


    @JsonProperty("data")
    public void setData(List<Asset> data) {
        this.assets = data;
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