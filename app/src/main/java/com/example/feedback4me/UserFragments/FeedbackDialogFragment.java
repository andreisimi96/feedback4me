package com.example.feedback4me.UserFragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.example.feedback4me.LoginActivity;
import com.example.feedback4me.R;
import com.example.feedback4me.Tools.FirebaseWrapper;
import com.example.feedback4me.UserInformation.Feedback;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;


public class FeedbackDialogFragment extends DialogFragment
{
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
                                sendFeedbackToFirebase(view, FirebaseAuth.getInstance().getUid());
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


        return feedbackDialog;
    }

    public void sendFeedbackToFirebase(View view, String userUid)
    {
        Switch feedbackTypeSwitch = view.findViewById(R.id.feedback_type);

        RadioGroup radioGroup = view.findViewById(R.id.feedback_impression);
        int radioButtonID = radioGroup.getCheckedRadioButtonId();
        View radioButtonView = radioGroup.findViewById(radioButtonID);
        int idx = radioGroup.indexOfChild(radioButtonView);
        RadioButton radioButton = (RadioButton) radioGroup.getChildAt(idx);

        EditText enteredFeedback = view.findViewById(R.id.feedback_enter_form);

        //create feedback object
        Feedback feedback = new Feedback();
        feedback.anonymous = feedbackTypeSwitch.isChecked();
        feedback.impression = radioButton.getText().toString();
        feedback.text = enteredFeedback.getText().toString();
        feedback.authorUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        feedback.date = Calendar.getInstance().getTime();

        FirebaseWrapper.sendFeedbackToFirebase(this, feedback, userUid);
    }
}
