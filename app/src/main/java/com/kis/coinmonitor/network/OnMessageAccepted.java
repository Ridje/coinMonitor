package com.kis.coinmonitor.network;

public interface OnMessageAccepted {
    void onResponce(String textResponse);
    void onFailure();
}
