package com.example.dragon.exampleproject;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.dragon.exampleproject.data.TestData;
import com.example.dragon.exampleproject.databinding.ActivityDataBindingBinding;
import com.example.dragon.exampleproject.presenter.TestPresenter;

public class DataBindingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDataBindingBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_data_binding);
        TestData testData = new TestData();
        testData.title.set("title");
        testData.des.set("destination");
        binding.setTestData(testData);
        binding.setPresenter(new TestPresenter());
    }
}
