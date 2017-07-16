package com.example.sam.anoumyouschat.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sam on 7/7/2017.
 */

public class Data {
    @SerializedName("user")
    private User user;
    @SerializedName("friend")
    private User friend;
    @SerializedName("conversation")
    private String conversation;

    public Data() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getFriend() {
        return friend;
    }

    public void setFriend(User friend) {
        this.friend = friend;
    }

    public String getConversation() {
        return conversation;
    }

    public void setConversation(String conversation) {
        this.conversation = conversation;
    }
}
