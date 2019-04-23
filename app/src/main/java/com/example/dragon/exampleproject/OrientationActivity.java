package com.example.dragon.exampleproject;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

public class OrientationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oritentation);
        Log.d("OrientationActivity","onCreate ");
        Spinner spinner = findViewById(R.id.spinner);
        spinner.setSelection(13);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        break;
                    case 1:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                        break;
                    case 2:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        break;
                    case 3:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                        break;
                    case 4:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                        break;
                    case 5:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
                        break;
                    case 6:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
                        break;
                    case 7:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                        break;
                    case 8:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                        break;
                    case 9:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
                        break;
                    case 10:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
                        break;
                    case 11:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
                        break;
                    case 12:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_USER);
                        break;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("OrientationActivity","onResume ");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d("OrientationActivity","onConfigurationChanged newConfig"+newConfig);
        Log.d("OrientationActivity","onConfigurationChanged getRequestedOrientation"+getRequestedOrientation());
        Log.d("OrientationActivity","onConfigurationChanged getRotation"+getWindowManager().getDefaultDisplay().getRotation());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("OrientationActivity","onPause ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("OrientationActivity","onDestroy ");
    }
}
