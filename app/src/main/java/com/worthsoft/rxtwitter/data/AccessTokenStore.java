package com.worthsoft.rxtwitter.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.worthsoft.rxtwitter.api.models.AccessToken;

public class AccessTokenStore {

    private static final String PREF_ACCESS_TOKEN = "PREF_ACCESS_TOKEN";
    private static final String KEY_ACCESS_TOKEN = "KEY_ACCESS_TOKEN";
    private static final String KEY_ACCESS_TOKEN_SECRET = "KEY_ACCESS_TOKEN_SECRET";

    private AccessToken accessToken = null;

    public void clearAccessToken(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_ACCESS_TOKEN, Context.MODE_PRIVATE).edit().clear();
        editor.apply();
        accessToken = null;
    }

    public void storeAccessToken(Context context, AccessToken accessToken) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_ACCESS_TOKEN, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_ACCESS_TOKEN, accessToken.getToken());
        editor.putString(KEY_ACCESS_TOKEN_SECRET, accessToken.getSecret());
        editor.apply();

        this.accessToken = accessToken;
    }

    public AccessToken loadAccessToken(Context context) {
        if (accessToken == null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_ACCESS_TOKEN, Context.MODE_PRIVATE);
            String accessToken = sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
            String accessTokenSecret = sharedPreferences.getString(KEY_ACCESS_TOKEN_SECRET, null);

            if (!TextUtils.isEmpty(accessToken) && !TextUtils.isEmpty(accessTokenSecret)) {
                this.accessToken = new AccessToken(accessToken, accessTokenSecret);
            }
        }

        return this.accessToken;
    }
}
