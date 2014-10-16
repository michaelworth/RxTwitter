package com.worthsoft.rxtwitter.api.models;

import com.google.gson.annotations.SerializedName;

public class RequestToken {
    @SerializedName("oauth_token")
    private String token;
    @SerializedName("oauth_token_secret")
    private String secret;
    @SerializedName("oauth_callback_confirmed")
    private boolean confirmed;

    public RequestToken(String token, String secret, boolean confirmed) {
        this.token = token;
        this.secret = secret;
        this.confirmed = confirmed;
    }

    public String getToken() {
        return token;
    }

    public String getSecret() {
        return secret;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
