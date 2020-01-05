package com.example.feedback4me.User;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.feedback4me.R;
import com.example.feedback4me.Tools.FirebaseWrapper;
import com.example.feedback4me.Tools.GlideWrapper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FriendViewHolder extends RecyclerView.ViewHolder
{
    public ConstraintLayout root;
    private ImageView friendImage;
    private ImageView anonymousImage;
    private TextView username;
    private TextView birthDate;
    private String friendUid;
    private DatabaseReference dbReference;

    public FriendViewHolder(View itemView)
    {
        super(itemView);
        root = itemView.findViewById(R.id.list_root);
        friendImage = itemView.findViewById(R.id.list_username_photo);
        username = itemView.findViewById(R.id.list_username);
        birthDate = itemView.findViewById(R.id.list_birth_date);
        anonymousImage = itemView.findViewById(R.id.list_anonymous_photo);
    }

    public void setFriendUid(String friendUid)
    {
        this.friendUid = friendUid;
    }

    public void fillFriendViewHolder()
    {

        String userDataPath = "users/" + friendUid + "/User Data/";
        dbReference = FirebaseDatabase.getInstance()
                            .getReference()
                            .child(userDataPath);
        Log.d("FriendViewHolder", "I reach here");
        ValueEventListener postListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.fullname);
                birthDate.setText(user.birthdate);
                if (user.allowsAnonymousFeedback)
                {
                    anonymousImage.setVisibility(View.VISIBLE);
                }
                FirebaseWrapper.asyncSetAvatar(friendUid, friendImage);

                /*
                TODO go to User Page
                 */
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                // Getting Post failed, log a message
                Log.w("Error", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        dbReference.addValueEventListener(postListener);
    }
}