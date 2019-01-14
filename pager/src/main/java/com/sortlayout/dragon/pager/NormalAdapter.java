package com.sortlayout.dragon.pager;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

public class NormalAdapter implements HVLPaper.Adapter {
    private static final String TAG = "NormalAdapter";
    private Context context;
    public NormalAdapter(Context context){
        this.context = context;
    }

    @Override
    public void startUpdate(ViewGroup paper) {

    }

    @Override
    public void finishUpdate(ViewGroup paper) {

    }

    @Override
    public void onCreate(ViewGroup itemContainer, int position) {
        Log.e(TAG, "onCreate position " + position);
        TextView textView = new TextView(context);
        textView.setBackgroundColor(Color.BLUE);
        textView.setText("paper" + position);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 100);
        textView.setTextColor(Color.WHITE);
        textView.setGravity(Gravity.CENTER);
        itemContainer.addView(textView);
    }

    @Override
    public void onShow(ViewGroup itemContainer, int position) {
        Log.e(TAG, "onShow position " + position);
    }

    @Override
    public void onActive(ViewGroup itemContainer, int position) {
        Log.e(TAG, "onActive position " + position);
    }

    @Override
    public void onInactive(ViewGroup itemContainer, int position) {
        Log.e(TAG, "onInactive position " + position);
    }

    @Override
    public void onHide(ViewGroup itemContainer, int position) {
        Log.e(TAG, "onHide position " + position);
    }

    @Override
    public void onDestroy(ViewGroup itemContainer, int position) {
        Log.e(TAG, "onDestroy position " + position);
        itemContainer.removeAllViews();
    }

    @Override
    public int getCount() {
        return 10;
    }
}
