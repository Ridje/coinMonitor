package com.kis.coinmonitor.network;

import com.kis.coinmonitor.model.standardAPI.Asset;
import com.kis.coinmonitor.model.standardAPI.AssetByID;
import com.kis.coinmonitor.model.standardAPI.AssetHistory;
import com.kis.coinmonitor.model.standardAPI.Assets;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public final class CoinCapService {

    public static final String HTTPS_API_COINCAP_URL = "https://api.coincap.io/v2/";

    public interface AssetsService {
        @GET("assets")
        Call<Assets> assets(
                @Query("search") String key,
                @Query("ids") String ids,
                @Query("limit") Integer limit,
                @Query("offset") Integer offset);

        @GET("assets/{asset}/history")
        Call<AssetHistory> assetHistory(
                @Path("asset") String assetID,
                @Query("interval") String interval,
                @Query("start") Long startTimestamp,
                @Query("end") Long endTimestamp);

        @GET("assets/{asset}")
        Call<AssetByID> asset(
                @Path("asset") String assetID
        );
    }
}
