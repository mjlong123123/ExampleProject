package com.example.dragon.exampleproject.databinding.adapter;

import android.databinding.BindingAdapter;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ViewTarget;
import com.example.dragon.exampleproject.R;

public class CustomImageViewBindingAdapter {

    @BindingAdapter("app:url")
    public static void setUrl(ImageView imageView, String url) {
        if(TextUtils.isEmpty(url)){
            return;
        }
        Glide.with(imageView).load(R.drawable.ic_launcher_background).into(imageView);
//        imageView.setImageResource(R.drawable.ic_launcher_background);
    }
}
