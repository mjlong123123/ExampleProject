package com.example.dragon.exampleproject;

import android.annotation.TargetApi;
import android.app.Presentation;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.display.DisplayManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

@TargetApi(17)
public class Camera2WifiDisplayActivity extends AppCompatActivity {

    DisplayManager displayManager;
    MyPresentation myPresentation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2_wifi_display);
        displayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
        TextView textView = findViewById(R.id.textView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myPresentation == null) {
                    myPresentation = new MyPresentation(Camera2WifiDisplayActivity.this, displayManager.getDisplays()[1]);
                    myPresentation.show();
                } else {
                    myPresentation.dismiss();
                    myPresentation = null;
                }
            }
        });
    }


    @TargetApi(17)
    private static class MyPresentation extends Presentation {
        Camera camera;
        SurfaceView surfaceView;

        public MyPresentation(Context outerContext, Display display) {
            super(outerContext, display);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_wifi_display);
            surfaceView = findViewById(R.id.surfaceView);
            surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {
                        camera = Camera.open();
                        camera.setPreviewDisplay(holder);
                        camera.startPreview();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    camera.stopPreview();
                    camera.release();
                    camera = null;
                }
            });
        }
    }
}
