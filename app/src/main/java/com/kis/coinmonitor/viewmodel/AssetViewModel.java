package com.kis.coinmonitor.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.Chart;
import com.kis.coinmonitor.model.standardAPI.Asset;
import com.kis.coinmonitor.model.standardAPI.AssetHistory;
import com.kis.coinmonitor.repository.RepositoryRetrofit;
import com.kis.coinmonitor.utils.ChartInterval;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AssetViewModel extends AndroidViewModel {

    private final MutableLiveData<Asset> assetLiveData = new MutableLiveData<>();
    private final MutableLiveData<AssetHistory> assetHistoryLiveData = new MutableLiveData<>();
    private final String assetID;

    public AssetViewModel(@NonNull Application application, String assetID) {
        super(application);
        this.assetID = assetID;
        downloadAssetData();
        downloadAssetHistory(ChartInterval.ONE_DAY);
    }

    public void downloadAssetData() {

        Single.fromCallable(() ->
                RepositoryRetrofit.getInstance().getAssetData(assetID))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        assetByID -> assetLiveData.setValue(assetByID.getAsset()),
                        throwable -> throwable.printStackTrace());
    }

    public void downloadAssetHistory(ChartInterval chartInterval) {

        Long chartEndAT = System.currentTimeMillis();

        Single.fromCallable(() ->
                RepositoryRetrofit.getInstance().getAssetHistory(assetID
                        , chartInterval.getDefaultIntervalBetweenPoints().getIntervalValue()
                        , chartEndAT - chartInterval.getDefaultTimeDifference()
                        , chartEndAT))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        assetHistory -> assetHistoryLiveData.setValue(assetHistory),
                        throwable -> throwable.printStackTrace());
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


        public Factory(@NonNull Application application, String assetID) {
            this.application = application;
            this.assetID = assetID;
        }

        @SuppressWarnings("unchecked")
        @Override
        @NonNull
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new AssetViewModel(application, assetID);
        }
    }
}