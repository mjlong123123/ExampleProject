package com.example.dragon.exampleproject.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.dragon.exampleproject.data.TestData;

public class CustomViewModel extends ViewModel {
    public LiveData<TestData> liveData = new MutableLiveData<>();


}
