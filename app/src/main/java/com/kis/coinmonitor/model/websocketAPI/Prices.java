package com.kis.coinmonitor.model.websocketAPI;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;
import java.util.Map;

public class Prices {
    private Map<String, String> mPrices = null;

    @JsonAnySetter
    public void add(String key, String value) {
        if (mPrices == null) {
            mPrices = new HashMap<>();
        }
        mPrices.put(key, value);
    }

    public Map<String, String> getPrices() {
        return mPrices;
    }

}
