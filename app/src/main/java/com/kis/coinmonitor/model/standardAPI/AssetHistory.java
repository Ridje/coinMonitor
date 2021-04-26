package com.kis.coinmonitor.model.standardAPI;

import java.sql.Timestamp;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "data",
        "timestamp"
})
public class AssetHistory {

    @JsonProperty("data")
    private List<AssetHistoryValue> data = null;
    @JsonProperty("timestamp")
    private Timestamp timestamp;

    @JsonProperty("data")
    public List<AssetHistoryValue> getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(List<AssetHistoryValue> data) {
        this.data = data;
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