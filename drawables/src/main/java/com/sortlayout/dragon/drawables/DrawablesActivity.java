package com.sortlayout.dragon.drawables;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DrawablesActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawables);
        TextView textView = findViewById(R.id.msgText);
        textView.setBackgroundDrawable(new RainbowDrawable());
    }
}
