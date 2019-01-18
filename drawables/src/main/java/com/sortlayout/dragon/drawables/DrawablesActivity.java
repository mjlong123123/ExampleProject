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
        builder.setRadius((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,20,getResources().getDisplayMetrics()));
        builder.setStrokeWidth((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,5,getResources().getDisplayMetrics()));
        builder.setDuration(2000);
        builder.setBackgroundColor(Color.parseColor("#60000000"));
        builder.setGradientColorAndPosition(new int[]{Color.BLUE,Color.RED,Color.YELLOW},new float[]{0.0f,0.1f,1.f});
        textView.setBackgroundDrawable(builder.build());
    }
}
