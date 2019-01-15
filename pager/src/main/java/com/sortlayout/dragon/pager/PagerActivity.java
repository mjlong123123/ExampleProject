package com.sortlayout.dragon.pager;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

public class PagerActivity extends FragmentActivity {

    private  HVLPaper HVLPaper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);
        HVLPaper = findViewById(R.id.paperView);
        HVLPaper.setAdapter(new FragmentAdapter(getSupportFragmentManager()),0);
    }

    public void onClickRequestLayout(View view){
        HVLPaper.setAdapter(new FragmentAdapter(getSupportFragmentManager()),5);
    }
}
