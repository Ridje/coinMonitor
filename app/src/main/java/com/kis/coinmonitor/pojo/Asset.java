package com.kis.coinmonitor.pojo;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "rank",
        "symbol",
        "name",
        "supply",
        "maxSupply",
        "marketCapUsd",
        "volumeUsd24Hr",
        "priceUsd",
        "changePercent24Hr",
        "vwap24Hr",
        "explorer"
})
public class Asset {

    @JsonProperty("id")
    private String id;
    @JsonProperty("rank")
    private String rank;
    @JsonProperty("symbol")
    private String symbol;
    @JsonProperty("name")
    private String name;
    @JsonProperty("supply")
    private String supply;
    @JsonProperty("maxSupply")
    private String maxSupply;
    @JsonProperty("marketCapUsd")
    private String marketCapUsd;
    @JsonProperty("volumeUsd24Hr")
    private String volumeUsd24Hr;
    @JsonProperty("priceUsd")
    private String priceUsd;
    @JsonProperty("changePercent24Hr")
    private String changePercent24Hr;
    @JsonProperty("vwap24Hr")
    private String vwap24Hr;
    @JsonProperty("explorer")
    private String explorer;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("rank")
    public String getRank() {
        return rank;
    }

    @JsonProperty("rank")
    public void setRank(String rank) {
        this.rank = rank;
    }

    @JsonProperty("symbol")
    public String getSymbol() {
        return symbol;
    }

    @JsonProperty("symbol")
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("supply")
    public String getSupply() {
        return supply;
    }

    @JsonProperty("supply")
    public void setSupply(String supply) {
        this.supply = supply;
    }

    @JsonProperty("maxSupply")
    public String getMaxSupply() {
        return maxSupply;
    }

    @JsonProperty("maxSupply")
    public void setMaxSupply(String maxSupply) {
        this.maxSupply = maxSupply;
    }

    @JsonProperty("marketCapUsd")
    public String getMarketCapUsd() {
        return marketCapUsd;
    }

    @JsonProperty("marketCapUsd")
    public void setMarketCapUsd(String marketCapUsd) {
        this.marketCapUsd = marketCapUsd;
    }

    @JsonProperty("volumeUsd24Hr")
    public String getVolumeUsd24Hr() {
        return volumeUsd24Hr;
    }

    @JsonProperty("volumeUsd24Hr")
    public void setVolumeUsd24Hr(String volumeUsd24Hr) {
        this.volumeUsd24Hr = volumeUsd24Hr;
    }

    @JsonProperty("priceUsd")
    public String getPriceUsd() {
        return priceUsd;
    }

    @JsonProperty("priceUsd")
    public void setPriceUsd(String priceUsd) {
        this.priceUsd = priceUsd;
    }

    @JsonProperty("changePercent24Hr")
    public String getChangePercent24Hr() {
        return changePercent24Hr;
    }

    @JsonProperty("changePercent24Hr")
    public void setChangePercent24Hr(String changePercent24Hr) {
        this.changePercent24Hr = changePercent24Hr;
    }

    @JsonProperty("vwap24Hr")
    public String getVwap24Hr() {
        return vwap24Hr;
    }

    @JsonProperty("vwap24Hr")
    public void setVwap24Hr(String vwap24Hr) {
        this.vwap24Hr = vwap24Hr;
    }

    @JsonProperty("explorer")
    public String getExplorer() {
        return explorer;
    }

    @JsonProperty("explorer")
    public void setExplorer(String explorer) {
        this.explorer = explorer;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}