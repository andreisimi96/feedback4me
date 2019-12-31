package com.example.feedback4me.NavigationFragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.feedback4me.LoginActivity;
import com.example.feedback4me.MainActivity;
import com.example.feedback4me.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeFragment extends Fragment
{
    ImageView user_avatar;
    TextView user_name;
    TextView user_email;

    public HomeFragment() {}

    public static HomeFragment newInstance()
    {
        HomeFragment fragment = new HomeFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        user_avatar = rootView.findViewById(R.id.user_avatar_home);
        user_name = rootView.findViewById(R.id.user_name_home);
        user_email = rootView.findViewById(R.id.user_email_home);

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

            Glide.with(this)
                    .load(photoUrl)
                    .circleCrop()
                    .into(user_avatar);

        }
        else
        {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    }
}
