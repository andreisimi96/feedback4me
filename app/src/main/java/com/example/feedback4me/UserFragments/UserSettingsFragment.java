package com.example.feedback4me.UserFragments;

import android.content.DialogInterface;
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
import com.example.feedback4me.MainActivity;
import com.example.feedback4me.R;
import com.example.feedback4me.Tools.GlideWrapper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserSettingsFragment extends Fragment
{
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
    public void fillWithFirebaseData(View rootView)
    {
        ImageView user_avatar = rootView.findViewById(R.id.user_avatar);
        TextView user_name = rootView.findViewById(R.id.user_name_settings);
        TextView user_email = rootView.findViewById(R.id.user_email_settings);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
        {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            user_name.setText(name);
            user_email.setText(email);
            GlideWrapper.setAvatarFromUri(getActivity(), photoUrl, user_avatar);

        }
        else
        {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    }
    public void attachClickHandlers(View rootView)
    {
        TextView change_avatar = rootView.findViewById(R.id.change_profile_picture);
        TextView logOut = rootView.findViewById(R.id.user_log_out);

        change_avatar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //TODO
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
