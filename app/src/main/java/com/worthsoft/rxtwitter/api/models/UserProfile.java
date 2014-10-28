package com.worthsoft.rxtwitter.api.models;

import com.google.gson.annotations.SerializedName;

public class UserProfile {

    private long id;
    private String name;
    private String description;
    @SerializedName("favourites_count")
    private long favouritesCount;
    @SerializedName("followers_count")
    private long followersCount;
    @SerializedName("friends_count")
    private long friendsCount;
    @SerializedName("statuses_count")
    private long statusesCount;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public long getFavouritesCount() {
        return favouritesCount;
    }

    public long getFollowersCount() {
        return followersCount;
    }

    public long getFriendsCount() {
        return friendsCount;
    }

    public long getStatusesCount() {
        return statusesCount;
    }
}
