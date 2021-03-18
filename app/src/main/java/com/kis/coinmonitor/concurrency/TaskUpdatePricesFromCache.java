package com.kis.coinmonitor.concurrency;


import android.util.Log;

import com.kis.coinmonitor.model.CachedPrices;
import com.kis.coinmonitor.model.standardAPI.Asset;
import com.kis.coinmonitor.ui.AssetsListFragment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class TaskUpdatePricesFromCache implements Runnable {

    public CachedPrices mCachePrices;
    public List<Asset> mAssets;
    public UpdateablePrices mCallback;

    public TaskUpdatePricesFromCache(CachedPrices cachePrices, List<Asset> assets, UpdateablePrices callback) {
        this.mCachePrices = cachePrices;
        this.mAssets = assets;
        this.mCallback = callback;
    }

    @Override
    public void run() {
        mCallback.onStartUpdatePrices();

        for (Map.Entry<String, Stack<BigDecimal>> entry : mCachePrices.entrySet()) {
            for (int i = 0; i < mAssets.size(); i++) {
                if (mAssets.get(i).getId().equals(entry.getKey()) && !entry.getValue().isEmpty()) {
                    BigDecimal newValue = entry.getValue().peek().setScale(2, RoundingMode.HALF_UP);
                    if (mAssets.get(i).getPriceUsd().setScale(2, RoundingMode.HALF_UP).equals(newValue)) {
                        break;
                    }
                    mAssets.get(i).setPriceUsd(newValue);
                    mCallback.onPricesUpdated(i);
                    break;
                }
            }
        }
    }
}
