package com.example.feedback4me.UserFragments;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.feedback4me.MainActivity;
import com.example.feedback4me.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpFragment extends Fragment
{
    private FirebaseAuth firebaseAuth;


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
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout.fragment_sign_up, container, false);

        /*
           On click -> send request to firebase auth
         */
        Button signUpButton = rootView.findViewById(R.id.signup_button);
        signUpButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                doSignUp(rootView);
            }
        });

        /*
         Set listener for DatePicker.
         */
        EditText birthdateEditText = rootView.findViewById(R.id.edit_text_birthdate_signup);
        birthdateEditText.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                datePick(rootView);
            }
        });

        return rootView;
    }


    public void doSignUp(View rootView)
    {
        final EditText emailTextView = rootView.findViewById(R.id.edit_text_email_signup);
        final EditText passwordEditText = rootView.findViewById(R.id.edit_text_password_signup);
        final EditText usernameEditText = rootView.findViewById(R.id.edit_text_username_signup);
        final EditText birthdateEditText = rootView.findViewById(R.id.edit_text_birthdate_signup);

        final String email = emailTextView.getText().toString();
        final String password = passwordEditText.getText().toString();
        final String fullname = usernameEditText.getText().toString();
        final String birthdate = birthdateEditText.getText().toString();

        if(email.length() <= 6 || !email.contains("@"))
        {
            Toast.makeText(getContext(), "Please enter your email address", Toast.LENGTH_SHORT).show();
        }
        else if (fullname.length() <= 4)
        {
            Toast.makeText(getContext(), "Please enter your full name", Toast.LENGTH_SHORT).show();
        }
        else if (password.length() < 8)
        {
            Toast.makeText(getContext(), "Password must have more than 8 characters", Toast.LENGTH_SHORT).show();
        }
        else
        {
            final ProgressDialog progress = new ProgressDialog(getContext());
            progress.setTitle("Loading");
            progress.setMessage("Configuring your account...");
            progress.setCancelable(false);
            progress.show();

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (!task.isSuccessful())
                            {
                                Toast.makeText(getContext(), "ERROR",Toast.LENGTH_LONG).show();
                                progress.dismiss();
                            }
                            else
                            {
                                //get current user
                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
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
                                        User user = new User(fullname, email, birthdate);
                                        DatabaseReference usersDbReference = database.getReference();
                                        String userUID = firebaseAuth.getCurrentUser().getUid();
                                        usersDbReference = usersDbReference.child("users/" + userUID);

                                        Map<String, User> userData = new HashMap<>();
                                        userData.put("User Data", user);
                                        usersDbReference.setValue(userData);

                                        progress.dismiss();
                                        startActivity(new Intent(getContext(), MainActivity.class));

                                    }
                                });

                            }
                        }
                    });
        }
    }

    public void datePick(View rootView)
    {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        final EditText birthdateEditText = rootView.findViewById(R.id.edit_text_birthdate_signup);


        DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        birthdateEditText.setText(dayOfMonth + "-"
                                + (monthOfYear + 1) + "-" + year);
                    }
                }, year, month, day);
        dpd.show();
    }
}
