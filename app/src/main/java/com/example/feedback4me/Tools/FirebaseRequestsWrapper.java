package com.example.feedback4me.Tools;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.feedback4me.MainActivity;
import com.example.feedback4me.User.Feedback;
import com.example.feedback4me.User.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class FirebaseRequestsWrapper
{

    public static void createUserWithEmailAndPassword(final Fragment callingFragment,
                                                      final String email,
                                                      final String password,
                                                      final String fullname,
                                                      final String birthdate)
    {
        final ProgressDialog progress = UIHelpers.createProgressDialog(callingFragment.getContext(),
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

    //called only the first time someone logs in with google
    public static void createUserDBForGoogleSignin(final Activity callingActivity)
    {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(callingActivity);
        Log.d("OMG", "hehe");
        if (account != null)
        {
            Log.d("OMG", "hehe2");
            String name = account.getDisplayName();
            String email = account.getEmail();
            Uri avatarUri = account.getPhotoUrl();
            Log.d("OMG", email);
            Log.d("OMG", name);
            Log.d("OMG", avatarUri.toString());

            FirebaseUser firebaseUser =  FirebaseAuth.getInstance().getCurrentUser();

            String userUid = firebaseUser.getUid();
            DatabaseReference usersDbReference = FirebaseDatabase.getInstance().getReference();
            User user = new User(name, email, "Missing Birthdate", userUid, null);
            usersDbReference = usersDbReference.child("users/" + userUid);

            Map<String, User> userData = new HashMap<>();
            userData.put("User Data", user);
            usersDbReference.setValue(userData);

        }
    }

    public static void changeUserAvatar(final Context callingContext, final Uri newAvatarUri)
    {
        final ProgressDialog progress = UIHelpers.createProgressDialog(callingContext,
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
                Toast.makeText(callingContext,
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
                                                                        .child("users/" + userUid + "/User Data/avatarUri");
                                    avatarReference.setValue(photoUrl);
                                    progress.dismiss();
                                }
                            }
                        });

            }
        });

    }

    public static void sendFeedbackToFirebase(final Feedback feedback,
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

    public static void sendFriendRequest (String senderUid, String receiverUid)
    {
        String requestsPath = "users/" + receiverUid + "/User Data/requests";
        DatabaseReference usersDbReference = FirebaseDatabase.getInstance()
                .getReference()
                .child(requestsPath);

        //simulate hashmap
        usersDbReference.child(senderUid).setValue(senderUid);
    }

    public static void acceptFriendRequest (String senderUid, String receiverUid)
    {
        String senderFriendsPath = "users/" + senderUid + "/User Data/friends";
        String receiverFriendsPath = "users/" + receiverUid + "/User Data/friends";

        DatabaseReference senderDbReference = FirebaseDatabase.getInstance()
                .getReference()
                .child(senderFriendsPath);

        DatabaseReference receiverDbReference = FirebaseDatabase.getInstance()
                .getReference()
                .child(receiverFriendsPath);

        senderDbReference.child(receiverUid).setValue(receiverUid);
        receiverDbReference.child(senderUid).setValue(senderUid);

        deleteFriendRequest(senderUid, receiverUid);
    }

    public static void deleteFriendRequest(String senderUid, String receiverUid)
    {
        String senderRequestsPath = "users/" + receiverUid + "/User Data/requests/" + senderUid;
        DatabaseReference senderDbReference = FirebaseDatabase.getInstance()
                .getReference()
                .child(senderRequestsPath);

        senderDbReference.removeValue();
    }
}
