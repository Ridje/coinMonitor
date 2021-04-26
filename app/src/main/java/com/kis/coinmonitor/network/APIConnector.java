package com.kis.coinmonitor.network;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kis.coinmonitor.model.standardAPI.AssetHistory;
import com.kis.coinmonitor.model.standardAPI.Assets;

import java.sql.Timestamp;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public final class APIConnector {
    public static final String API_URL = "https://api.coincap.io/v2/";

    Retrofit retrofit;
    private static APIConnector connectorInstance = null;
    private static CoinCapService.AssetsService assetsService = null;

    private APIConnector() {
        retrofit =
                new Retrofit.Builder()
                        .baseUrl(API_URL).callbackExecutor(Executors.newSingleThreadExecutor())
                        .addConverterFactory(JacksonConverterFactory.create())
                        .build();
        assetsService = retrofit.create(CoinCapService.AssetsService.class);
    }

    public static APIConnector getAPIConnector() {
        if (connectorInstance == null) {
            connectorInstance = new APIConnector();
        }
        return connectorInstance;
    }

    public void getAssets(@Nullable String search, @Nullable String ids, @Nullable Integer limit, @Nullable Integer offset, Callback<Assets> callback) {
        Call<Assets> call = assetsService.assets(search, ids, limit, offset);
        call.enqueue(callback);
    }

    public void getAssetHistory(@NonNull String asset, @NonNull String interval, @Nullable Long start, @Nullable Long end, Callback<AssetHistory> callback) {
        Call<AssetHistory> call = assetsService.assetHistory(asset, interval, start, end);
        call.enqueue(callback);
    }
}
