package com.kis.coinmonitor.repository;

import com.kis.coinmonitor.model.standardAPI.AssetByID;
import com.kis.coinmonitor.model.standardAPI.AssetHistory;
import com.kis.coinmonitor.model.standardAPI.Assets;
import com.kis.coinmonitor.network.OnWebsocketMessageAccepted;

public interface Repository {

    Assets getAssets(Integer limit, Integer offset);
    Assets getAssets(String ids);
    AssetHistory getAssetHistory(String assetID, String historyInterval, Long historyStart, Long historyEnd);
    AssetByID getAssetData(String assetID);
    void setupPricesUpdating(OnWebsocketMessageAccepted listener, String assets);
    void startPricesUpdating();
    void stopPricesUpdating();
}
