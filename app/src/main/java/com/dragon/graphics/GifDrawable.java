package com.dragon.graphics;

import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Movie;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.InputStream;


/**
 * @author dragon
 */
public class GifDrawable extends Drawable implements Runnable {
	private Movie movie;
	private boolean loop = true;
	private int duration = 0;
	private static final int STEP_DURATION = 30;//millisecond
	private boolean started = false;
	private long startTime = 0;

	public static GifDrawable create(AssetManager assetManager, String name){
		GifDrawable gifDrawable = null;
		try{
			gifDrawable = new GifDrawable(assetManager.open(name));
		}catch (Exception e){
			e.printStackTrace();
		}
		return gifDrawable;
	}

	private GifDrawable(InputStream is) {
		try {
			movie = Movie.decodeStream(is);
			duration = movie.duration();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void start(boolean loop) {
		if (started) {
			return;
		}
		this.loop = loop;
		started = true;
		startTime = SystemClock.uptimeMillis();
		scheduleSelf(this, startTime);
	}

	public void stop() {
		if (!started) {
			return;
		}
		started = false;
		unscheduleSelf(this);
	}

	@Override
	public void draw(@NonNull Canvas canvas) {
		int playTime = 0;
		long currentTime = SystemClock.uptimeMillis();
		if (startTime != 0) {
			playTime = (int) (currentTime - startTime);
			if (playTime >= duration) {
				if (loop) {
					playTime %= duration;
				} else {
					playTime = duration - 1;
					stop();
				}
			}
		}
		movie.setTime(playTime);
		movie.draw(canvas, 0, 0);
		if (started) {
			scheduleSelf(this, currentTime + STEP_DURATION);
		}
	}

	@Override
	public void run() {
		invalidateSelf();
	}

	@Override
	public int getIntrinsicHeight() {
		return movie.height();
	}

	@Override
	public int getIntrinsicWidth() {
		return movie.width();
	}

	@Override
	public void setAlpha(int alpha) {
	}

	@Override
	public void setColorFilter(@Nullable ColorFilter colorFilter) {
	}

	@Override
	public int getOpacity() {
		return PixelFormat.TRANSPARENT;
	}
}
