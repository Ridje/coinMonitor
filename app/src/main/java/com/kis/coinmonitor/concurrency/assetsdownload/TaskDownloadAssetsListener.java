package com.kis.coinmonitor.concurrency.assetsdownload;

import java.util.List;

public interface TaskDownloadAssetsListener {
    void onTaskRan();
    void onResponse(List dataList);
    void onFailure();
}
