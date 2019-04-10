package com.dragon.graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Movie;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.InputStream;

/**
 * @author chenjiulong
 */
public class GifDrawable extends Drawable implements Runnable {

	private Movie movie;
	private boolean loop = false;
	private int duration = 0;
	private static final int STEP_DURATION = 2;//millisecond
	private boolean started = false;
	private long startTime = 0;

	public GifDrawable(Context context) {
		InputStream is = null;
		try {
			is = context.getAssets().open("test.gif");
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

	public void start() {
		if (started) {
			return;
		}
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
//			scheduleSelf(this, currentTime + STEP_DURATION);
			invalidateSelf();
		}
	}

	@Override
	public void run() {
		Log.d("dragon_track", "run ");
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
