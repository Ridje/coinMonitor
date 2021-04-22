package com.kis.coinmonitor.network;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class PricesWebSocketListener extends WebSocketListener {

    private static final int NORMAL_CLOSURE_STATUS = 1000;
    private final OnWebsocketMessageAccepted mOnWebsocketMessageAcceptedCallback;

    public PricesWebSocketListener(OnWebsocketMessageAccepted callback) {
        super();
        mOnWebsocketMessageAcceptedCallback = callback;
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        mOnWebsocketMessageAcceptedCallback.onWebsocketConnectionFailure();
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        mOnWebsocketMessageAcceptedCallback.onWebsocketMessageAccepted(text);
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
    }
}

