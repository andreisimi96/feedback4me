package com.example.feedback4me.UserInformation;

import android.net.Uri;

import java.util.ArrayList;

public class User
{
    public String birthdate;
    public Uri avatarUri;
    public String fullname;
    public String email;
    public ArrayList<String> friends;
    public ArrayList<String> groups;
    public boolean allowsAnonymousFeedback;

    public User(String fullname, String email,
                String birthdate, String uid,
                Uri avatarUri)
    {
        this.fullname = fullname;
        this.email = email;
        this.birthdate = birthdate;
        friends = new ArrayList<>();
        friends.add(uid);
        groups = new ArrayList<>();
        allowsAnonymousFeedback = true;
    }


}
