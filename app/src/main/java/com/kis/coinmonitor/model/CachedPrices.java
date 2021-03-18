package com.kis.coinmonitor.model;

import com.kis.coinmonitor.model.websocketAPI.Prices;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class CachedPrices {

    private HashMap<String, Stack<BigDecimal>> mInnerCacheList = new HashMap<>();
    private Integer count = 0;

    public synchronized boolean push(Prices item) {

        for (Map.Entry<String, String> itemEntry : item.getPrices().entrySet()) {
            if (mInnerCacheList.get(itemEntry.getKey()) == null) {
                mInnerCacheList.put(itemEntry.getKey(), new Stack<>());
            }
            mInnerCacheList.get(itemEntry.getKey()).push(new BigDecimal(itemEntry.getValue()));
            count++;
        }

        return true;
    }

    public Set<Map.Entry<String, Stack<BigDecimal>>> entrySet() {
        return mInnerCacheList.entrySet();
    }

    void clear(String key) {
        if (mInnerCacheList.get(key) != null) {
            mInnerCacheList.get(key).clear();
        }
    }

    public synchronized void clearAll() {
        for (String key : mInnerCacheList.keySet()) {
            clear(key);
        }
        count = 0;
    }

    public synchronized boolean isEmpty() {
        return count <= 0;
    }

    public CachedPrices clearWithCopy() {
        CachedPrices currentCopy = new CachedPrices();
        synchronized (this) {
            currentCopy.mInnerCacheList = new HashMap<>();
            for (String key: mInnerCacheList.keySet()) {
                currentCopy.mInnerCacheList.put(key, (Stack<BigDecimal>) mInnerCacheList.get(key).clone());
            }
            currentCopy.count = count;
        }
        clearAll();
        return currentCopy;
    }
}
