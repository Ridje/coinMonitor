package com.kis.coinmonitor.concurrency.priceupdater;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kis.coinmonitor.model.websocketAPI.CachedPrices;
import com.kis.coinmonitor.model.websocketAPI.Prices;
import com.kis.coinmonitor.network.PricesWebSocketListener;
import com.kis.coinmonitor.network.OnWebsocketMessageAccepted;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

public class TaskListenPricesChanges implements Runnable, OnWebsocketMessageAccepted {

    OkHttpClient mHttpClient;
    WebSocket mWebsocketPrices;
    private static volatile String mAssetsParam;
    private static volatile boolean mAssetsChanged;
    Request mRequest;
    CachedPrices mPrices;

    TaskListenPricesChangesListener mListener;

    private static final Integer CODE_OK = 1000;
    private static final String CLOSING_REASON_OK = "Goodbye!";
    private static final String CLOSING_REASON_RECREATE_WEBSOCKET = "See you in 3...2...1...";
    private static final String URL_PRICES = "wss://ws.coincap.io/prices?assets=";


    public TaskListenPricesChanges(String assetsParam, CachedPrices prices, TaskListenPricesChangesListener listener) {
        this.mHttpClient = new OkHttpClient();
        this.mPrices = prices;
        this.mListener = listener;
        mAssetsChanged = true;
        mAssetsParam = assetsParam;
    }

    public static void assetsListChanged(String newValue) {
        mAssetsChanged = true;
        mAssetsParam = newValue;
    }

    @Override
    public void run() {

        while (!Thread.currentThread().isInterrupted()) {
            if (mAssetsChanged) {
                closeWebsocket(CODE_OK, CLOSING_REASON_RECREATE_WEBSOCKET);
                mRequest = new Request.Builder().url(URL_PRICES + mAssetsParam).build();
                mWebsocketPrices = mHttpClient.newWebSocket(mRequest, new PricesWebSocketListener(this));
                mAssetsChanged = false;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                closeWebsocket(CODE_OK, CLOSING_REASON_OK);
            }
            synchronized (this) {
                mListener.onTaskPaused();
            }
        }
        closeWebsocket(CODE_OK, CLOSING_REASON_OK);
    }

    private void closeWebsocket(Integer code, String reason) {
        if (mWebsocketPrices != null) {
            mWebsocketPrices.close(code, reason);
            mWebsocketPrices = null;
        }
        if (mRequest != null) {
            mRequest = null;
        }
    }

    @Override
    public void onWebsocketMessageAccepted(String textResponse) {
        try {
            synchronized (this) {
                mPrices.push(new ObjectMapper()
                        .readerFor(Prices.class)
                        .readValue(textResponse));
            }

        } catch (JsonProcessingException e) {
            onWebsocketConnectionFailure();
        }
    }

    @Override
    public void onWebsocketConnectionFailure() {
        // TODO: 18-Mar-21 Decide what to do on Failure
    }
}
