package com.worthsoft.rxtwitter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.worthsoft.rxtwitter.api.TwitterApi;
import com.worthsoft.rxtwitter.api.TwitterClient;
import com.worthsoft.rxtwitter.api.models.RequestToken;

import retrofit.RestAdapter;
import rx.functions.Action0;
import rx.functions.Action1;


public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(TwitterApi.ENDPOINT)
                .setClient(new TwitterClient())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        TwitterApi twitterApi = restAdapter.create(TwitterApi.class);

        twitterApi.getRequestToken().subscribe(new Action1<RequestToken>() {
            @Override
            public void call(RequestToken requestToken) {
                Log.i(TAG, "OnNext");
                Log.i(TAG, "token: " + requestToken.getToken());
                Log.i(TAG, "secret: " + requestToken.getSecret());
                Log.i(TAG, "isConfirmed: " + requestToken.isConfirmed());
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
}
