package com.kis.coinmonitor.repository;

import androidx.lifecycle.MutableLiveData;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kis.coinmonitor.model.standardAPI.Asset;
import com.kis.coinmonitor.model.standardAPI.AssetByID;
import com.kis.coinmonitor.model.standardAPI.AssetHistory;
import com.kis.coinmonitor.model.standardAPI.Assets;
import com.kis.coinmonitor.model.websocketAPI.Prices;
import com.kis.coinmonitor.network.CoinCapService;
import com.kis.coinmonitor.network.OnWebsocketMessageAccepted;
import com.kis.coinmonitor.network.PricesWebsocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RepositoryRetrofit implements Repository {

    private static RepositoryRetrofit repositoryRetrofit;
    private CoinCapService.AssetsService coinCapAssetsService;
    private final PricesWebsocket websocketPricesUpdated;


    private final Executor executor = Executors.newFixedThreadPool(2);

    private RepositoryRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CoinCapService.HTTPS_API_COINCAP_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .callbackExecutor(executor)
                .build();
        coinCapAssetsService = retrofit.create(CoinCapService.AssetsService.class);
        websocketPricesUpdated = new PricesWebsocket();
    }

    public synchronized static RepositoryRetrofit getInstance() {
        if (repositoryRetrofit == null) {
            synchronized (RepositoryRetrofit.class) {
                if (repositoryRetrofit == null) {
                    repositoryRetrofit = new RepositoryRetrofit();
                }
            }
        }
        return repositoryRetrofit;
    }

    public Assets getAssets(Integer limit, Integer offset) {
        return getAsset(null, null, limit, offset);
    }

    public Assets getAssets(String ids) {
        return getAsset(null, ids, null, null);
    }

    private Assets getAsset(String key, String ids, Integer limit, Integer offset) {
        Response<Assets> response = null;
        try {
            response = coinCapAssetsService.assets(key, ids, limit, offset).execute();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        Assets result = null;

        if (response.isSuccessful()) {
            result = response.body();
        }

        return result;
    }

    public AssetHistory getAssetHistory(String assetID, String historyInterval, Long historyStart, Long historyEnd) {

        Response<AssetHistory> response = null;
        try {
            response = coinCapAssetsService.assetHistory(assetID, historyInterval, historyStart, historyEnd).execute();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        AssetHistory result = null;

        if (response.isSuccessful()) {
            result = response.body();
        }

        return result;
    }

    public AssetByID getAssetData(String assetID) {

        Response<AssetByID> response = null;
        try {
            response = coinCapAssetsService.asset(assetID).execute();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        AssetByID result = null;

        if (response.isSuccessful()) {
            result = response.body();
        }

        return result;
    }

    @Override
    public void setupPricesUpdating(OnWebsocketMessageAccepted listener, String assets) {
        websocketPricesUpdated.setup(listener, assets);
    }

    @Override
    public void startPricesUpdating() {
        websocketPricesUpdated.open();
    }

    @Override
    public void stopPricesUpdating() {
        websocketPricesUpdated.close();
    }


}