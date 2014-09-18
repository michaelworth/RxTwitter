package com.worthsoft.rxtwitter.api;

import android.util.Log;

import com.worthsoft.rxtwitter.utils.AuthUtils;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import retrofit.client.Client;
import retrofit.client.OkClient;
import retrofit.client.Request;
import retrofit.client.Response;
import retrofit.mime.TypedOutput;

public class TwitterClient implements Client {

    // Replace with your specific API key
    private final String TWITTER_API_KEY = "ioEcs86I43YA7ChqVCsaz7Il0";

    final Client client;
    final String token;

    public TwitterClient(String token) {
        this.client = new OkClient();
        this.token = token;
    }

    @Override
    public Response execute(Request request) throws IOException {
        Request signedRequest = sign(request);
        return client.execute(signedRequest);
    }

    private Request sign(Request request) {
        final HashMap<String, String> params = extractParams(request);
        AuthUtils.insertOAuthParams(params, TWITTER_API_KEY, nonce, token);

        final String signingKey = AuthUtils.generateSigningKey(TWITTER_API_KEY, token);
        final String parameterString = AuthUtils.generateParameterString(params);
        final String signatureBase = AuthUtils.generateSignatureBaseString(request.getMethod(), request.getUrl(), parameterString);
        final String signature = AuthUtils.generateSignature(signingKey, signatureBase);

        return new Request(request.getMethod(), request.getUrl(), request.getHeaders(), request.getBody());
    }

    private HashMap<String, String> extractParams(Request request) {
        HashMap<String, String> params = new HashMap<>();

        // Extract any query params
        try {
            URL url = new URL(request.getUrl());
            insertKeyValuePairs(params, url.getQuery());

            TypedOutput body = request.getBody();
            if (body.length() > 0) {
                Log.i("TEST", "Mime type: " + body.mimeType());
                insertKeyValuePairs(params, "");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return params;
    }

    private void insertKeyValuePairs(HashMap<String, String> params, String data) {
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
