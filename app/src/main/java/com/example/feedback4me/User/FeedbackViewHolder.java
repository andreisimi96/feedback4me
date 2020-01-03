package com.example.feedback4me.User;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.feedback4me.R;
import com.example.feedback4me.Tools.GlideWrapper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FeedbackViewHolder extends RecyclerView.ViewHolder
{
    public ConstraintLayout root;
    public ImageView authorImage;
    public TextView feedbackAuthor;
    public TextView feedbackText;
    public TextView feedbackDate;

    public FeedbackViewHolder(View itemView)
    {
        super(itemView);
        root = itemView.findViewById(R.id.list_root);
        authorImage = itemView.findViewById(R.id.list_feedback_author_photo);
        feedbackAuthor = itemView.findViewById(R.id.list_feedback_author);
        feedbackText = itemView.findViewById(R.id.list_feedback_text);
        feedbackDate = itemView.findViewById(R.id.list_feedback_date);
    }

    public void setFeedbackAuthor(String string)
    {
        feedbackAuthor.setText(string);
    }

    public void setFeedbackText(String string)
    {
        feedbackText.setText(string);
    }

    public void setFeedbackDate(Date date)
    {
        if (date != null)
        {
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
            String strDate = dateFormat.format(date);
            feedbackDate.setText(strDate);
        }
    }
    public void setAuthorImage(Uri authorImageUri)
    {

        GlideWrapper.setAvatarFromUri(authorImage.getContext(), authorImageUri, authorImage);
    }
}