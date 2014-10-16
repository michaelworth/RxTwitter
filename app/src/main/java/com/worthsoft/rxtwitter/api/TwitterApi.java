package com.worthsoft.rxtwitter.api;

import retrofit.client.Response;
import retrofit.http.POST;
import rx.Observable;

public interface TwitterApi {
    public static final String ENDPOINT = "https://api.twitter.com/";

    @POST("/oauth/request_token")
    Observable<Response> getRequestToken();

}
