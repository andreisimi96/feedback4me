package com.example.feedback4me.User;

import android.net.Uri;

import java.util.ArrayList;

public class User
{
    public String birthdate;
    public Uri avatarUri;
    public String uid;
    public String fullname;
    public String email;
    public ArrayList<String> friends;
    public ArrayList<String> groups;
    public ArrayList<String> pendingRequests;
    public boolean allowsAnonymousFeedback;

    public User() {}

    public User(String fullname, String email,
                String birthdate, String uid,
                Uri avatarUri)
    {
        this.fullname = fullname;
        this.email = email;
        this.birthdate = birthdate;
        this.uid = uid;
        friends = new ArrayList<>();
        friends.add(uid);
        groups = new ArrayList<>();
        pendingRequests = new ArrayList<>();
        allowsAnonymousFeedback = true;
        this.avatarUri = avatarUri;
    }


}
