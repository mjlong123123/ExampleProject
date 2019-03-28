package com.example.dragon.exampleproject.paper;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dragon.exampleproject.R;

public class TestFragment extends Fragment {

    private TextView textView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test,container,false);
        textView = view.findViewById(R.id.textView);
        return view;
    }

    @Override
    public void onResume() {
        Log.e("TestFragment","onResume title "+getArguments().getString("title"));
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.e("TestFragment","onPause title "+getArguments().getString("title"));
        super.onPause();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        Log.e("TestFragment","onHiddenChanged title "+getArguments().getString("title"));
        Log.e("TestFragment","onHiddenChanged hidden "+hidden);
        textView.setText("paper "+getArguments().getString("title"));
    }
}
