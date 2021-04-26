package com.kis.coinmonitor.model.standardAPI;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.kis.coinmonitor.utils.BigDecimalDeserializer;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "priceUsd",
        "time"
})
public class AssetHistoryValue {

    @JsonProperty("priceUsd")
    @JsonDeserialize(using = BigDecimalDeserializer.class)
    private BigDecimal priceUsd;

    @JsonProperty("time")
    private Timestamp time;

    @JsonIgnore
    @JsonProperty("date")
    private String date;

    @JsonIgnore
    @JsonProperty("circulatingSupply")
    private BigDecimal circulatingSupply;

    @JsonProperty("priceUsd")
    public BigDecimal getPriceUsd() {
        return priceUsd;
    }

    @JsonProperty("priceUsd")
    public void setPriceUsd(BigDecimal priceUsd) {
        this.priceUsd = priceUsd;
    }

    @JsonProperty("time")
    public Timestamp getTime() {
        return time;
    }

    @JsonProperty("time")
    public void setTime(Timestamp time) {
        this.time = time;
    }
}