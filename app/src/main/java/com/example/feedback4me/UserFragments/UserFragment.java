package com.example.feedback4me.UserFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.feedback4me.R;
import com.example.feedback4me.Tools.FirebaseAdaptersWrapper;
import com.example.feedback4me.Tools.FirebaseRequestsWrapper;
import com.example.feedback4me.User.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserFragment extends Fragment
{
    private String userUid;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter recyclerAdapter;


    public UserFragment() {}

    public static UserFragment newInstance()
    {
        UserFragment fragment = new UserFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_user, container, false);
        fillWithFirebaseData(rootView);
        attachClickHandlers(rootView);
        setupRecyclerAdapter(rootView);

        return rootView;
    }

    private void fillWithFirebaseData(View rootView)
    {
        final ImageView userAvatar = rootView.findViewById(R.id.user_avatar);
        final TextView username = rootView.findViewById(R.id.user_name);
        final TextView birthdate = rootView.findViewById(R.id.user_birthdate);

        String userDataPath = "users/" + userUid + "/User Data/";
        DatabaseReference dbReference = FirebaseDatabase.getInstance()
                                                        .getReference()
                                                        .child(userDataPath);
        ValueEventListener postListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                User user = dataSnapshot.getValue(User.class);

                if (user.fullname != null && user.birthdate != null)
                {
                    username.setText(user.fullname);
                    birthdate.setText(user.birthdate);
                }
                FirebaseRequestsWrapper.asyncSetAvatar(userUid, userAvatar);
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

    private void attachClickHandlers(View rootView)
    {
        Button writeFeedback = rootView.findViewById(R.id.write_feedback);
        writeFeedback.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FeedbackDialogFragment newFragment = FeedbackDialogFragment.newInstance();
                newFragment.setUserUid(userUid);
                newFragment.show(getFragmentManager(), "dialog");
            }
        });
    }

    private void setupRecyclerAdapter(View rootView)
    {
        recyclerView = rootView.findViewById(R.id.feedback_recyclerview);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(false);

        recyclerAdapter = FirebaseAdaptersWrapper.getFeedbackFirebaseRecyclerAdapter(getActivity(), userUid);
        recyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver()
        {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount)
            {
                recyclerView.smoothScrollToPosition(positionStart);
            }
        });

        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.startListening();
    }

    public String getUserUid()
    {
        return userUid;
    }

    public void setUserUid(String userUid)
    {
        this.userUid = userUid;
    }
}
