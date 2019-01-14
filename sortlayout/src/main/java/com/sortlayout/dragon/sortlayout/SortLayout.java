package com.sortlayout.dragon.sortlayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;
import static android.view.View.MeasureSpec.EXACTLY;
import static com.sortlayout.dragon.sortlayout.SortLayout.TouchMode.DRAG;
import static com.sortlayout.dragon.sortlayout.SortLayout.TouchMode.NORMAL;

/**
 * @author dragon
 */
public class SortLayout extends ViewGroup {

    private static final String TAG = "SortLayout";

    private Rect rect = new Rect();
    private float lastX;
    private float lastY;

    private float startX;
    private float startY;

    private int touchSlop;

    private TouchMode touchMode = NORMAL;

    private View dragView;
    private View intersectView;
    private Bitmap dragBitmap;
    private int dragBitmapWidth;
    private int dragBitmapHeight;
    private int dragBitmapX;
    private int dragBitmapY;
    private Paint paint;
    private long lastFindIntersectTime;
    private long currentTime;

    enum TouchMode {
        NORMAL, DRAG,
    }

    private int topSpace;
    private int divideSpace;
    private int secondLineLeftSpace;
    private int secondLineRightSpace;
    private int secondLineDivideSpace;

    private Rect bitmapRect = new Rect();

    private int startAnimationDuration = 500;

    private int endAnimationDuration = 500;

    public SortLayout(Context context) {
        super(context);
        init();
    }

    public SortLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        paint = new Paint();
        topSpace = (int) TypedValue.applyDimension(COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
        divideSpace = (int) TypedValue.applyDimension(COMPLEX_UNIT_DIP, 11, getResources().getDisplayMetrics());
        secondLineLeftSpace = (int) TypedValue.applyDimension(COMPLEX_UNIT_DIP, 11, getResources().getDisplayMetrics());
        secondLineRightSpace = (int) TypedValue.applyDimension(COMPLEX_UNIT_DIP, 11, getResources().getDisplayMetrics());
        secondLineDivideSpace = (int) TypedValue.applyDimension(COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
        setWillNotDraw(false);
    }

    public void addHolder(Holder holder) {
        if (holder == null) {
            Log.d(TAG, "addHolder holder == null "); 
            return;
        }
        View view = holder.createView(this);
        if (holder.data == null) {
            throw new RuntimeException("addHolder holder data is null error!!!!");
        }
        holder.bindView(this, view, getChildCount(), holder.data);
        addView(view);
    }

    public void notifyHolderChange(Holder holder) {
        if (holder == null) {
            Log.d(TAG, "notifyHolderChange holder == null!!!!!");
            return;
        }
        int index = indexOfChild(holder.view);
        if (index < 0) {
            Log.d(TAG, "notifyHolderChange not found holder!!!! ");
            return;
        }

        if (holder.data == null) {
            throw new RuntimeException("addHolder holder data is null error!!!!");
        }

        holder.bindView(this, holder.view, index, holder.data);
    }

    public void notifyAllHolderChange() {
        int count = getChildCount();
        View view;
        Object tag;
        for (int i = 0; i < count; i++) {
            view = getChildAt(i);
            tag = view.getTag();
            if (tag == null || !(tag instanceof Holder)) {
                continue;
            }
            Holder holder = (Holder) tag;
            holder.bindView(this, holder.view, i, holder.data);
        }
    }

    public Holder findFirstEmptyHolder() {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            Object object = view.getTag();
            if (object != null && object instanceof Holder) {
                Holder imageHolder = (Holder) object;
                if (!imageHolder.canMove()) {
                    return imageHolder;
                }
            }
        }
        return null;
    }

    public List<Holder> findHolders(QueryCondition queryCondition) {
        List<Holder> list = new ArrayList<>();
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            Object object = view.getTag();
            if (object != null && object instanceof Holder) {
                Holder holder = (Holder) object;
                if (queryCondition.check(holder)) {
                    list.add(holder);
                }
            }
        }
        return list;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                startX = 0;
                startY = 0;
                int x = (int) ev.getX();
                int y = (int) ev.getY();
                dragView = findViewByPosition(x, y);
                Log.d(TAG, "onInterceptTouchEvent ACTION_DOWN dragView " + dragView);
                if (dragView != null) {
                    Object object = dragView.getTag();
                    boolean canMove = false;
                    if (object != null && object instanceof Holder) {
                        canMove = ((Holder) object).canMove();
                    }
                    Log.d(TAG, "onInterceptTouchEvent ACTION_DOWN canMove " + canMove);
                    if (canMove) {
                        Log.d(TAG, "onInterceptTouchEvent ACTION_DOWN find a view");
                        startX = x;
                        startY = y;
                    }
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (startX != 0 || startY != 0) {
                    int x = (int) ev.getX();
                    int y = (int) ev.getY();
                    if (ev.getEventTime() - ev.getDownTime() >= ViewConfiguration.getLongPressTimeout() || Math.abs(startX - x) > touchSlop || Math.abs(startY - y) > touchSlop) {
                        Log.d(TAG, "onInterceptTouchEvent ACTION_MOVE start move");
                        touchMode = DRAG;
                        requestDisallowInterceptTouchEvent(true);
                        lastX = x;
                        lastY = y;
                        buildDragBitmap(x, y);
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                resetDragBitmap();
                break;
        }

        return touchMode == DRAG;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_MOVE: {
                int x = (int) event.getX();
                int y = (int) event.getY();

                if (touchMode == DRAG && (Math.abs(x - lastX) > 5 || Math.abs(y - lastY) > 5)) {
                    Log.d(TAG, "onTouchEvent ACTION_MOVE ===========");
                    moveDragBitmap(x - lastX, y - lastY);
                    lastX = x;
                    lastY = y;
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                touchMode = NORMAL;
                resetDragBitmap();
                break;
        }

        return touchMode == DRAG;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        drawDragBitmap(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childCount = getChildCount();
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int statLeft = Build.VERSION.SDK_INT < 17 ? getPaddingLeft() : getPaddingStart();
        int starTop = getPaddingTop();
        if (childCount > 0) {
            View childView = getChildAt(0);
            LayoutParameter layoutParameter = (LayoutParameter) childView.getLayoutParams();
            int childWidth = width - getPaddingLeft() - getPaddingRight();
            childView.measure(MeasureSpec.makeMeasureSpec(childWidth, EXACTLY), MeasureSpec.makeMeasureSpec(childWidth, EXACTLY));
            layoutParameter.left = statLeft;
            layoutParameter.top = starTop;
            layoutParameter.right = layoutParameter.left + childView.getMeasuredWidth();
            layoutParameter.bottom = layoutParameter.top + childView.getMeasuredHeight();
            starTop = layoutParameter.bottom;
        }
        starTop += divideSpace;
        int secondLineChildWidth = width - getPaddingLeft() - getPaddingRight() - secondLineLeftSpace - secondLineRightSpace - (secondLineDivideSpace * 3);
        secondLineChildWidth /= 4;

        bitmapRect.set(0, 0, secondLineChildWidth, secondLineChildWidth);

        statLeft = Build.VERSION.SDK_INT >= 17 ? getPaddingStart() : getPaddingLeft();
        statLeft += secondLineLeftSpace;

        if (Build.VERSION.SDK_INT >= 17 && getLayoutDirection() == LAYOUT_DIRECTION_RTL) {
            for (int i = childCount - 1; i > 0; i--) {
                View childView = getChildAt(i);
                LayoutParameter layoutParameter = (LayoutParameter) childView.getLayoutParams();
                childView.measure(MeasureSpec.makeMeasureSpec(secondLineChildWidth, EXACTLY), MeasureSpec.makeMeasureSpec(secondLineChildWidth, EXACTLY));
                layoutParameter.left = statLeft;
                layoutParameter.top = starTop;
                layoutParameter.right = layoutParameter.left + childView.getMeasuredWidth();
                layoutParameter.bottom = layoutParameter.top + childView.getMeasuredHeight();
                statLeft = layoutParameter.right;
                statLeft += secondLineDivideSpace;
            }
        } else {
            for (int i = 1; i < childCount; i++) {
                View childView = getChildAt(i);
                LayoutParameter layoutParameter = (LayoutParameter) childView.getLayoutParams();
                childView.measure(MeasureSpec.makeMeasureSpec(secondLineChildWidth, EXACTLY), MeasureSpec.makeMeasureSpec(secondLineChildWidth, EXACTLY));
                layoutParameter.left = statLeft;
                layoutParameter.top = starTop;
                layoutParameter.right = layoutParameter.left + childView.getMeasuredWidth();
                layoutParameter.bottom = layoutParameter.top + childView.getMeasuredHeight();
                statLeft = layoutParameter.right;
                statLeft += secondLineDivideSpace;
            }
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        View childView;
        LayoutParameter layoutParameter;
        for (int i = 0; i < childCount; i++) {
            childView = getChildAt(i);
            layoutParameter = (LayoutParameter) childView.getLayoutParams();
            childView.layout(layoutParameter.left, layoutParameter.top, layoutParameter.right, layoutParameter.bottom);
        }
    }

    @Override
    public void addView(View child, LayoutParams params) {
        super.addView(child, params);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParameter(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParameter(getContext(), attrs);
    }

    public static class LayoutParameter extends MarginLayoutParams {

        public int left;
        public int right;
        public int top;
        public int bottom;

        public LayoutParameter(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParameter(int width, int height) {
            super(width, height);
        }

        public LayoutParameter(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParameter(LayoutParams source) {
            super(source);
        }
    }

    private View findViewByPosition(int x, int y) {
        int count = getChildCount();
        View childView;
        for (int index = 0; index < count; index++) {
            childView = getChildAt(index);
            computeViewLocalPosition(childView, rect);
            if (rect.contains(x, y)) {
                return childView;
            }
        }
        return null;
    }

    private void computeViewLocalPosition(View view, Rect rect) {
        if (view == null) {
            rect.setEmpty();
            return;
        }
        rect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        rect.offset((int) view.getTranslationX(), (int) view.getTranslationY());
    }

    private void buildDragBitmap(float x, float y) {
        dragBitmap = null;
        if (dragView == null) {
            Log.d(TAG, "buildDragBitmap dragView == null!!!");
            return;
        }
        Object obj = dragView.getTag();
        if (obj != null && obj instanceof Holder) {
            ((Holder) obj).beforeHideView();
        }
        dragView.destroyDrawingCache();
        dragView.setDrawingCacheEnabled(true);
        Bitmap bitmap = dragView.getDrawingCache();
        int offsetX = 0;
        int offsetY = 0;
        if (bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Matrix matrix = new Matrix();
            matrix.setScale(bitmapRect.width() * 1.0f / width, bitmapRect.height() * 1.0f / height);
            dragBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
            offsetX = (width - dragBitmap.getWidth()) / 2;
            offsetY = (height - dragBitmap.getHeight()) / 2;
        }
        if (dragBitmap == null) {
            Log.d(TAG, "buildDragBitmap dragBitmap == null!!!");
            return;
        }
        if (obj != null && obj instanceof Holder) {
            ((Holder) obj).hideView(indexOfChild(dragView));
        }
        dragBitmapWidth = dragBitmap.getWidth();
        dragBitmapHeight = dragBitmap.getHeight();
        dragBitmapX = dragView.getLeft() + offsetX;
        dragBitmapY = dragView.getTop() + offsetY;

        dragOffsetX = dragBitmapX;
        dragOffsetY = dragBitmapY;

        animationOffsetX = 0;
        animationOffsetY = 0;

        startAnimation(dragView.getLeft() + ((dragView.getWidth() - dragBitmapWidth) / 2.0f), x - (dragBitmapWidth / 2.0f), dragView.getTop() + ((dragView.getHeight() - dragBitmapHeight) / 2.0f),
                y - (dragBitmapHeight / 2.0f), dragView.getWidth() * 1.0f / dragBitmapWidth, 1.0f, null, startAnimationDuration);
    }

    private Matrix dragMatrix = new Matrix();
    private float dragScale = 1.0f;
    private float dragOffsetX = 0;
    private float dragOffsetY = 0;
    private boolean animationIsRunning = false;
    private boolean animationRequestFinished = false;
    private float animationOffsetX = 0;
    private float animationOffsetY = 0;

    public void setDragScale(float value) {
        dragScale = value;
        invalidate();
    }

    public void setDragOffsetX(float value) {
        dragOffsetX = value;
        invalidate();
    }

    public void setDragOffsetY(float value) {
        dragOffsetY = value;
        invalidate();
    }

    private void startAnimation(float startX, float endX, float startY, float endY, float startScale, float endScale, final Runnable runnable, int duration) {
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(this, "dragOffsetX", startX, endX);

        ObjectAnimator animatorY = ObjectAnimator.ofFloat(this, "dragOffsetY", startY, endY);

        ObjectAnimator animatorScale = ObjectAnimator.ofFloat(this, "dragScale", startScale, endScale);

        if (getWidth() > 0) {
            duration *= Math.sqrt(Math.pow((startX - endX), 2) + Math.pow((startY - endY), 2)) / getWidth();
        }

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.setDuration(duration);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                animationIsRunning = true;
                animationRequestFinished = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animationRequestFinished = true;
                if (runnable != null) {
                    runnable.run();
                }
            }
        });
        animatorSet.playTogether(animatorX, animatorY, animatorScale);
        animatorSet.start();
    }

    private void drawDragBitmap(Canvas canvas) {
        if (dragBitmap != null) {
            canvas.save();
            dragMatrix.reset();
            if (animationIsRunning) {
                if (animationRequestFinished) {
                    dragOffsetX += animationOffsetX;
                    dragOffsetY += animationOffsetY;
                    dragMatrix.setTranslate(dragOffsetX, dragOffsetY);
                    animationIsRunning = false;
                    animationRequestFinished = false;
                    animationOffsetX = 0;
                    animationOffsetY = 0;
                } else {
                    dragMatrix.setTranslate(dragOffsetX + animationOffsetX, dragOffsetY + animationOffsetY);
                }
            } else {
                dragMatrix.setTranslate(dragOffsetX, dragOffsetY);
            }
            dragMatrix.preScale(dragScale, dragScale, dragBitmapWidth / 2.0f, dragBitmapHeight / 2.0f);
            canvas.concat(dragMatrix);
            canvas.drawBitmap(dragBitmap, 0, 0, paint);
            canvas.restore();
        }
    }

    private void moveDragBitmap(float dx, float dy) {
        if (animationIsRunning) {
            animationOffsetX += dx;
            animationOffsetY += dy;
            return;
        }
        dragBitmapX += dx;
        dragBitmapY += dy;
        dragOffsetX += dx;
        dragOffsetY += dy;
        invalidate();
        if (dragView == null) {
            Log.d(TAG, "moveDragBitmap dragView == null");
            return;
        }
        currentTime = System.currentTimeMillis();
        if (currentTime - lastFindIntersectTime < 300) {
            Log.d(TAG, "moveDragBitmap currentTime - lastFindIntersectTime < 300");
            return;
        }
        lastFindIntersectTime = currentTime;
        View childView = findViewByPosition((int) dragOffsetX + (dragBitmapWidth / 2), (int) dragOffsetY + (dragBitmapHeight / 2));
        if (childView == null) {
            Log.d(TAG, "moveDragBitmap childView == null");
            return;
        }
        if (intersectView != null && intersectView == childView) {
            Log.d(TAG, "moveDragBitmap intersectView == childView");
            return;
        }
        Object object = childView.getTag();
        if (object != null && object instanceof Holder && !((Holder) object).canMove()) {
            Log.d(TAG, "moveDragBitmap child can not move");
            return;
        }
        intersectView = childView;

        int intersectIndex = indexOfChild(intersectView);
        int dragIndex = indexOfChild(dragView);
        if (intersectIndex < 0 || dragIndex < 0 || intersectIndex == dragIndex) {
            Log.d(TAG, "moveDragBitmap intersectIndex < 0 || dragIndex < 0 || intersectIndex == dragIndex");
            return;
        }
        removeView(dragView);
        addView(dragView, intersectIndex);
    }

    private void resetDragBitmap() {
        if (dragView == null) {
            return;
        }
        startAnimation(dragOffsetX, dragView.getLeft() + (dragView.getWidth() / 2.0f) - (dragBitmapWidth / 2.0f), dragOffsetY,
                dragView.getTop() + (dragView.getHeight() / 2.0f) - (dragBitmapHeight / 2.0f), dragScale, dragView.getWidth() * 1.0f / dragBitmapWidth, new Runnable() {
                    @Override
                    public void run() {
                        if (dragView != null) {
                            Object obj = dragView.getTag();
                            if (obj != null && obj instanceof Holder) {
                                ((Holder) obj).showView(indexOfChild(dragView));
                            }
                        }
                        dragBitmap = null;
                        dragView = null;
                        intersectView = null;
                        notifyAllHolderChange();
                        invalidate();
                    }
                }, endAnimationDuration);
    }

    public static abstract class Holder<T> {

        public View view;
        public T data;
        public Context context;

        public Holder(Context context, T data) {
            this.context = context;
            this.data = data;
        }

        public abstract View createView(View parent);

        public void bindView(View parent, View view, int position, T data) {
            this.view = view;
            this.data = data;
            view.setTag(this);
        }

        public abstract void showView(int index);

        public abstract void hideView(int index);

        public abstract boolean canMove();

        public abstract void beforeHideView();
    }

    public interface QueryCondition<T> {

        boolean check(T holder);
    }
}
