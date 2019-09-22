package com.example.dragon.exampleproject.presenter;

import android.util.Log;
import android.view.View;

import com.example.dragon.exampleproject.data.TestData;

public class TestPresenter {

    public void onClickTitleText(View view, TestData testData){
        Log.d("dragon_debug","onClick");
        testData.url.set("http://ac-r.static.booking.cn/images/hotel/max1024x768/987/98767654.jpg");
        testData.visible.set(!testData.visible.get());
        testData.number.set(testData.number.get()+1);
    }
}
