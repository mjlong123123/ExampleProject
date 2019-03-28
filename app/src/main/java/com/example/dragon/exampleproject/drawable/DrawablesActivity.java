package com.example.dragon.exampleproject.drawable;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.dragon.graphics.RainbowDrawable;
import com.example.dragon.exampleproject.R;

public class DrawablesActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawables);
        TextView textView = findViewById(R.id.msgText);
        textView.setBackgroundDrawable(new RainbowDrawable());
    }
}
