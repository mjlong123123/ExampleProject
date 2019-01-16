package com.sortlayout.dragon.drawables;

import android.content.res.Resources;
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
import android.util.AttributeSet;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import static android.graphics.Canvas.ALL_SAVE_FLAG;

/**
 * @author chenjiulong
 */
public class RainbowDrawable extends Drawable implements Runnable {

    Paint paint;
    Paint paintGradient;
    Path pathL;
    Path pathS;
    Path pathS1;

    RectF rectL = new RectF();
    RectF rectS = new RectF();
    RectF rectS1 = new RectF();

    RectF saveLayerRect = new RectF();

    Paint paintMask = new Paint();
    int degree = 0;

    SweepGradient sweepGradient;

    int radius = 60;
    int strokeWidth = 6;

    int durationOnCircle = 1000;

    int timeStep = 33;

    int degreeStep = 1;

    boolean requestPlay = true;

    public RainbowDrawable() {
        init();
    }

    private void init() {
        degreeStep = (int) (timeStep / (durationOnCircle / 360.0));
        paintGradient = new Paint();
        paintGradient.setAntiAlias(true);
        paintGradient.setStyle(Paint.Style.FILL_AND_STROKE);
        paintGradient.setColor(Color.RED);
        paintGradient.setStrokeWidth(strokeWidth);
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(strokeWidth);
        paint.setAntiAlias(true);
        pathL = new Path();
        pathS = new Path();
        pathS1 = new Path();
        paintMask.setStyle(Paint.Style.STROKE);
        paintMask.setColor(Color.GREEN);
        paintMask.setStrokeWidth(strokeWidth);
        paintMask.setAntiAlias(true);
        paintMask.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
    }

    public void setDegree(int degree) {
        this.degree = degree;
        invalidateSelf();
    }

    @Override
    public void inflate(@NonNull Resources r, @NonNull XmlPullParser parser, @NonNull AttributeSet attrs) throws IOException, XmlPullParserException {
        super.inflate(r, parser, attrs);
    }

    @Override
    public void inflate(@NonNull Resources r, @NonNull XmlPullParser parser, @NonNull AttributeSet attrs, @Nullable Resources.Theme theme) throws IOException, XmlPullParserException {
        super.inflate(r, parser, attrs, theme);
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
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(saveLayerRect, radius, radius, paint);
        canvas.saveLayer(saveLayerRect, paint, ALL_SAVE_FLAG);
        canvas.rotate(degree, rect.width() / 2, rect.height() / 2);
        canvas.drawCircle(rect.width() / 2, rect.height() / 2, rect.width(), paintGradient);
        canvas.rotate(-degree, rect.width() / 2, rect.height() / 2);
        canvas.saveLayer(saveLayerRect, paintMask, ALL_SAVE_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(pathS, paint);
        canvas.restore();
        canvas.restore();
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
        rectL.set(0, 0, w, h);
        rectS1.set(strokeWidth, strokeWidth, w - (strokeWidth), h - (strokeWidth));
        rectS.set(strokeWidth / 2, strokeWidth / 2, w - (strokeWidth / 2), h - (strokeWidth / 2));
        pathL.addRoundRect(rectL, radius, radius, Path.Direction.CCW);
        pathS.addRoundRect(rectS, radius, radius, Path.Direction.CCW);
        sweepGradient = new SweepGradient(w / 2, h / 2, new int[]{Color.BLUE, Color.RED, Color.GREEN, Color.RED, Color.BLUE}, null);
        paintGradient.setShader(sweepGradient);
        saveLayerRect.set(0, 0, w, h);
    }

    @Override
    public void run() {
        Log.e("dragon_run", "run timeStep " + timeStep);
        Log.e("dragon_run", "run degreeStep " + degreeStep);
        Log.e("dragon_run", "run degree " + degree);
        scheduleSelf(this, SystemClock.uptimeMillis() + timeStep);
        degree += degreeStep;
        degree %= 360;
        invalidateSelf();
    }
}
