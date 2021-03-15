package com.kis.coinmonitor.services;

import com.kis.coinmonitor.pojo.Assets;
import com.kis.coinmonitor.pojo.Asset;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;

public final class CoinCapService {
    public static final String API_URL = "https://api.coincap.io/v2/";

    public interface CoinCap {
        @GET("assets")
        Call<Assets> assets();
    }

    public static void main(String[] args) throws IOException {
        Retrofit retrofit =
                new Retrofit.Builder()
                        .baseUrl(API_URL)
                        .addConverterFactory(JacksonConverterFactory.create())
                        .build();

        CoinCap coinCap = retrofit.create(CoinCap.class);

        Call<Assets> call = coinCap.assets();
        Assets assets = call.execute().body();

    }
}
