package com.example.feedback4me.UserFragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import com.example.feedback4me.R;
import com.example.feedback4me.Tools.FirebaseRequestsWrapper;
import com.example.feedback4me.User.Feedback;
import com.example.feedback4me.User.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;


public class FeedbackDialogFragment extends DialogFragment
{
    private String userUid;

    public FeedbackDialogFragment() {}

    public static FeedbackDialogFragment newInstance()
    {
        FeedbackDialogFragment fragment = new FeedbackDialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_feedback_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                                sendFeedbackToFirebase(view, userUid);
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                                dialog.dismiss();
                            }
                        }
                );

        Dialog feedbackDialog = builder.create();
        ViewGroup.LayoutParams params = feedbackDialog.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        feedbackDialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);


        //check if user allows anonymous feedback
        final Switch anonymousSwitch = view.findViewById(R.id.feedback_type);
        String userDataPath = "users/" + userUid + "/User Data/";
        DatabaseReference dbReference = FirebaseDatabase.getInstance()
                .getReference()
                .child(userDataPath);
        ValueEventListener postListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                User user = dataSnapshot.getValue(User.class);
                if (!user.allowsAnonymousFeedback)
                {
                    anonymousSwitch.setVisibility(View.GONE);
                }
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



        return feedbackDialog;
    }

    public void sendFeedbackToFirebase(View view, String userUid)
    {
        final Switch anonymousSwitch = view.findViewById(R.id.feedback_type);

        RadioGroup radioGroup = view.findViewById(R.id.feedback_impression);
        int radioButtonID = radioGroup.getCheckedRadioButtonId();
        View radioButtonView = radioGroup.findViewById(radioButtonID);
        int idx = radioGroup.indexOfChild(radioButtonView);
        RadioButton radioButton = (RadioButton) radioGroup.getChildAt(idx);

        EditText enteredFeedback = view.findViewById(R.id.feedback_enter_form);

        //create feedback object
        Feedback feedback = new Feedback();
        feedback.impression = radioButton.getText().toString();
        feedback.text = enteredFeedback.getText().toString();

        //anonymous feedback
        if (anonymousSwitch.isChecked())
        {
            feedback.author = "Anonymous";
            feedback.authorUid = "Anonymous";
        }
        else
        {
            feedback.author = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            feedback.authorUid = FirebaseAuth.getInstance().getUid();
        }
        feedback.date = Calendar.getInstance().getTime();
        FirebaseRequestsWrapper.sendFeedbackToFirebase(feedback, userUid);
    }


    public String getUserUid()
    {
        return userUid;
    }

    public void setUserUid(String userUid)
    {
        this.userUid = userUid;
    }
}
