package com.kis.coinmonitor.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.kis.coinmonitor.model.standardAPI.Asset;
import com.kis.coinmonitor.model.standardAPI.AssetByID;
import com.kis.coinmonitor.model.standardAPI.AssetHistory;
import com.kis.coinmonitor.network.ProjectRepository;
import com.kis.coinmonitor.utils.ChartInterval;

public class AssetViewModel extends AndroidViewModel {

    private final LiveData<Asset> assetLiveData;
    private final LiveData<AssetHistory> assetHistoryLiveData;

    public AssetViewModel(@NonNull Application application, String assetID, ChartInterval chartInterval) {
        super(application);
        assetLiveData = ProjectRepository.getInstance().loadAssetData(assetID);
        Long chartEndAt = System.currentTimeMillis();
        assetHistoryLiveData = ProjectRepository
                .getInstance()
                .loadAssetHistory(assetID
                        , chartInterval.getDefaultIntervalBetweenPoints().getIntervalValue()
                        , chartEndAt - chartInterval.getDefaultTimeDifference()
                        , chartEndAt);
    }

    public void loadAssetHistory(String assetID, ChartInterval chartInterval) {
        Long chartEndAT = System.currentTimeMillis();
        ProjectRepository.getInstance().loadAssetHistory(assetID
                , chartInterval.getDefaultIntervalBetweenPoints().getIntervalValue()
                , chartEndAT - chartInterval.getDefaultTimeDifference()
                , chartEndAT);
    }

    public LiveData<Asset> getAssetLiveData() {
        return assetLiveData;
    }

    public LiveData<AssetHistory> getAssetHistoryLiveData() {
        return assetHistoryLiveData;
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application application;
        private final String assetID;
        private final ChartInterval chartInterval;


        public Factory(@NonNull Application application, String assetID, ChartInterval chartInterval) {
            this.application = application;
            this.assetID = assetID;
            this.chartInterval = chartInterval;
        }

        @SuppressWarnings("unchecked")
        @Override
        @NonNull
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new AssetViewModel(application, assetID, chartInterval);
        }
    }
}