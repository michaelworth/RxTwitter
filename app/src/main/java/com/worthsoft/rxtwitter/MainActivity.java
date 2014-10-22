package com.worthsoft.rxtwitter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.worthsoft.rxtwitter.api.TwitterClient;
import com.worthsoft.rxtwitter.api.TwitterRequestHelper;
import com.worthsoft.rxtwitter.api.models.RequestToken;

import java.util.regex.Pattern;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = (WebView) findViewById(R.id.webview);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new TwitterWebViewClient());
    }

    @Override
    protected void onResume() {
        super.onResume();

        TwitterRequestHelper twitterRequestHelper = new TwitterRequestHelper(null);

        twitterRequestHelper.getRequestToken()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<RequestToken>() {
                    @Override
                    public void call(RequestToken requestToken) {
                        Log.i(TAG, "OnNext");
                        Log.i(TAG, "token: " + requestToken.getToken());
                        Log.i(TAG, "secret: " + requestToken.getSecret());
                        Log.i(TAG, "callback confirmed: " + requestToken.isConfirmed());
                        webView.loadUrl(TwitterClient.TWITTER_AUTH_URL_BASE + requestToken.getToken());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.i(TAG, "OnError");
                        throwable.printStackTrace();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        Log.i(TAG, "OnComplete");
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class TwitterWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!TextUtils.isEmpty(url) && url.startsWith(TwitterClient.OAUTH_CALLBACK)) {
                String payload = url.substring(TwitterClient.OAUTH_CALLBACK.length() + 2);
                String params[] = payload.split(Pattern.quote("&"));
                if (params.length == 2) {
                    Log.i(TAG, "token: " + params[0]);
                    Log.i(TAG, "verifier: " + params[1]);
                }
                return true;
            }

            return super.shouldOverrideUrlLoading(view, url);
        }
    }
}
