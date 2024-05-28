package com.iecube.community.model.tcpClient.service;

public interface MessageListener {
    void onMessageReceived(String message);
}
