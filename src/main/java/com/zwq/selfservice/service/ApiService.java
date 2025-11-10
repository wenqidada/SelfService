package com.zwq.selfservice.service;

public interface ApiService {
    String createQR(String command);

    long getOpenTime(Integer tableId);
}
