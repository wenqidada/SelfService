package com.zwq.selfservice.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Component
public class SendHttpRequestUtil {

    private final RestTemplate restTemplate;

    public SendHttpRequestUtil(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // GET 请求
    public <T> ResponseEntity<T> get(String url,Object requestEntity, Class<T> responseType, Map<String, String> headers) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> rawMap = objectMapper.convertValue(
                requestEntity, new com.fasterxml.jackson.core.type.TypeReference<>() {
                }
        );
        Map<String, String> params = new HashMap<>();
        rawMap.forEach((key, value) -> {
            if (value != null) {
                params.put(key, value.toString());
            }
        });
        if (!params.isEmpty()) {
            StringBuilder sb = new StringBuilder(url);
            sb.append(url.contains("?") ? "&" : "?");
            params.forEach((k, v) -> sb.append(k).append("=").append(v).append("&"));
            url = sb.substring(0, sb.length() - 1);
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        if (headers != null) {
            headers.forEach(httpHeaders::set);
        }
        HttpEntity<Object> entity = new HttpEntity<>(null, httpHeaders);
        return restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
    }

    // POST 请求
    public <T> ResponseEntity<T> post(String url, Object requestBody, Class<T> responseType, Map<String, String> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (headers != null) {
            headers.forEach(httpHeaders::set);
        }
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, httpHeaders);
        return restTemplate.exchange(url, HttpMethod.POST, entity, responseType);
    }

    public <T> ResponseEntity<T> post(String url, Object requestBody, Class<T> responseType, Map<String, String> headers, Map<String, String> params) {
        // 拼接 params 到 url
        if (params != null && !params.isEmpty()) {
            StringBuilder sb = new StringBuilder(url);
            sb.append(url.contains("?") ? "&" : "?");
            params.forEach((k, v) -> sb.append(k).append("=").append(v).append("&"));
            url = sb.substring(0, sb.length() - 1); // 去掉最后一个&
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        if (headers != null) {
            headers.forEach(httpHeaders::set);
        }
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, httpHeaders);
        return restTemplate.exchange(url, HttpMethod.POST, entity, responseType);
    }

    // PUT 请求
    public <T> ResponseEntity<T> put(String url, Object requestBody, Class<T> responseType, Map<String, String> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (headers != null) {
            headers.forEach(httpHeaders::set);
        }
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, httpHeaders);
        return restTemplate.exchange(url, HttpMethod.PUT, entity, responseType);
    }

    // DELETE 请求
    public <T> ResponseEntity<T> delete(String url, Class<T> responseType, Map<String, String> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (headers != null) {
            headers.forEach(httpHeaders::set);
        }
        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
        return restTemplate.exchange(url, HttpMethod.DELETE, entity, responseType);
    }

    // 文件上传
    public <T> ResponseEntity<T> uploadFile(String url, MultipartFile file, Class<T> responseType, Map<String, String> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (headers != null) {
            headers.forEach(httpHeaders::set);
        }
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, httpHeaders);
        return restTemplate.exchange(url, HttpMethod.POST, entity, responseType);
    }
}