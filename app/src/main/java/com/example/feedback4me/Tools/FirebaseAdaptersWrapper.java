package com.example.feedback4me.Tools;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.feedback4me.MainActivity;
import com.example.feedback4me.R;
import com.example.feedback4me.User.Feedback;
import com.example.feedback4me.User.FeedbackViewHolder;
import com.example.feedback4me.User.FriendViewHolder;
import com.example.feedback4me.User.RequestsViewHolder;
import com.example.feedback4me.User.User;
import com.example.feedback4me.User.UserSearchViewHolder;
import com.example.feedback4me.UserFragments.UserFragment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseAdaptersWrapper
{
    public static FirebaseRecyclerAdapter getFeedbackFirebaseRecyclerAdapter(final String userUid)
    {
        FirebaseRecyclerAdapter recyclerAdapter;

        final String feedbackPath = "users/" + userUid + "/User Feedback/";
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child(feedbackPath);

        FirebaseRecyclerOptions<Feedback> options =
                new FirebaseRecyclerOptions.Builder<Feedback>()
                        .setQuery(query, new SnapshotParser<Feedback>()
                        {
                            @NonNull
                            @Override
                            public Feedback parseSnapshot(@NonNull DataSnapshot snapshot)
                            {
                                Feedback feedback = snapshot.getValue(Feedback.class);
                                return feedback;
                            }
                        })
                        .build();

        recyclerAdapter = new FirebaseRecyclerAdapter<Feedback, FeedbackViewHolder>(options)
        {
            @Override
            public FeedbackViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.feedback_list_item, parent, false);

                return new FeedbackViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(final FeedbackViewHolder holder, final int position, final Feedback feedback)
            {
                holder.setFeedbackAuthor(feedback.author);
                holder.setFeedbackText(feedback.text);
                holder.setFeedbackDate(feedback.date);


                //setup feedback profile image
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                StorageReference photoRef = storageRef.child(feedback.authorUid);

                //Set image
                photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                {
                    @Override
                    public void onSuccess(Uri uri)
                    {
                        holder.setAuthorImage(uri);
                    }
                });


                //go to user fragment
                holder.root.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        //TODO: Go to user fragment
                    }
                });
            }

        };

        return recyclerAdapter;
    }

    public static FirebaseRecyclerAdapter getFriendsFirebaseRecyclerAdapter(final String userUid)
    {
        FirebaseRecyclerAdapter recyclerAdapter;

        final String feedbackPath = "users/" + userUid + "/User Data/friends/";
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child(feedbackPath)
                .orderByKey();

        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(query, new SnapshotParser<User>()
                        {
                            @NonNull
                            @Override
                            public User parseSnapshot(@NonNull DataSnapshot snapshot)
                            {
                                //the logic isn't done here
                                //due to clutter constraints
                                User user = new User();
                                user.uid = snapshot.getValue().toString();
                                return user;
                            }
                        })
                        .build();


        recyclerAdapter = new FirebaseRecyclerAdapter<User, FriendViewHolder>(options)
        {
            @Override
            public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.friend_list_item, parent, false);

                return new FriendViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(final FriendViewHolder holder, final int position, final User user)
            {
                holder.setFriendUid(user.uid);
                holder.fillFriendViewHolder();

                //go to user fragment
                holder.root.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        //TODO: Go to user fragment
                    }
                });
            }

        };

        return recyclerAdapter;
    }

    public static FirebaseRecyclerAdapter getRequestsFirebaseRecyclerAdapter(final String userUid)
    {

        FirebaseRecyclerAdapter recyclerAdapter;

        final String feedbackPath = "users/" + userUid + "/User Data/requests/";
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child(feedbackPath)
                .orderByKey();

        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(query, new SnapshotParser<User>()
                        {
                            @NonNull
                            @Override
                            public User parseSnapshot(@NonNull DataSnapshot snapshot)
                            {
                                //the logic isn't done here
                                //due to clutter constraints
                                String userUid = snapshot.getValue().toString();
                                Log.d("REQUESTS", userUid);
                                User user = new User();
                                user.uid = userUid;
                                return user;
                            }
                        })
                        .build();


        recyclerAdapter = new FirebaseRecyclerAdapter<User, RequestsViewHolder>(options)
        {
            @Override
            public RequestsViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.request_list_item, parent, false);

                return new RequestsViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(final RequestsViewHolder holder, final int position, final User user)
            {
                holder.setUserUid(user.uid);
                holder.fillRequestsViewHolder();

                //go to user fragment
                holder.root.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        //TODO: Go to user fragment
                    }
                });
            }

        };

        return recyclerAdapter;
    }

    public static FirebaseRecyclerAdapter getSearchFirebaseRecyclerAdapter(final Activity callingActivity,
                                                                            final String queryText)
    {
        FirebaseRecyclerAdapter recyclerAdapter;

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("users")
                .orderByChild("User Data/fullname")
                .startAt(queryText, "fullname")
                .endAt(queryText +  "\uf8ff", "fullname");

        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(query, new SnapshotParser<User>()
                        {
                            @NonNull
                            @Override
                            public User parseSnapshot(@NonNull DataSnapshot snapshot)
                            {
                                Log.d("HELPME", "entering here");
                                //the logic isn't done here
                                //due to clutter constraints
                                User user = new User();
                                user.uid = snapshot.getKey();

                                return user;
                            }
                        })
                        .build();

        recyclerAdapter = new FirebaseRecyclerAdapter<User, UserSearchViewHolder>(options)
        {
            @Override
            public UserSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.search_list_item, parent, false);

                return new UserSearchViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(final UserSearchViewHolder holder, final int position, final User user)
            {
                holder.setUserUid(user.uid);
                holder.fillUserSearchViewHolder();

                //go to user fragment
                holder.root.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        MainActivity castedCallingActivity = ((MainActivity)callingActivity);
                        castedCallingActivity.openNavigationFragment(UserFragment.newInstance());
                    }
                });
            }

        };
        return recyclerAdapter;
    }

}
