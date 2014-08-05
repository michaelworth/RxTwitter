package com.worthsoft.rxtwitter.api;

import com.worthsoft.rxtwitter.api.models.RequestToken;

import retrofit.http.Header;
import retrofit.http.POST;

public interface TwitterApi {
    @POST("oauth/request_token")
    RequestToken getRequestToken(@Header("Authorization") String authorization);
}
