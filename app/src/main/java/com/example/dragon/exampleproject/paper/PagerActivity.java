package com.example.dragon.exampleproject.paper;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.dragon.widget.HVLPaper;
import com.example.dragon.exampleproject.R;

public class PagerActivity extends FragmentActivity {

    private com.dragon.widget.HVLPaper HVLPaper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);
        HVLPaper = findViewById(R.id.paperView);
        HVLPaper.setAdapter(new FragmentAdapter(getSupportFragmentManager()),0);
    }
}
