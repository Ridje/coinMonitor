package com.kis.coinmonitor.network;

public interface OnWebsocketMessageAccepted {
    void onWebsocketMessageAccepted(String textResponse);
    void onWebsocketConnectionFailure();
    void onWebsocketConnectionOpen();
    void onWebsocketConnectionClosed();
}
