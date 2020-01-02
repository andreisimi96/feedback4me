package com.example.feedback4me.UserInformation;

import java.util.Date;
import java.util.Objects;

public class Feedback
{
    public String authorUid;
    public String text;
    //positive, negative or neutral
    public String impression;
    public Date date;
    public boolean anonymous;
}
