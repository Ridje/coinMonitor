package com.kis.coinmonitor.network;

import com.kis.coinmonitor.model.Assets;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public final class CoinCapService {


    public interface AssetsService {
        @GET("assets")
        Call<Assets> assets(
                @Query("search") String key,
                @Query("ids") String ids,
                @Query("limit") Integer limit,
                @Query("offset") Integer offset);
    }
}
