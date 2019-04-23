package com.example.dragon.exampleproject;

import android.annotation.TargetApi;
import android.app.Presentation;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.File;

public class WifiDisplayActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    SurfaceView surfaceView;
    DisplayManager displayManager;
    Presentation presentation;
    @TargetApi(17)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_display);
        surfaceView = findViewById(R.id.surfaceView);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
//                initPlayer(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
//                releasePlayer();
            }
        });
        surfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
                Display[] displays = displayManager.getDisplays();
                for (Display display : displays) {
                    Log.e("dragon", " display id " + display.getName());
                }
                if(displays.length >= 2) {
                    if(presentation == null) {
                        presentation = new MyPresentation(WifiDisplayActivity.this, displays[1]);
                        presentation.show();
                    }else{
                        presentation.dismiss();
                        presentation = null;
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initPlayer(SurfaceHolder holder) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(new File("mnt/sdcard/123.mp4").getPath());
            mediaPlayer.setLooping(true);
            mediaPlayer.setDisplay(holder);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void releasePlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    @TargetApi(17)
    private static class MyPresentation extends Presentation {

        MediaPlayer mediaPlayer;
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
                    initPlayer(holder);
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    releasePlayer();
                }
            });
        }

        private void initPlayer(SurfaceHolder holder) {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(new File("mnt/sdcard/123.mp4").getPath());
                mediaPlayer.setLooping(true);
                mediaPlayer.setDisplay(holder);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void releasePlayer() {
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }

    }
}
