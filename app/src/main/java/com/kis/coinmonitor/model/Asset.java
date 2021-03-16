package com.kis.coinmonitor.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class Asset implements Parcelable {

    @JsonProperty("id")
    private String id;
    @JsonProperty("rank")
    private Integer rank;
    @JsonProperty("symbol")
    private String symbol;
    @JsonProperty("name")
    private String name;
    @JsonProperty("supply")
    @JsonFormat(shape= JsonFormat.Shape.STRING)
    private BigDecimal supply;
    @JsonProperty("maxSupply")
    @JsonFormat(shape= JsonFormat.Shape.STRING)
    private BigDecimal maxSupply;
    @JsonProperty("marketCapUsd")
    @JsonFormat(shape= JsonFormat.Shape.STRING)
    private BigDecimal marketCapUsd;
    @JsonProperty("volumeUsd24Hr")
    @JsonFormat(shape= JsonFormat.Shape.STRING)
    private BigDecimal volumeUsd24Hr;
    @JsonFormat(shape= JsonFormat.Shape.STRING)
    @JsonProperty("priceUsd")
    private BigDecimal priceUsd;
    @JsonFormat(shape= JsonFormat.Shape.STRING)
    @JsonProperty("changePercent24Hr")
    private BigDecimal changePercent24Hr;
    @JsonProperty("vwap24Hr")
    @JsonFormat(shape= JsonFormat.Shape.STRING)
    private BigDecimal vwap24Hr;
    @JsonProperty("explorer")
    private String explorer;

    public Asset() {

    }

    protected Asset(Parcel in) {
        id = in.readString();
        if (in.readByte() == 0) {
            rank = null;
        } else {
            rank = in.readInt();
        }
        symbol = in.readString();
        name = in.readString();
        explorer = in.readString();
    }

    public static final Creator<Asset> CREATOR = new Creator<Asset>() {
        @Override
        public Asset createFromParcel(Parcel in) {
            return new Asset(in);
        }

        @Override
        public Asset[] newArray(int size) {
            return new Asset[size];
        }
    };

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("rank")
    public Integer getRank() {
        return rank;
    }

    @JsonProperty("rank")
    public void setRank(Integer rank) {
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
    public BigDecimal getSupply() {
        return supply;
    }

    @JsonProperty("supply")
    public void setSupply(BigDecimal supply) {
        this.supply = supply;
    }

    @JsonProperty("maxSupply")
    public BigDecimal getMaxSupply() {
        return maxSupply;
    }

    @JsonProperty("maxSupply")
    public void setMaxSupply(BigDecimal maxSupply) {
        this.maxSupply = maxSupply;
    }

    @JsonProperty("marketCapUsd")
    public BigDecimal getMarketCapUsd() {
        return marketCapUsd;
    }

    @JsonProperty("marketCapUsd")
    public void setMarketCapUsd(BigDecimal marketCapUsd) {
        this.marketCapUsd = marketCapUsd;
    }

    @JsonProperty("volumeUsd24Hr")
    public BigDecimal getVolumeUsd24Hr() {
        return volumeUsd24Hr;
    }

    @JsonProperty("volumeUsd24Hr")
    public void setVolumeUsd24Hr(BigDecimal volumeUsd24Hr) {
        this.volumeUsd24Hr = volumeUsd24Hr;
    }

    @JsonProperty("priceUsd")
    public BigDecimal getPriceUsd() {
        return priceUsd;
    }

    @JsonProperty("priceUsd")
    public void setPriceUsd(BigDecimal priceUsd) {
        this.priceUsd = priceUsd;
    }

    @JsonProperty("changePercent24Hr")
    public BigDecimal getChangePercent24Hr() {
        return changePercent24Hr;
    }

    @JsonProperty("changePercent24Hr")
    public void setChangePercent24Hr(BigDecimal changePercent24Hr) {
        this.changePercent24Hr = changePercent24Hr;
    }

    @JsonProperty("vwap24Hr")
    public BigDecimal getVwap24Hr() {
        return vwap24Hr;
    }

    @JsonProperty("vwap24Hr")
    public void setVwap24Hr(BigDecimal vwap24Hr) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        if (rank == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(rank);
        }
        dest.writeString(symbol);
        dest.writeString(name);
        dest.writeString(explorer);
    }
}