package com.example.feedback4me.UserFragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.feedback4me.LoginActivity;
import com.example.feedback4me.R;
import com.example.feedback4me.Tools.FirebaseRequestsWrapper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserSettingsFragment extends Fragment
{
    public int PICK_IMAGE = 1522;

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
                    FirebaseRequestsWrapper.changeUserAvatar(this, newAvatarUri);
                }
            }
        }
    }

    public void fillWithFirebaseData(View rootView)
    {
        ImageView userAvatar = rootView.findViewById(R.id.user_avatar);
        TextView userName = rootView.findViewById(R.id.user_name_settings);
        TextView userEmail = rootView.findViewById(R.id.user_email_settings);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
        {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            userName.setText(name);
            userEmail.setText(email);

            FirebaseRequestsWrapper.asyncSetAvatar(user.getUid(), userAvatar);
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




    }
}
