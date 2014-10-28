package com.worthsoft.rxtwitter.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.worthsoft.rxtwitter.api.TwitterApi;
import com.worthsoft.rxtwitter.api.TwitterClient;
import com.worthsoft.rxtwitter.api.models.AccessToken;
import com.worthsoft.rxtwitter.api.models.UserProfile;
import com.worthsoft.rxtwitter.data.AccessTokenStore;

import retrofit.RestAdapter;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class UserProfileActivity extends Activity {

    private static final String TAG = UserProfileActivity.class.getSimpleName();

    TwitterApi twitterApi;
    AccessTokenStore accessTokenStore = new AccessTokenStore();
    Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AccessToken accessToken = accessTokenStore.loadAccessToken(this);

        if (accessToken != null) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(TwitterApi.ENDPOINT)
                    .setClient(new TwitterClient(accessToken.getToken(), accessToken.getSecret()))
                    .build();

            twitterApi = restAdapter.create(TwitterApi.class);

            subscription = twitterApi.getUserProfile()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<UserProfile>() {
                        @Override
                        public void call(UserProfile userProfile) {
                            Log.i(TAG, "Id: " + userProfile.getId());
                            Log.i(TAG, "Name: " + userProfile.getName());
                            Log.i(TAG, "Description : " + userProfile.getDescription());
                            Log.i(TAG, "Statuses Count: " + userProfile.getStatusesCount());
                            Log.i(TAG, "Friends Count: " + userProfile.getFriendsCount());
                            Log.i(TAG, "Followers Count: " + userProfile.getFollowersCount());
                            Log.i(TAG, "Favourites Count: " + userProfile.getFavouritesCount());
                        }
                    });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }
}
