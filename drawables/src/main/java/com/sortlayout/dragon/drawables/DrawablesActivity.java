package com.sortlayout.dragon.drawables;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

public class DrawablesActivity extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawables);
        TextView textView = (TextView) findViewById(R.id.msgText);
        textView.setBackgroundDrawable(new RainbowDrawable());
    }
}
