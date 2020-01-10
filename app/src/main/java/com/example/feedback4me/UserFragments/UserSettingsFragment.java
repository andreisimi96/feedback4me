package com.example.feedback4me.UserFragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.feedback4me.LoginActivity;
import com.example.feedback4me.R;
import com.example.feedback4me.Tools.FirebaseRequestsWrapper;
import com.example.feedback4me.User.Feedback;
import com.example.feedback4me.User.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class UserSettingsFragment extends Fragment
{
    public int PICK_IMAGE = 1522;
    private FirebaseUser user;

    public UserSettingsFragment() {}

    public static UserSettingsFragment newInstance()
    {
        UserSettingsFragment fragment = new UserSettingsFragment();
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
        user = FirebaseAuth.getInstance().getCurrentUser();

        View rootView = inflater.inflate(R.layout.fragment_user_settings, container, false);
        fillWithFirebaseData(rootView);
        attachClickHandlers(rootView);

        return rootView;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE)
        {
            if (data != null)
            {
                Uri newAvatarUri = data.getData();
                if (newAvatarUri != null)
                {
                    FirebaseRequestsWrapper.changeUserAvatar(getContext(), newAvatarUri);
                }
            }
        }
    }

    public void fillWithFirebaseData(View rootView)
    {
        ImageView userAvatar = rootView.findViewById(R.id.user_avatar);
        TextView userName = rootView.findViewById(R.id.user_name_settings);
        TextView userEmail = rootView.findViewById(R.id.user_email_settings);
        final Switch anonymous = rootView.findViewById(R.id.allow_anonymous_feedback);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
        {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();

            userName.setText(name);
            userEmail.setText(email);

            FirebaseRequestsWrapper.asyncSetAvatar(user.getUid(), userAvatar);

            //check if user is anonymous
            String userDataPath = "users/" + user.getUid() + "/User Data/";
            DatabaseReference dbReference = FirebaseDatabase.getInstance()
                    .getReference()
                    .child(userDataPath);
            ValueEventListener postListener = new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    User user = dataSnapshot.getValue(User.class);
                    anonymous.setChecked(user.allowsAnonymousFeedback);
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
        else
        {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    }
    public void attachClickHandlers(View rootView)
    {
        TextView changeAvatar = rootView.findViewById(R.id.change_profile_picture);
        TextView logOut = rootView.findViewById(R.id.user_log_out);
        TextView changeBirthDate = rootView.findViewById(R.id.change_birth_date);
        final Switch anonymous = rootView.findViewById(R.id.allow_anonymous_feedback);

        changeAvatar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        logOut.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getContext(), LoginActivity.class));

                    GoogleSignIn.getClient(
                            getContext(),
                            new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                    ).signOut();
            }
        });

        anonymous.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String feedbackPath = "users/" + user.getUid() + "/User Data/allowsAnonymousFeedback";
                DatabaseReference usersDbReference = FirebaseDatabase.getInstance()
                        .getReference()
                        .child(feedbackPath);

                usersDbReference.setValue(anonymous.isChecked());
            }
        });

        //you can change birth date ONLY if you have google account
        if ( GoogleSignIn.getLastSignedInAccount(rootView.getContext()) != null)
        {
            changeBirthDate.setVisibility(View.VISIBLE);
            changeBirthDate.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //TODO: Change birth date for google account
                }
            });

        }



    }
}
