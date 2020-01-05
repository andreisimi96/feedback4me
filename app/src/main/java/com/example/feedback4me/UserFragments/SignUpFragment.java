package com.example.feedback4me.UserFragments;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.feedback4me.R;
import com.example.feedback4me.Tools.FirebaseRequestsWrapper;
import com.google.firebase.auth.FirebaseAuth;

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
            FirebaseRequestsWrapper.createUserWithEmailAndPassword(this, email, password, fullname, birthdate);
        }
    }

    public void datePick(View rootView)
    {
        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        final EditText birthdateEditText = rootView.findViewById(R.id.edit_text_birthdate_signup);


        DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int yearDatePicker,
                                          int monthOfYear, int dayOfMonth) {

                        if (yearDatePicker >= year - 16)
                        {
                            Toast.makeText(getContext(), "The birthdate does not respect our terms and conditions.", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            birthdateEditText.setText(dayOfMonth + "-"
                                    + (monthOfYear + 1) + "-" + yearDatePicker);
                        }
                    }
                }, year, month, day);
        dpd.show();
    }
}
