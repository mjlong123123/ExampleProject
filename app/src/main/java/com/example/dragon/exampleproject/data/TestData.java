package com.example.dragon.exampleproject.data;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;

public class TestData extends BaseObservable {
    public ObservableField<String> title = new ObservableField<>();
    public ObservableField<String> url = new ObservableField<>();
    public ObservableField<String> des = new ObservableField<>();
    public ObservableField<Boolean> visible = new ObservableField<>(true);
    public ObservableField<Integer> number = new ObservableField<>(1);
}
