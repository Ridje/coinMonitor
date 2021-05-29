package com.kis.coinmonitor.network;

import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class PricesWebsocket  {

    private static final Integer STATUS_CODE_OK = 1000;
    private static final String CLOSE_REASON = "Goodbye!";
    private static final String CLOSE_REASON_ASSETS_ROSTER_CHANGED = "See you in 3...2...1...";
    private static final String WEBSOCKET_URI_BASE = "wss://ws.coincap.io/prices?assets=";
    private static final String LOG_TAG = "com.kis.coinmonitor.network" + ".websocketPriceUpdate";

    private OkHttpClient mHttpClient;
    private WebSocket mWebsocketPrices;
    private Request mRequest;
    private OnWebsocketMessageAccepted mMessagesListener;
    private Boolean mWebsocketOpen = false;


    public PricesWebsocket() {
        this.mHttpClient = new OkHttpClient();
    }

    public void setup(OnWebsocketMessageAccepted listener, String assetsRoster) {

        mMessagesListener = listener;

        mRequest = new Request.Builder().url(WEBSOCKET_URI_BASE + assetsRoster).build();

        if (mWebsocketOpen){
            close(STATUS_CODE_OK, CLOSE_REASON_ASSETS_ROSTER_CHANGED);
            open();
        }

    }

    public void open() {

        if (mRequest == null) {
            Log.w(LOG_TAG, "You tried to open socket without configurate it's url before.\n" +
                    "If fragment was recreated or created first time - this message is ok, this will be resolve automatically");
            return;
        }

        if (mWebsocketOpen) {
            close(STATUS_CODE_OK, CLOSE_REASON_ASSETS_ROSTER_CHANGED);
        }
        mWebsocketPrices = mHttpClient.newWebSocket(mRequest, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                mWebsocketOpen = true;
                mMessagesListener.onWebsocketConnectionOpen();
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                mMessagesListener.onWebsocketMessageAccepted(text);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                mWebsocketOpen = false;
                mMessagesListener.onWebsocketConnectionClosed();
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                mWebsocketOpen = false;
                mMessagesListener.onWebsocketConnectionFailure();
            }
        });
    }

    public void close() {
        close(PricesWebsocket.STATUS_CODE_OK, PricesWebsocket.CLOSE_REASON);
    }

    public void close(Integer code, String reason) {

        if (mWebsocketPrices == null) return;

        WebSocket thisWebsocket = mWebsocketPrices;
        synchronized (this) {
            thisWebsocket = this.mWebsocketPrices;
        }

        if (thisWebsocket != null) {
            thisWebsocket.close(code, reason);
            mWebsocketPrices = null;
        }
    }
}
