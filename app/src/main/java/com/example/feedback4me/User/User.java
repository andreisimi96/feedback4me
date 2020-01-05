package com.example.feedback4me.User;

import android.net.Uri;

import java.util.ArrayList;
import java.util.HashMap;

public class User
{
    public String birthdate;
    public Uri avatarUri;
    public String uid;
    public String fullname;
    public String email;
    public HashMap<String, String> friends;
    public HashMap<String, String> groups;
    public HashMap<String, String> requests;
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
        friends = new HashMap<>();
        groups = new HashMap<>();
        requests = new HashMap<>();
        allowsAnonymousFeedback = true;
        this.avatarUri = avatarUri;
    }


}
