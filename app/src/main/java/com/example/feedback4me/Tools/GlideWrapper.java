package com.example.feedback4me.Tools;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.feedback4me.R;

public class GlideWrapper
{
    //in case the imageview fails, a default image is loaded
    public static void setAvatarFromUri(final Context context, final Uri uri, final ImageView imageView)
    {
        Glide.with(context)
                .load(uri)
                .circleCrop()
                .placeholder(R.mipmap.ic_avatar_round)
                .into(imageView);
    }
}
