package com.example.feedback4me.Tools;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.feedback4me.MainActivity;
import com.example.feedback4me.UserInformation.Feedback;
import com.example.feedback4me.UserInformation.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    public static void sendFeedbackToFirebase(final Fragment callingFragment,
                                              final Feedback feedback,
                                              final String userUid)
    {
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference usersDbReference = database.getReference();
        String authorUid = firebaseAuth.getCurrentUser().getUid();
        usersDbReference = usersDbReference.child("users/" + userUid + "/User Feedback/");

        Map<String, Feedback> userData = new HashMap<>();
        userData.put(Integer.toString(feedback.hashCode()), feedback);
        usersDbReference.push().setValue(feedback);
    }

}
