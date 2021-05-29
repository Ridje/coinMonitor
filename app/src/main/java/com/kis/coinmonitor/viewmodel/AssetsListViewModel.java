package com.kis.coinmonitor.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kis.coinmonitor.model.standardAPI.Asset;
import com.kis.coinmonitor.model.websocketAPI.Prices;
import com.kis.coinmonitor.network.OnWebsocketMessageAccepted;
import com.kis.coinmonitor.repository.RepositoryRetrofit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AssetsListViewModel extends AndroidViewModel implements OnWebsocketMessageAccepted {

    private final MutableLiveData<List<Asset>> assetsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isAssetsDownloadingLiveData = new MutableLiveData<>();
    private final MutableLiveData<Asset> assetHistoryLiveData = new MutableLiveData<>();
    private final List<Asset> assetsList = new ArrayList<>();
    private final MutableLiveData<List<Asset>> updatedAssetsPricesLiveData = new MutableLiveData<>();

    private static final String LOG_TAG = "AssetsLisViewModel";
    private final Integer limit = 20;
    private Integer offset = 0;
    private static final String shortChartHistoryInterval = "m5";
    private static final Long DEFAULT_GRAPH_PERIOD_HOURS_IN_MS = (long) (27 * 3600000);


    public AssetsListViewModel(@NonNull Application application) {
        super(application);
    }

    public void downloadAssets() {

        isAssetsDownloadingLiveData.setValue(true);

        Single.fromCallable(() ->
                        RepositoryRetrofit.getInstance().getAssets(limit, offset))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        assets -> {
                            assetsList.addAll(assets.getData());
                            assetsLiveData.setValue(assetsList);
                            offset += limit;
                            setupPriceUpdating();
                            isAssetsDownloadingLiveData.setValue(false);
                        },
                        throwable -> {
                            throwable.printStackTrace();
                            isAssetsDownloadingLiveData.setValue(false);
                        });
    }

    public void downloadAssets(String coins) {
        isAssetsDownloadingLiveData.setValue(true);

        Single.fromCallable(() ->
                RepositoryRetrofit.getInstance().getAssets(coins))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        assets -> {
                            assetsList.addAll(assets.getData());
                            assetsLiveData.setValue(assetsList);
                            offset += limit;
                            setupPriceUpdating();
                            isAssetsDownloadingLiveData.setValue(false);
                        },
                        throwable -> {
                            throwable.printStackTrace();
                            isAssetsDownloadingLiveData.setValue(false);
                        });
    }

    public void downloadAssetShortHistory(Asset asset) {

        Single.fromCallable(() ->
                RepositoryRetrofit.getInstance().getAssetHistory(asset.getId()
                        , shortChartHistoryInterval
                        , System.currentTimeMillis() - DEFAULT_GRAPH_PERIOD_HOURS_IN_MS
                        , System.currentTimeMillis()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        assetHistory -> {
                            asset.setHistory(assetHistory);
                            assetHistoryLiveData.setValue(asset);
                        },
                        throwable -> throwable.printStackTrace());
    }

    public LiveData<List<Asset>> getAssetsLiveData() {
        return assetsLiveData;
    }

    public LiveData<Boolean> getIsAssetsDownloadingLiveData() {
        return isAssetsDownloadingLiveData;
    }

    public LiveData<Asset> getAssetHistoryLiveData() {
        return assetHistoryLiveData;
    }

    public LiveData<List<Asset>> getUpdatedAssetsPricesLiveData() {
        return updatedAssetsPricesLiveData;
    }

    public void setupPriceUpdating() {
        RepositoryRetrofit.getInstance().setupPricesUpdating(this, assetsList.stream().map(Asset::getId).collect(Collectors.joining(",")));
        RepositoryRetrofit.getInstance().startPricesUpdating();
    }

    public void startPricesUpdating() {
        RepositoryRetrofit.getInstance().startPricesUpdating();
    }

    public void stopPricesUpdating() {
        RepositoryRetrofit.getInstance().stopPricesUpdating();
    }

    @Override
    public void onWebsocketMessageAccepted(String textResponse) {
        Prices newPrices;
        try {
            newPrices = new ObjectMapper()
                    .readerFor(Prices.class)
                    .readValue(textResponse);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        }

        List<Asset> updatedPrices = new ArrayList<>();
        assetsList.forEach(asset -> {
            String newPrice = newPrices.getPrices().get(asset.getId());
            if (newPrice != null) {
                asset.setPriceUsd(new BigDecimal(newPrice));
                updatedPrices.add(asset);
            }
        });
        updatedAssetsPricesLiveData.postValue(updatedPrices);
    }

    @Override
    public void onWebsocketConnectionFailure() {}

    @Override
    public void onWebsocketConnectionOpen() {}

    @Override
    public void onWebsocketConnectionClosed() {}
}
