package com.kis.coinmonitor.concurrency.assetsdownload;

import android.util.Log;

import androidx.annotation.NonNull;

import com.kis.coinmonitor.model.standardAPI.Asset;
import com.kis.coinmonitor.model.standardAPI.Assets;
import com.kis.coinmonitor.network.APIConnector;

import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskDownloadAssets implements Runnable {

    APIConnector mConnector;
    String mSearch;
    String mIds;
    Integer mLimit;
    Integer mOffset;
    TaskDownloadAssetsListener mListener;

    public TaskDownloadAssets(String search, String ids, Integer limit, Integer offset,
                              TaskDownloadAssetsListener listener) {
        this.mSearch = search;
        this.mIds = ids;
        this.mLimit = limit;
        this.mOffset = offset;
        this.mListener = listener;
        mConnector = APIConnector.getAPIConnector();
    }

    @Override
    public void run() {
        mListener.onTaskRan();
        Log.e("assets_download", Thread.currentThread().getName() + "ran");
        mConnector.getAssets(mSearch, mSearch, mLimit, mOffset, new Callback<Assets>() {
            @Override
            public void onResponse(@NonNull Call<Assets> call, @NonNull Response<Assets> response) {
                List<Asset> assetsToAdd = null;
                String listOfAddedKeys = "";
                if (response.body() != null) {
                    assetsToAdd = response.body().getData();
                    listOfAddedKeys = assetsToAdd.stream().map(Asset::getId).collect(Collectors.joining(","));
                } else {
                    mListener.onFailure();
                }
                Log.e("assets_download", String.format("%s thread added element(s) with key(s) %s", Thread.currentThread().getName(), listOfAddedKeys));
                mListener.onResponse(assetsToAdd);
            }

            @Override
            public void onFailure(@NonNull Call<Assets> call, @NonNull Throwable t) {
                mListener.onFailure();
            }
        });
    }
}
