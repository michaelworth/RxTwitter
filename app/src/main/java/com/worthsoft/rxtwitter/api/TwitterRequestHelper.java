package com.worthsoft.rxtwitter.api;

import com.worthsoft.rxtwitter.api.models.RequestToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import retrofit.client.Response;
import rx.Observable;
import rx.functions.Func1;

public class TwitterRequestHelper {

    private static final String OAUTH_TOKEN_PREFIX = "oauth_token=";
    private static final String OAUTH_TOKEN_SECRET_PREFIX = "oauth_token_secret=";
    private static final String OAUTH_CALLBACK_CONFIRMED_PREFIX = "oauth_callback_confirmed=";

    private final TwitterApi twitterApi;

    public TwitterRequestHelper(TwitterApi twitterApi) {
        this.twitterApi = twitterApi;
    }

    public Observable<RequestToken> getRequestToken() {
        return twitterApi.getRequestToken().flatMap(new Func1<Response, Observable<? extends RequestToken>>() {
            @Override
            public Observable<? extends RequestToken> call(Response response) {
                BufferedReader stream = null;
                String token = null;
                String secret = null;
                boolean callbackConfirmed = false;

                try {
                    stream = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String result = stream.readLine();

                    StringTokenizer tokenizer = new StringTokenizer(result, "&");
                    while (tokenizer.hasMoreTokens()) {
                        String currentToken = tokenizer.nextToken();
                        if (currentToken.startsWith(OAUTH_TOKEN_PREFIX)) {
                            token = currentToken.substring(OAUTH_TOKEN_PREFIX.length());
                        } else if (currentToken.startsWith(OAUTH_TOKEN_SECRET_PREFIX)) {
                            secret = currentToken.substring(OAUTH_TOKEN_SECRET_PREFIX.length());
                        } else if (currentToken.startsWith(OAUTH_CALLBACK_CONFIRMED_PREFIX)) {
                            callbackConfirmed = Boolean.valueOf(currentToken.substring(OAUTH_CALLBACK_CONFIRMED_PREFIX.length()));
                        }
                    }
                } catch (Exception e) {
                    return Observable.error(e);
                } finally {
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                return Observable.just(new RequestToken(token, secret, callbackConfirmed));
            }
        });

    }
}
