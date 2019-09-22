package com.example.dragon.exampleproject.databinding.adapter;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.databinding.BindingAdapter;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import java.text.NumberFormat;

public class CustomTextViewBindingAdapter {

    private static final int REQUEST_VISIBLE = 1;
    private static final int NUMBER_ANIMATION = 2;

    @BindingAdapter("app:textNumberWithAnimation")
    public static void setTextWithAnimation(final TextView textView, int number) {
        Object tag = textView.getTag(NUMBER_ANIMATION);

        int oldNumber = 100;
        int newNumber = 1000;
        ValueAnimator valueAnimator = ObjectAnimator.ofInt(oldNumber,newNumber);
        valueAnimator.setDuration(3000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int)animation.getAnimatedValue();
                textView.setText(formatNumber(value));
            }
        });
        valueAnimator.start();
    }

    private static String formatNumber(int number){
        return NumberFormat.getInstance().format(number);
    }

    @BindingAdapter("app:visibleWithAnimation")
    public static void setVisibleWithAnimation(final TextView textView, final int visible) {
        Object tag = textView.getTag(REQUEST_VISIBLE);
        if(tag != null && tag instanceof Integer){
            int requestVisible = (int)tag;
            if(visible == requestVisible){
                return;
            }
        }
        //cancel old animation.
        Animation animation = textView.getAnimation();
        if (animation != null) {
            animation.cancel();
        }
        //start new animation.
        final float startAlpha = visible == View.VISIBLE ? 0f : 1f;
        final float endAlpha = visible == View.VISIBLE ? 1f : 0f;
        textView.setVisibility(View.VISIBLE);
        AlphaAnimation alphaAnimation = new AlphaAnimation(startAlpha, endAlpha);
        alphaAnimation.setDuration(3000);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                textView.setVisibility(visible);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        textView.startAnimation(alphaAnimation);
    }
}
