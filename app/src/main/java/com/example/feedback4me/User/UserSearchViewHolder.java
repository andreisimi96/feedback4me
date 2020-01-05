package com.example.feedback4me.User;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class UserSearchViewHolder extends RecyclerView.ViewHolder
{
    public ConstraintLayout root;
    private ImageView userImage;
    private Button addFriend;
    private TextView username;
    private TextView birthDate;
    private DatabaseReference dbReference;
    private String userUid;

    public UserSearchViewHolder(View itemView)
    {
        super(itemView);
        root = itemView.findViewById(R.id.list_root);
        userImage = itemView.findViewById(R.id.list_username_photo);
        addFriend = itemView.findViewById(R.id.add_friend_button);
        username = itemView.findViewById(R.id.list_username);
        birthDate = itemView.findViewById(R.id.list_birth_date);

        addFriend.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });

    }

    public void setUserUid(String userUid) {this.userUid = userUid;}

    public void fillUserSearchViewHolder()
    {

        String userDataPath = "users/" + userUid + "/User Data/";
        dbReference = FirebaseDatabase.getInstance()
                .getReference()
                .child(userDataPath);

        ValueEventListener postListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                // Get Post object and use the values to update the UI
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.fullname);
                birthDate.setText(user.birthdate);
                FirebaseWrapper.asyncSetAvatar(userUid, userImage);

                /*
                TODO
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