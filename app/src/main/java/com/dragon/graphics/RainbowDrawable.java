package com.dragon.graphics;

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
 * @author dragon
 */
public class RainbowDrawable extends Drawable implements Runnable {
    private Paint paint;
    private Paint paintGradient;
    private Paint paintMask = new Paint();
    private Path pathS;
    private RectF rectS = new RectF();
    private RectF rectF = new RectF();
    private int degree = 0;
    private int radius = 60;
    private int strokeWidth = 6;
    private int durationOnCircle = 1000;
    private int timeStep = 33;
    private int degreeStep = 1;
    private boolean requestPlay = true;

    public RainbowDrawable() {
        init();
    }

    private void init() {
        degreeStep = (int) (timeStep / (durationOnCircle / 360.0));
        paintGradient = new Paint();
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(strokeWidth);
        paint.setAntiAlias(true);
        pathS = new Path();
        paintMask.setStyle(Paint.Style.STROKE);
        paintMask.setColor(Color.GREEN);
        paintMask.setStrokeWidth(strokeWidth);
        paintMask.setAntiAlias(true);
        paintMask.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
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
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(rectF, radius, radius, paint);
        {
            canvas.saveLayer(rectF, paint, ALL_SAVE_FLAG);
            canvas.rotate(degree, rect.width() / 2.0f, rect.height() / 2.0f);
            canvas.drawCircle(rect.width() / 2.0f, rect.height() / 2.0f, rect.width(), paintGradient);
            canvas.rotate(-degree, rect.width() / 2.0f, rect.height() / 2.0f);
            {
                canvas.saveLayer(rectF, paintMask, ALL_SAVE_FLAG);
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawPath(pathS, paint);
                canvas.restore();
            }
            canvas.restore();
        }
        if (requestPlay) {
            requestPlay = false;
            scheduleSelf(this, SystemClock.uptimeMillis() + timeStep);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        //Do nothind
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
    protected void onBoundsChange(Rect bounds) {
        int w = bounds.width();
        int h = bounds.height();
        rectS.set(strokeWidth / 2.0f, strokeWidth / 2.0f, w - (strokeWidth / 2.0f), h - (strokeWidth / 2.0f));
        pathS.addRoundRect(rectS, radius, radius, Path.Direction.CCW);
        paintGradient.setShader(new SweepGradient(w / 2.f, h / 2.f, new int[]{Color.BLUE, Color.RED, Color.GREEN, Color.RED, Color.BLUE}, null));
        rectF.set(0, 0, w, h);
    }

    @Override
    public void run() {
        scheduleSelf(this, SystemClock.uptimeMillis() + timeStep);
        degree += degreeStep;
        degree %= 360;
        invalidateSelf();
    }
}
