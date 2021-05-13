package com.kis.coinmonitor.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.kis.coinmonitor.model.standardAPI.Asset;
import com.kis.coinmonitor.network.ProjectRepository;

import java.util.List;

public class AssetsListViewModel extends AndroidViewModel  {

    private final LiveData<List<Asset>> assetsListObservable;
    private final LiveData<Boolean> isAssetsListDownloading;
    private final LiveData<List<Asset>> updatedAssetsPricesLiveData;
    private final LiveData<Asset> downloadedAssetHistoryLiveData;

    public AssetsListViewModel(@NonNull Application application) {
        super(application);
        isAssetsListDownloading = ProjectRepository.getInstance().getIsAssetsListDownloadingLiveData();
        assetsListObservable = ProjectRepository.getInstance().getAssetsLiveData();
        updatedAssetsPricesLiveData = ProjectRepository.getInstance().getUpdatedAssetsPricesLiveData();
        downloadedAssetHistoryLiveData = ProjectRepository.getInstance().getDownloadedAssetHistoryLiveData();
        downloadAssets();
    }

    public void downloadAssets() {
        ProjectRepository.getInstance().loadAssetsList();
    }

    public void downloadAssetHistory(Asset asset) {
        ProjectRepository.getInstance().loadAssetHistory(asset, null);
    }

    public LiveData<List<Asset>> getAssetsListObservable() {
        return assetsListObservable;
    }

    public LiveData<Boolean> getIsAssetsDownloading() {
        return isAssetsListDownloading;
    }

    public LiveData<List<Asset>> getUpdatedAssetsPricesLiveData() {
        return updatedAssetsPricesLiveData;
    }


    public LiveData<Asset> getDownloadedAssetHistoryLiveData() {
        return downloadedAssetHistoryLiveData;
    }

    public void startPricesUpdating() {
        ProjectRepository.getInstance().startPricesUpdating();
    }

    public void stopPricesUpdating() {
        ProjectRepository.getInstance().stopPricesUpdating();
    }
}
