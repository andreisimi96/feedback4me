package com.example.feedback4me.Tools;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.feedback4me.MainActivity;
import com.example.feedback4me.R;
import com.example.feedback4me.User.Feedback;
import com.example.feedback4me.User.FeedbackViewHolder;
import com.example.feedback4me.User.User;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
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
        final ProgressDialog progress = new ProgressDialog(callingFragment.getContext());
        progress.setTitle("Loading");
        progress.setMessage("Configuring your account...");
        progress.setCancelable(false);
        progress.show();

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
                            final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            final FirebaseDatabase database = FirebaseDatabase.getInstance();

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
                                    DatabaseReference usersDbReference = database.getReference();
                                    String userUid = firebaseAuth.getCurrentUser().getUid();
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

    public static void changeUserAvatar(final Fragment callingFragment, final Uri newAvatarUri)
    {
        final ProgressDialog progress = new ProgressDialog(callingFragment.getContext());
        progress.setTitle("Loading");
        progress.setMessage("Adding profile picture...");
        progress.setCancelable(false);
        progress.show();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String userUid = firebaseAuth.getUid();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference photoRef = storageRef.child(userUid);

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

                photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                {
                    @Override
                    public void onSuccess(Uri uri)
                    {
                        holder.setAuthorImage(uri);
                    }
                }).addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception exception)
                    {
                        // Handle any errors
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

}
