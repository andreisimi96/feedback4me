package com.example.feedback4me.LoginFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.feedback4me.R;

public class SignUpFragment extends Fragment
{


    public SignUpFragment() {}

    public static SignUpFragment newInstance()
    {
        SignUpFragment fragment = new SignUpFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_sign_up, container, false);

        /*
           On click -> send request to firebase auth
         */
        Button signUpButton = rootView.findViewById(R.id.signup_button);
        TextView email = rootView.findViewById(R.id.edit_text_email_signup);
        EditText password = rootView.findViewById(R.id.edit_text_password_signup);
        signUpButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String
            }
        });



        return rootView;
    }


}
