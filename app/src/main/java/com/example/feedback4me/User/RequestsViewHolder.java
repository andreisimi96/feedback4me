package com.example.feedback4me.User;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.feedback4me.R;
import com.example.feedback4me.Tools.FirebaseRequestsWrapper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RequestsViewHolder extends RecyclerView.ViewHolder
{
    public ConstraintLayout root;
    private ImageView userImage;
    private TextView username;
    private TextView birthdate;
    private String userUid;
    private DatabaseReference dbReference;

    public RequestsViewHolder(View itemView)
    {
        super(itemView);
        root = itemView.findViewById(R.id.list_root);
        username = itemView.findViewById(R.id.list_username);
        birthdate = itemView.findViewById(R.id.list_birthdate);
        userImage = itemView.findViewById(R.id.list_username_photo);
    }

    public void setUserUid(String userUid)
    {
        this.userUid = userUid;
    }

    public void fillRequestsViewHolder()
    {
        String userDataPath = "users/" + userUid + "/User Data/";
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
                birthdate.setText(user.birthdate);

                FirebaseRequestsWrapper.asyncSetAvatar(userUid, userImage);
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

    public String getUserUid()
    {
        return userUid;
    }
}