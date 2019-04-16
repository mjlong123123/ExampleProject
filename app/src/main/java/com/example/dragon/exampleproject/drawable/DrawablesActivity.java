package com.example.dragon.exampleproject.drawable;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.dragon.graphics.GifDrawable;
import com.dragon.graphics.RainbowDrawable;
import com.example.dragon.exampleproject.R;

public class DrawablesActivity extends AppCompatActivity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drawables);
		initRainbowView();
		initGif();
	}

	private void initRainbowView() {
		TextView rainbowView = findViewById(R.id.rainbowDrawableView);
		RainbowDrawable.Builder builder = new RainbowDrawable.Builder();
		builder.setBackgroundColor(Color.parseColor("#60000000"));
		builder.setDuration(2000);
		builder.setRadius(50);
		builder.setStrokeWidth(10);
		builder.setGradientColorAndPosition(new int[]{Color.RED, Color.GREEN, Color.BLUE, Color.GREEN, Color.RED}, new float[]{0f, 0.2f, 0.4f, 0.6f, 0.8f, 1.0f});
		rainbowView.setBackgroundDrawable(builder.build());
	}

	private void initGif() {
		ImageView gifView = findViewById(R.id.gifView);
		GifDrawable gifDrawable = new GifDrawable(this);
		gifDrawable.start();
		gifView.setImageDrawable(gifDrawable);
		ImageView gifView1 = findViewById(R.id.gifView1);

//		File file = new File("mnt/sdcard/test.gif");
//		if(!file.exists()){
//			throw new RuntimeException("file error!!!!!");
//		}
//		Glide.with(this).asGif().load(file).into(gifView1);
	}
}
