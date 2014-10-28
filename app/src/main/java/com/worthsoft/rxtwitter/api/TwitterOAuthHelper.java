package com.worthsoft.rxtwitter.api;

import com.worthsoft.rxtwitter.api.models.AccessToken;
import com.worthsoft.rxtwitter.api.models.RequestToken;
import com.worthsoft.rxtwitter.utils.AuthUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import retrofit.RestAdapter;
import retrofit.client.Response;
import rx.Observable;
import rx.functions.Func1;

/**
 * Helper class to handle fetching and parsing out initial oauth request token
 */
public class TwitterOAuthHelper {

    /**
     * Creates a TwitterApi with a TwitterClient using null for a token to fetch a request token
     *
     * @return Observable that emits a request token when request is complete
     */
    public Observable<RequestToken> getRequestToken() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(TwitterApi.ENDPOINT)
                .setClient(new TwitterClient(null, null))
                .build();

        TwitterApi twitterApi = restAdapter.create(TwitterApi.class);

        return twitterApi.getRequestToken().flatMap(new Func1<Response, Observable<RequestToken>>() {
            @Override
            public Observable<RequestToken> call(Response response) {
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
                        if (currentToken.startsWith(AuthUtils.TOKEN + "=")) {
                            token = currentToken.substring(AuthUtils.TOKEN.length() + 1);
                        } else if (currentToken.startsWith(AuthUtils.TOKEN_SECRET + "=")) {
                            secret = currentToken.substring(AuthUtils.TOKEN_SECRET.length() + 1);
                        } else if (currentToken.startsWith(AuthUtils.CALLBACK_CONFIRMED + "=")) {
                            callbackConfirmed = Boolean.valueOf(currentToken.substring(AuthUtils.CALLBACK_CONFIRMED.length() + 1));
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

    public Observable<AccessToken> getAccessToken(String token, String verifier) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(TwitterApi.ENDPOINT)
                .setClient(new TwitterClient(token, null))
                .build();

        TwitterApi twitterApi = restAdapter.create(TwitterApi.class);

        return twitterApi.getAccessToken(verifier).flatMap(new Func1<Response, Observable<AccessToken>>() {
            @Override
            public Observable<AccessToken> call(Response response) {
                BufferedReader stream = null;
                String token = null;
                String secret = null;

                try {
                    stream = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String result = stream.readLine();

                    StringTokenizer tokenizer = new StringTokenizer(result, "&");
                    while (tokenizer.hasMoreTokens()) {
                        String currentToken = tokenizer.nextToken();
                        if (currentToken.startsWith(AuthUtils.TOKEN + "=")) {
                            token = currentToken.substring(AuthUtils.TOKEN.length() + 1);
                        } else if (currentToken.startsWith(AuthUtils.TOKEN_SECRET + "=")) {
                            secret = currentToken.substring(AuthUtils.TOKEN_SECRET.length() + 1);
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

                return Observable.just(new AccessToken(token, secret));
            }
        });
    }
}
