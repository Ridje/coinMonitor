package com.kis.coinmonitor.concurrency;

public interface UpdateablePrices {
    void onPricesUpdated(Integer itemPosition);
    void onStartUpdatePrices();
    void onPauseUpdatePrices();
}