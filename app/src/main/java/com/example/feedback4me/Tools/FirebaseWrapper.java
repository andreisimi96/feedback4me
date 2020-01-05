package com.example.feedback4me.Tools;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.feedback4me.MainActivity;
import com.example.feedback4me.R;
import com.example.feedback4me.User.Feedback;
import com.example.feedback4me.User.FeedbackViewHolder;
import com.example.feedback4me.User.FriendViewHolder;
import com.example.feedback4me.User.User;
import com.example.feedback4me.User.UserSearchViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FirebaseWrapper
{

    public static void createUserWithEmailAndPassword(final Fragment callingFragment,
                                                      final String email,
                                                      final String password,
                                                      final String fullname,
                                                      final String birthdate)
    {
        final ProgressDialog progress = UI.createProgressDialog(callingFragment.getContext(),
                                                "Loading", "Configuring your account...", false);

        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(callingFragment.getActivity(), new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (!task.isSuccessful())
                        {
                            Toast.makeText(callingFragment.getActivity(), "ERROR",Toast.LENGTH_LONG).show();
                            progress.dismiss();
                        }
                        else
                        {
                            //get current user & db
                            final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            final String userUid = firebaseAuth.getCurrentUser().getUid();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(fullname)
                                    .build();
                            firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    /*
                                        Add user specific folder to firebase.
                                    */
                                    DatabaseReference usersDbReference = FirebaseDatabase.getInstance().getReference();
                                    Uri avatarUri = firebaseUser.getPhotoUrl();
                                    User user = new User(fullname, email, birthdate, userUid, avatarUri);
                                    usersDbReference = usersDbReference.child("users/" + userUid);

                                    Map<String, User> userData = new HashMap<>();
                                    userData.put("User Data", user);
                                    usersDbReference.setValue(userData);

                                    progress.dismiss();
                                    callingFragment.startActivity(new Intent(callingFragment.getContext(), MainActivity.class));

                                }
                            });

                        }
                    }
                });
    }

    public static void createUserDBForGoogleSignin(final Fragment callingFragment)
    {
        /*
        TODO
         */
    }

    public static void changeUserAvatar(final Fragment callingFragment, final Uri newAvatarUri)
    {
        final ProgressDialog progress = UI.createProgressDialog(callingFragment.getContext(),
                                        "Loading", "Adding profile picture...", false);

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userUid = firebaseUser.getUid();

        StorageReference photoRef = FirebaseStorage.getInstance().getReference().child(userUid);

        UploadTask uploadTask = photoRef.putFile(newAvatarUri);
        uploadTask.addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception exception)
            {
                Toast.makeText(callingFragment.getContext(),
                        "An error occured. Can't upload new profile picture.",
                            Toast.LENGTH_LONG).show();
                progress.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(newAvatarUri)
                        .build();
                firebaseUser.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    String photoUrl = firebaseUser.getPhotoUrl().toString();
                                    DatabaseReference avatarReference = FirebaseDatabase.getInstance()
                                                                        .getReference()
                                                                        .child("users/" + userUid + "/User Data/avatarUid");
                                    avatarReference.setValue(photoUrl);
                                    progress.dismiss();
                                }
                            }
                        });

            }
        });

    }

    public static void sendFeedbackToFirebase(final Fragment callingFragment,
                                              final Feedback feedback,
                                              final String userUid)
    {
        String feedbackPath = "users/" + userUid + "/User Feedback/";
        DatabaseReference usersDbReference = FirebaseDatabase.getInstance()
                                            .getReference()
                                            .child(feedbackPath);

        Map<String, Feedback> userData = new HashMap<>();
        userData.put(Integer.toString(feedback.hashCode()), feedback);
        usersDbReference.push().setValue(feedback);
    }

    public static FirebaseRecyclerAdapter getFeedbackFirebaseRecyclerAdapter(final String userUid,
                                                                             final RecyclerView recyclerView)
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
                               Feedback feedback = getFeedbackFromSnapshot(snapshot);
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

       recyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver()
       {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount)
            {
                recyclerView.smoothScrollToPosition(positionStart);
            }
        });


        recyclerView.setAdapter(recyclerAdapter);
        return recyclerAdapter;
    }

    public static FirebaseRecyclerAdapter getFriendsFirebaseRecyclerAdapter(final String userUid,
                                                                             final RecyclerView recyclerView)
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
                                user.uid = snapshot.getKey();
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

        recyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver()
        {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount)
            {
                recyclerView.smoothScrollToPosition(positionStart);
            }
        });


        recyclerView.setAdapter(recyclerAdapter);
        return recyclerAdapter;
    }


    public static FirebaseRecyclerAdapter getSearchFirebaseRecyclerAdapter(final RecyclerView recyclerView,
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
                                Log.d("HELPME", snapshot.getValue().toString());

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
                        //TODO: Go to user fragment
                    }
                });
            }

        };
        recyclerView.setAdapter(recyclerAdapter);
        return recyclerAdapter;
    }


    public static void asyncSetAvatar (String userUid,
                                      final ImageView imageView)
    {
        StorageReference photoRef = FirebaseStorage.getInstance()
                                                    .getReference()
                                                    .child(userUid);

        photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
        {
            @Override
            public void onSuccess(Uri uri)
            {
                GlideWrapper.setAvatarFromUri(imageView.getContext(), uri, imageView);
            }
        });
    }

    public static void sendFriendRequest (String userUid)
    {

    }

    public static Feedback getFeedbackFromSnapshot(DataSnapshot snapshot)
    {
        Feedback feedback = new Feedback();

        Object authorObj = snapshot.child("author").getValue();
        Object textObj = snapshot.child("text").getValue();
        Object impressionObj = snapshot.child("impression").getValue();
        Object dateObj = snapshot.child("date/time").getValue();
        Object authorUidObj = snapshot.child("authorUid").getValue();

        //return default if one is null
        if (authorObj == null ||
                textObj == null || impressionObj == null ||
                dateObj == null || authorUidObj == null)
        {
            return feedback;
        }

        feedback.author = authorObj.toString();
        feedback.text = textObj.toString();
        feedback.impression = impressionObj.toString();
        long epochTimeMs = Long.parseLong(dateObj.toString());
        feedback.date = new Date(epochTimeMs);
        feedback.authorUid = authorUidObj.toString();

        return feedback;
    }
}
