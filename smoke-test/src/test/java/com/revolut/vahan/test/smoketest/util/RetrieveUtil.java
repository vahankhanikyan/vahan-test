package com.revolut.vahan.test.smoketest.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class RetrieveUtil {
    public static <T> T retrieveResourceFromResponse(HttpResponse response, Class<T> clazz)
            throws IOException {

        String jsonFromResponse = retrieveResourceFromResponse(response);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonFromResponse, clazz);
    }

    public static String retrieveResourceFromResponse(HttpResponse response)
            throws IOException {
        return EntityUtils.toString(response.getEntity());
    }

    public static HttpResponse get(String url) throws IOException {
        HttpUriRequest request = new HttpGet(url);
        return HttpClientBuilder.create().build().execute(request);
    }

    public static <T> T get(String url, Class<T> clazz) throws IOException {
        HttpResponse response = get(url);
        return retrieveResourceFromResponse(response, clazz);
    }

    public static String getRetrieveString(String url) throws IOException {
        HttpResponse response = get(url);
        return retrieveResourceFromResponse(response);
    }

    public static HttpResponse postJson(String url, String json) throws IOException {
        HttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        return client.execute(httpPost);
    }

    public static HttpResponse postJson(String url, Object obj) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return postJson(url, objectMapper.writeValueAsString(obj));
    }

    public static String postJsonRetrieveString(String url, String json) throws IOException {
        HttpResponse response = postJson(url, json);
        return retrieveResourceFromResponse(response);
    }

    public static String postJsonRetrieveString(String url, Object obj) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return postJsonRetrieveString(url, objectMapper.writeValueAsString(obj));
    }

    public static <T> T postJson(String url, String json, Class<T> clazz) throws IOException {
        HttpResponse response = postJson(url, json);
        return retrieveResourceFromResponse(response, clazz);
    }

    public static <T> T postJson(String url, Object obj, Class<T> clazz) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return postJson(url, objectMapper.writeValueAsString(obj), clazz);
    }
}
