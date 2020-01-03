package com.example.feedback4me.Tools;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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
