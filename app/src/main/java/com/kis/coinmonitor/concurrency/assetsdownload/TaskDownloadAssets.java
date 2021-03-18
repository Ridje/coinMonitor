package com.kis.coinmonitor.concurrency.assetsdownload;

import android.util.Log;

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
        mListener.onTaskRunned();
        Log.e("assets_download", Thread.currentThread().getName() + "runned");
        mConnector.getAssets(mSearch, mSearch, mLimit, mOffset, new Callback<Assets>() {
            @Override
            public void onResponse(Call<Assets> call, Response<Assets> response) {
                List<Asset> assetsToAdd = response.body().getData();
                Log.e("assets_download", String.format("%s thread added element(s) with key(s) %s", Thread.currentThread().getName(), assetsToAdd.stream().map(Asset::getId).collect(Collectors.joining(","))));
                mListener.onResponce(assetsToAdd);
            }

            @Override
            public void onFailure(Call<Assets> call, Throwable t) {
                mListener.onFailure();
            }
        });
    }
}
