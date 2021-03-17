package com.kis.coinmonitor.network;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class AssetWebSocketListener extends WebSocketListener {

    private static final int NORMAL_CLOSURE_STATUS = 1000;
    private OnMessageAccepted mOnMessageAcceptedCallback;

    public AssetWebSocketListener(OnMessageAccepted callback) {
        super();
        mOnMessageAcceptedCallback = callback;
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        mOnMessageAcceptedCallback.onFailure();
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        mOnMessageAcceptedCallback.onResponce(text);
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
    }
}

