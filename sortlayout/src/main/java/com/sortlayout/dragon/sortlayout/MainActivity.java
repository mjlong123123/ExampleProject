package com.sortlayout.dragon.sortlayout;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {

    private SortLayout sortLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sortLayout = findViewById(R.id.sortLayout);
        sortLayout.addHolder(new MyHolder(this,new Data("11")), 0);
        sortLayout.addHolder(new MyHolder(this,new Data("2222")), 1);
        sortLayout.addHolder(new MyHolder(this,new Data("333")), 2);
    }

    private static class Data{
        public String title;
        public Data(String title){
            this.title = title;
        }
    }

    private class MyHolder extends SortLayout.Holder<Data> implements View.OnClickListener {
        private ImageView imageView;
        private TextView textView;
        private MyHolder(Context context,Data data){
            super(context,data);
        }
        @Override
        public View createView(View parent) {
            View root =  LayoutInflater.from(context).inflate(R.layout.image_item_layout,null,false);
            imageView = root.findViewById(R.id.imageView);
            textView = root.findViewById(R.id.textView);
            root.setOnClickListener(this);
            return root;
        }

        @Override
        public void onClick(View v) {

        }

        @Override
        public void bindView(View parent, View view, int position, Data data) {
            super.bindView(parent, view, position, data);
            TextView textView= (TextView) view;
            textView.setText(data.title);
            textView.setOnClickListener(this);
//            textView.setText(data.title);
//            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
//            textView.setTextColor(Color.BLUE);
        }

        @Override
        public void showView(int index) {

        }

        @Override
        public void hideView(int index) {

        }

        @Override
        public boolean canMove() {
            return true;
        }

        @Override
        public void beforeHideView() {

        }
    }
}
