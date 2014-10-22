package com.worthsoft.rxtwitter.api;

import android.text.TextUtils;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;
import com.worthsoft.rxtwitter.utils.AuthUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit.client.Client;
import retrofit.client.Header;
import retrofit.client.OkClient;
import retrofit.client.Request;
import retrofit.client.Response;
import retrofit.mime.TypedOutput;

public class TwitterClient implements Client {

    private static final String TWITTER_CONSUMER_KEY = "ioEcs86I43YA7ChqVCsaz7Il0";
    private static final String TWITTER_CONSUMER_SECRET = "nZt9dbZibRlaCoLpfkrbV2RoN7sChIuDY7uLVYLWK0tSoQYX8W";

    public static final String OAUTH_CALLBACK = "http://localhost/sign-in-with-twitter/";
    public static final String TWITTER_AUTH_URL_BASE = "https://api.twitter.com/oauth/authorize?oauth_token=";

    private static final int CONNECT_TIMEOUT_MILLIS = 15000;
    private static final int READ_TIMEOUT_MILLIS = 20000;

    final Client client;
    final String token;

    public TwitterClient(String token) {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        client.setReadTimeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        client.setProtocols(Arrays.asList(Protocol.HTTP_1_1, Protocol.HTTP_2));
        this.client = new OkClient(client);
        this.token = token;
    }

    @Override
    public Response execute(Request request) throws IOException {
        Request signedRequest = sign(request);
        return client.execute(signedRequest);
    }

    private Request sign(Request request) {
        final HashMap<String, String> params = extractParams(request);

        List<Header> headers = request.getHeaders();
        ArrayList<Header> newHeaders = new ArrayList<>();
        newHeaders.addAll(headers);
        newHeaders.add(new Header("Authorization",
                AuthUtils.generateAuthorizationHeader(
                        params,
                        TWITTER_CONSUMER_KEY,
                        TWITTER_CONSUMER_SECRET,
                        token,
                        OAUTH_CALLBACK,
                        request.getMethod(),
                        request.getUrl(),
                        AuthUtils.generateNonce(),
                        AuthUtils.generateTimestamp())));

        return new Request(request.getMethod(), request.getUrl(), newHeaders, request.getBody());
    }

    private HashMap<String, String> extractParams(Request request) {
        HashMap<String, String> params = new HashMap<>();

        // Extract any query params
        try {
            URL url = new URL(request.getUrl());
            insertKeyValuePairs(params, url.getQuery());

            TypedOutput body = request.getBody();
            if (body != null && body.length() > 0) {
                Log.i("TEST", "Mime type: " + body.mimeType());
                insertKeyValuePairs(params, "");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return params;
    }

    private void insertKeyValuePairs(HashMap<String, String> params, String data) {
        if (!TextUtils.isEmpty(data)) {
            String[] querySets = data.split("&");
            if (querySets.length > 0) {
                for (String query : querySets) {
                    String[] keyValuePair = query.split("=");
                    if (keyValuePair.length == 2) {
                        params.put(keyValuePair[0], keyValuePair[1]);
                    }
                }
            }
        }
    }
}
