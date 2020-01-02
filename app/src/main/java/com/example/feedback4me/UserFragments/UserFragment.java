package com.example.feedback4me.UserFragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.feedback4me.LoginActivity;
import com.example.feedback4me.R;
import com.example.feedback4me.Tools.GlideWrapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserFragment extends Fragment
{

    public UserFragment() {}

    public static UserFragment newInstance()
    {
        UserFragment fragment = new UserFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_user, container, false);
        fillWithFirebaseData(rootView);
        attachClickHandlers(rootView);

        return rootView;
    }

    public void fillWithFirebaseData(View rootView)
    {
        ImageView user_avatar = rootView.findViewById(R.id.user_avatar_home);
        TextView user_name = rootView.findViewById(R.id.user_name_home);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
        {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            Uri photoUrl = user.getPhotoUrl();

            user_name.setText(name);

            GlideWrapper.setAvatarFromUri(getActivity(), photoUrl, user_avatar);
        }
        else
        {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    }

    public void attachClickHandlers(View rootView)
    {
        Button writeFeedback = rootView.findViewById(R.id.write_feedback);
        writeFeedback.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                
            }
        });
    }
}
