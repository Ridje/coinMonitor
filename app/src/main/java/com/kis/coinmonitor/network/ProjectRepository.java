package com.kis.coinmonitor.network;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kis.coinmonitor.model.standardAPI.Asset;
import com.kis.coinmonitor.model.standardAPI.AssetByID;
import com.kis.coinmonitor.model.standardAPI.AssetHistory;
import com.kis.coinmonitor.model.standardAPI.Assets;
import com.kis.coinmonitor.model.websocketAPI.Prices;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class ProjectRepository implements OnWebsocketMessageAccepted {

    private CoinCapService.AssetsService coinCapAssetsService;
    private static ProjectRepository projectRepository;
    private Integer currentOffset = 0;
    private Boolean mWebsocketOpen = false;
    public static final String DEFAULT_GRAPH_INTERVAL = "m5";
    public static final Long DEFAULT_GRAPH_PERIOD_HOURS_IN_MS = (long) (27 * 3600000);
    public static final Long MS_IN_FIVE_MINUTES = (long) (5 * 60000);

    private static final Integer LIMIT_PER_DOWNLOAD = 20;


    private final MutableLiveData<Boolean> isAssetsListDownloading = new MutableLiveData<>();
    private final MutableLiveData<List<Asset>> assetsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Asset>> updatedAssetsPricesLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isPriceUpdatingWorks = new MutableLiveData<>();
    private final MutableLiveData<Asset> downloadedAssetHistoryLiveData = new MutableLiveData<>();
    private final MutableLiveData<AssetHistory> assetHistoryMutableLiveData = new MutableLiveData<>();
    private final List<Asset> assets = new ArrayList<>();
    private PricesWebsocket websocketPricesUpdated;

    private ProjectRepository() {
        Retrofit retrofit = new Retrofit.Builder() .baseUrl(CoinCapService.HTTPS_API_COINCAP_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        coinCapAssetsService = retrofit.create(CoinCapService.AssetsService.class);
        websocketPricesUpdated = new PricesWebsocket(this);
    }

    public synchronized static ProjectRepository getInstance() {
        if (projectRepository == null) {
            synchronized (ProjectRepository.class) {
                if (projectRepository == null) {
                    projectRepository = new ProjectRepository();
                }
            }
        }
        return projectRepository;
    }

    public void loadAssetsList() {
        isAssetsListDownloading.setValue(true);
        coinCapAssetsService.assets(null, null, LIMIT_PER_DOWNLOAD, currentOffset).enqueue(new Callback<Assets>() {
            @Override
            public void onResponse(Call<Assets> call, Response<Assets> response) {
                if (response.isSuccessful()) {
                    assets.addAll(response.body().getData());
                    assetsLiveData.setValue(assets);
                    isAssetsListDownloading.setValue(false);
                    currentOffset += LIMIT_PER_DOWNLOAD;
                    //setupPriceUpdater();
                }
            }

            @Override
            public void onFailure(Call<Assets> call, Throwable t) {
                isAssetsListDownloading.setValue(false);
            //TODO: failure handling
            }
        });
    }

    public void loadAssetHistory(Asset asset, String graphInterval) {

        if (graphInterval == null) {
            graphInterval = DEFAULT_GRAPH_INTERVAL;
        }

        coinCapAssetsService.assetHistory(asset.getId(), graphInterval, System.currentTimeMillis() - DEFAULT_GRAPH_PERIOD_HOURS_IN_MS, System.currentTimeMillis())
                .enqueue(new Callback<AssetHistory>() {
                    @Override
                    public void onResponse(Call<AssetHistory> call, Response<AssetHistory> response) {
                        if (response.isSuccessful()) {
                            asset.setHistory(response.body());
                            downloadedAssetHistoryLiveData.setValue(asset);
                        }
                    }

                    @Override
                    public void onFailure(Call<AssetHistory> call, Throwable t) {
                        //TODO: failure handling
                    }
                });
    }

    public MutableLiveData<AssetHistory> loadAssetHistory(@NonNull String assetKey, @NonNull String chartInterval, @NonNull Long chartStart, @NonNull Long chartEnd) {

        coinCapAssetsService.assetHistory(assetKey, chartInterval, chartStart, chartEnd).enqueue(new Callback<AssetHistory>() {
            @Override
            public void onResponse(Call<AssetHistory> call, Response<AssetHistory> response) {
                if (response.isSuccessful()) {
                    assetHistoryMutableLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<AssetHistory> call, Throwable t) {
                //TODO: failure handling
            }
        });
        return assetHistoryMutableLiveData;
    }

    public MutableLiveData<Asset> loadAssetData(String assetKey) {
        final MutableLiveData<Asset> loadedAsset = new MutableLiveData<>();
        coinCapAssetsService.asset(assetKey).enqueue(new Callback<AssetByID>() {
            @Override
            public void onResponse(Call<AssetByID> call, Response<AssetByID> response) {
                if (response.isSuccessful()) {
                    loadedAsset.setValue(response.body().getAsset());
                }
            }

            @Override
            public void onFailure(Call<AssetByID> call, Throwable t) {
                //TODO: failure handling
            }
        });
        return loadedAsset;
    }

    public void setupPriceUpdater() {
        websocketPricesUpdated.setup(assets.stream().map(Asset::getId).collect(Collectors.joining(",")));
        websocketPricesUpdated.open();
    }

    public LiveData<Boolean> getIsAssetsListDownloadingLiveData() {
        return isAssetsListDownloading;
    }

    public LiveData<List<Asset>> getAssetsLiveData() {
        return assetsLiveData;
    }

    public LiveData<List<Asset>> getUpdatedAssetsPricesLiveData() {
        return updatedAssetsPricesLiveData;
    }

    public LiveData<Boolean> getIsPriceUpdatingWorksLiveData() {
        return isPriceUpdatingWorks;
    }

    public LiveData<Asset> getDownloadedAssetHistoryLiveData() {
        return downloadedAssetHistoryLiveData;
    }

    public void startPricesUpdating() {
        websocketPricesUpdated.open();
    }

    public void stopPricesUpdating() {
        websocketPricesUpdated.close();
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
        assets.forEach(asset -> {
            String newPrice = newPrices.getPrices().get(asset.getId());
            if (newPrice != null) {
                asset.setPriceUsd(new BigDecimal(newPrice));
                updatedPrices.add(asset);
            }
        });
        updatedAssetsPricesLiveData.postValue(updatedPrices);
    }

    @Override
    public void onWebsocketConnectionFailure() {
        isPriceUpdatingWorks.postValue(false);
    }

    @Override
    public void onWebsocketConnectionOpen() {
        isPriceUpdatingWorks.postValue(true);
    }

    @Override
    public void onWebsocketConnectionClosed() {
        isPriceUpdatingWorks.postValue(false);
    }
}
