package com.kis.coinmonitor.concurrency.assetsdownload;

import java.util.List;

public interface TaskDownloadAssetsListener<E> {
    public void onTaskRunned();
    public void onResponce(List<E> dataList);
    public void onFailure();
}
