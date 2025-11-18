package com.zwq.selfservice.service;

import java.util.Map;

public interface ApiService {
    String createQR(String command);

    long getOpenTime(Integer tableId);

    Map<Integer, String> getOpenTimeMap();

    String putOpenTimeMap(Integer key,String value);
}
