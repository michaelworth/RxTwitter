package com.worthsoft.rxtwitter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.worthsoft.rxtwitter.api.TwitterClient;
import com.worthsoft.rxtwitter.api.TwitterOAuthHelper;
import com.worthsoft.rxtwitter.api.models.AccessToken;
import com.worthsoft.rxtwitter.api.models.RequestToken;
import com.worthsoft.rxtwitter.data.AccessTokenStore;
import com.worthsoft.rxtwitter.utils.AuthUtils;

import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @InjectView(R.id.webview)
    WebView webView;

    @InjectView(R.id.error_view)
    View errorView;

    private Subscription subscription;
    private RequestToken requestToken = null;
    private TwitterOAuthHelper twitterOAuthHelper = new TwitterOAuthHelper();
    private AccessTokenStore accessTokenStore = new AccessTokenStore();

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new TwitterWebViewClient());
    }

    @Override
    protected void onResume() {
        super.onResume();

        AccessToken accessToken = accessTokenStore.loadAccessToken(this);
        if (accessToken != null) {
            Log.i(TAG, "LOADED ACCESS TOKEN!");
            Log.i(TAG, "LOADED access token: " + accessToken.getToken());
            Log.i(TAG, "LOADED token secret: " + accessToken.getSecret());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    @OnClick(R.id.button_login)
    public void loginButtonPressed(Button button) {
        button.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
        requestToken();
    }

    @OnClick(R.id.button_retry)
    public void retryButtonPressed() {
        errorView.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
        requestToken();
    }

    private void handleError(Throwable throwable) {
        throwable.printStackTrace();
        webView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
        MainActivity.this.requestToken = null;
    }

    private void requestToken() {
        subscription = twitterOAuthHelper.getRequestToken()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new TwitterRequestTokenObserver());
    }

    private class TwitterRequestTokenObserver implements Observer<RequestToken> {

        @Override
        public void onCompleted() {
            if (subscription != null && !subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
        }

        @Override
        public void onError(Throwable e) {
            handleError(e);

            if (subscription != null && !subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
        }

        @Override
        public void onNext(RequestToken requestToken) {
            Log.i(TAG, "token: " + requestToken.getToken());
            Log.i(TAG, "secret: " + requestToken.getSecret());
            Log.i(TAG, "callback confirmed: " + requestToken.isConfirmed());
            MainActivity.this.requestToken = requestToken;
            webView.loadUrl(TwitterClient.TWITTER_AUTH_URL_BASE + requestToken.getToken());
        }
    }

    private class TwitterWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!TextUtils.isEmpty(url) && url.startsWith(TwitterClient.OAUTH_CALLBACK)) {
                String payload = url.substring(TwitterClient.OAUTH_CALLBACK.length() + 1);
                String params[] = payload.split(Pattern.quote("&"));
                if (params.length == 2) {
                    String oAuthToken = null;
                    if (!TextUtils.isEmpty(params[0]) && params[0].startsWith(AuthUtils.TOKEN + "=")) {
                        oAuthToken = params[0].substring(AuthUtils.TOKEN.length() + 1);
                    }

                    String oAuthVerifier = null;
                    if (!TextUtils.isEmpty(params[1]) && params[1].startsWith(AuthUtils.VERIFIER + "=")) {
                        oAuthVerifier = params[1].substring(AuthUtils.VERIFIER.length() + 1);
                    }

                    // Ensure original request token matches returned value with verifier
                    if (requestToken.getToken().equals(oAuthToken) && !TextUtils.isEmpty(oAuthVerifier)) {
                        subscription = twitterOAuthHelper.getAccessToken(oAuthToken, oAuthVerifier)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new TwitterAccessTokenObserver());
                    } else {
                        handleError(new Exception("Bad request token response"));
                    }
                }

                return true;
            }

            return super.shouldOverrideUrlLoading(view, url);
        }
    }


    private class TwitterAccessTokenObserver implements Observer<AccessToken> {
        @Override
        public void onCompleted() {
            if (subscription != null && !subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
        }

        @Override
        public void onError(Throwable e) {
            handleError(e);

            if (subscription != null && !subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
        }

        @Override
        public void onNext(AccessToken accessToken) {
            Log.i(TAG, "access token: " + accessToken.getToken());
            Log.i(TAG, "token secret: " + accessToken.getSecret());
            new AccessTokenStore().storeAccessToken(MainActivity.this, accessToken);
        }
    }
}
