package com.kis.coinmonitor.network;

import androidx.annotation.Nullable;

import com.kis.coinmonitor.model.standardAPI.Assets;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public final class APIConnector {
    public static final String API_URL = "https://api.coincap.io/v2/";

    Retrofit retrofit = null;
    private static APIConnector connectorInstance = null;

    private APIConnector() {
        retrofit =
                new Retrofit.Builder()
                        .baseUrl(API_URL)
                        .addConverterFactory(JacksonConverterFactory.create())
                        .build();
    }

    public static APIConnector getAPIConnector() {
        if (connectorInstance == null) {
            connectorInstance = new APIConnector();
        }
        return connectorInstance;
    }

    public void getAssets(@Nullable String search, @Nullable String ids, @Nullable Integer limit, @Nullable Integer offset, Callback<Assets> callback) {

        CoinCapService.AssetsService assetsService = retrofit.create(CoinCapService.AssetsService.class);

        Call<Assets> call = assetsService.assets(search, ids, limit, offset);
        call.enqueue(callback);
    }

}
