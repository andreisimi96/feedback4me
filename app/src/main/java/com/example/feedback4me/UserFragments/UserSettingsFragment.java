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
import com.example.feedback4me.Tools.GlideWrapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserSettingsFragment extends Fragment
{

    ImageView user_avatar;
    TextView user_name;
    TextView user_email;

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
        user_avatar = rootView.findViewById(R.id.user_avatar);
        user_name = rootView.findViewById(R.id.user_name_settings);
        user_email = rootView.findViewById(R.id.user_email_settings);


        fillFragmentWithFirebaseData();

        return rootView;
    }
    public void fillFragmentWithFirebaseData()
    {
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
}
