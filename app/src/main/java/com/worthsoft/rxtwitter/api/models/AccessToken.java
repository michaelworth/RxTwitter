package com.worthsoft.rxtwitter.api.models;

import com.google.gson.annotations.SerializedName;

public class AccessToken {
    @SerializedName("oauth_token")
    private String token;
    @SerializedName("oauth_token_secret")
    private String secret;

    public AccessToken(String token, String secret) {
        this.token = token;
        this.secret = secret;
    }

    public String getToken() {
        return token;
    }

    public String getSecret() {
        return secret;
    }
}
