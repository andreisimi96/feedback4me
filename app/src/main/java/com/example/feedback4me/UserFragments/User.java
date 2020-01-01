package com.example.feedback4me.UserFragments;

import java.util.ArrayList;

public class User
{
    public String birthdate;
    public String fullname;
    public String email;
    public ArrayList<String> friends;
    public ArrayList<String> groups;
    public boolean allowsAnonymousFeedback;

    public User(String fullname, String email, String birthdate)
    {
        this.fullname = fullname;
        this.email = email;
        this.birthdate = birthdate;
        friends = new ArrayList<>();
        groups = new ArrayList<>();
        allowsAnonymousFeedback = true;
    }


}
