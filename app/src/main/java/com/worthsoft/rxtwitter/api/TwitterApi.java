package com.worthsoft.rxtwitter.api;

import com.worthsoft.rxtwitter.utils.AuthUtils;

import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import rx.Observable;

public interface TwitterApi {
    public static final String ENDPOINT = "https://api.twitter.com/";

    @POST("/oauth/request_token")
    Observable<Response> getRequestToken();

    @FormUrlEncoded
    @POST("/oauth/access_token")
    Observable<Response> getAccessToken(@Field(AuthUtils.VERIFIER) String verifier);
}
