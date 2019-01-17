package com.sortlayout.dragon.drawables;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.widget.TextView;

public class DrawablesActivity extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawables);
        TextView textView = (TextView) findViewById(R.id.msgText);
        RainbowDrawable.Builder builder = new RainbowDrawable.Builder();
        builder.setRadius((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10,getResources().getDisplayMetrics()));
        builder.setStrokeWidth((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10,getResources().getDisplayMetrics()));
        builder.setDuration(1000);
        builder.setBackgroundColor(Color.parseColor("#f0000000"));
        textView.setBackgroundDrawable(builder.Build());
    }
}
