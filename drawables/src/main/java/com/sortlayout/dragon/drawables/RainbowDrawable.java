package com.sortlayout.dragon.drawables;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static android.graphics.Canvas.ALL_SAVE_FLAG;

/**
 * @author chenjiulong
 */
public class RainbowDrawable extends Drawable implements Runnable {

	private Paint paint = new Paint();
	private Paint paintMask = new Paint();
	private Paint paintGradient = new Paint();
	private Path rainbowPath = new Path();
	private Path bgPath = new Path();
	private RectF drawableRect = new RectF();
	private int degree = 0;

	private final int timeStep = 33;
	private int degreeStep = 1;
	private boolean requestPlay = true;

	/**
	 * user can change.
	 */
	private int radius = 60;
	private int strokeWidth = 20;
	private int durationOnCircle = 2000;

	public RainbowDrawable() {
		init();
	}

	public RainbowDrawable(int durationOnCircle, int strokeWidth, int radius) {
		this.durationOnCircle = durationOnCircle;
		this.strokeWidth = strokeWidth;
		this.radius = radius;
		init();
	}

	private void init() {
		paintGradient.setStyle(Paint.Style.FILL_AND_STROKE);
		paintMask.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(strokeWidth);
		paint.setAntiAlias(true);
	}

	@Override
	public boolean setVisible(boolean visible, boolean restart) {
		boolean changed = super.setVisible(visible, restart);
		if (changed) {
			if (visible) {
				scheduleSelf(this, SystemClock.uptimeMillis() + timeStep);
			} else {
				unscheduleSelf(this);
			}
		}
		return changed;
	}

	@Override
	public void draw(@NonNull Canvas canvas) {
		Rect rect = getBounds();
		paint.setColor(Color.parseColor("#90000000"));
		paint.setStyle(Paint.Style.FILL);
		canvas.drawPath(bgPath, paint);
		canvas.saveLayer(drawableRect, paint, ALL_SAVE_FLAG);
		canvas.rotate(degree, rect.width() / 2, rect.height() / 2);
		canvas.drawCircle(rect.width() / 2, rect.height() / 2, rect.width(), paintGradient);
		canvas.rotate(-degree, rect.width() / 2, rect.height() / 2);
		canvas.saveLayer(drawableRect, paintMask, ALL_SAVE_FLAG);
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawPath(rainbowPath, paint);
		canvas.restore();
		canvas.restore();
		if (requestPlay) {
			requestPlay = false;
			scheduleSelf(this, SystemClock.uptimeMillis() + timeStep);
		}
	}

	@Override
	protected void onBoundsChange(Rect bounds) {
		int w = bounds.width();
		int h = bounds.height();
		int offset = strokeWidth / 2;
		degreeStep = (int) (timeStep / (durationOnCircle / 360.0));
		drawableRect.set(0, 0, w, h);
		RectF rectF = new RectF(drawableRect.left + offset, drawableRect.top + offset, drawableRect.right - offset, drawableRect.bottom - offset);
		rainbowPath.reset();
		rainbowPath.addRoundRect(rectF, radius, radius, Path.Direction.CCW);
		bgPath.reset();
		RectF rectF2 = new RectF(drawableRect.left + strokeWidth, drawableRect.top + strokeWidth, drawableRect.right - strokeWidth, drawableRect.bottom - strokeWidth);
		bgPath.addRoundRect(rectF2, radius - offset, radius - offset, Path.Direction.CCW);
		paintGradient.setShader(new SweepGradient(w / 2, h / 2, new int[]{Color.BLUE, Color.RED, Color.GREEN, Color.RED, Color.BLUE}, null));
	}

	@Override
	public void setAlpha(int alpha) {
		//Do nothing
	}

	@Override
	public void setColorFilter(@Nullable ColorFilter colorFilter) {
		//Do nothing
	}

	@Override
	public int getOpacity() {
		return PixelFormat.TRANSPARENT;
	}


	@Override
	public void run() {
		degree += degreeStep;
		degree %= 360;
		invalidateSelf();
		scheduleSelf(this, SystemClock.uptimeMillis() + timeStep);
	}
}
