package com.sortlayout.dragon.pager;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Scroller;


/**
 * @author chenjiulong
 */
public class HVLPaper extends ViewGroup {

    private int slop;
    private boolean horizontal = true;
    private boolean supportLoop = true;
    private Scroller scroller;
    /**
     * Determines speed during touch scrolling
     */
    private VelocityTracker mVelocityTracker;
    private int mMinimumVelocity;
    private int mMaximumVelocity;
    private static final int MIN_FLING_VELOCITY = 400; // dips

    private View downView;

    private boolean isFirstLayout = true;

    private Adapter adapter;

    private Rect selfRect = new Rect();

    private Updater updater = new Updater();

    public HVLPaper(Context context) {
        super(context);
        init(context);
    }

    public HVLPaper(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        slop = ViewConfiguration.get(context).getScaledTouchSlop();
        final float density = context.getResources().getDisplayMetrics().density;
        mMinimumVelocity = (int) (MIN_FLING_VELOCITY * density);
        mMaximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        scroller = new Scroller(context);
        //init item container.
        removeAllViews();
        //first item container.
        FrameLayout frameLayout = new FrameLayout(getContext());
        frameLayout.setId(R.id.HVLContainerFirst);
        addView(frameLayout);
        //second item container.
        frameLayout = new FrameLayout(getContext());
        frameLayout.setId(R.id.HVLContainerSecond);
        addView(frameLayout);
        //third item container.
        frameLayout = new FrameLayout(getContext());
        frameLayout.setId(R.id.HVLContainerThird);
        addView(frameLayout);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();

        int childCount = getChildCount();
        View childView;
        for (int i = 0; i < childCount; i++) {
            childView = getChildAt(i);
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        View childView;

        if (isFirstLayout) {
            int left = getPaddingLeft();
            int top = getPaddingTop();
            if (horizontal) {
                left -= getMeasuredWidth();
            } else {
                top -= getMeasuredHeight();
            }
            LayoutParams layoutParams;
            for (int i = 0; i < childCount; i++) {
                childView = getChildAt(i);
                layoutParams = (LayoutParams) childView.getLayoutParams();
                layoutParams.left = left;
                layoutParams.top = top;
                layoutParams.right = left + childView.getMeasuredWidth();
                layoutParams.bottom = top + childView.getMeasuredHeight();
                childView.layout(layoutParams.left, layoutParams.top, layoutParams.right, layoutParams.bottom);
                if (horizontal) {
                    left += getMeasuredWidth();
                } else {
                    top += getMeasuredHeight();
                }
            }
            isFirstLayout = false;
        } else {
            LayoutParams layoutParams;
            for (int i = 0; i < childCount; i++) {
                childView = getChildAt(i);
                layoutParams = (LayoutParams) childView.getLayoutParams();

                Log.e("dragon_sort", "sortChildViewAgain index " + layoutParams);
                childView.layout(layoutParams.left, layoutParams.top, layoutParams.right, layoutParams.bottom);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        selfRect.set(0, 0, w, h);
        Log.e("dragon_change", "onSizeChanged ");
        isFirstLayout = true;
        scrollTo(0, 0);
    }

    float lastX;
    float lastY;
    int activePointId = -1;

    boolean dragMode = false;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);
        int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                resetTouchDownEvent(ev);
                downView = findLargestViewInScreen(null);
                break;
            case MotionEvent.ACTION_MOVE:
                if (activePointId < 0) {
                    break;
                }
                int index = ev.findPointerIndex(activePointId);
                float currentX = ev.getX(index);
                float currentY = ev.getY(index);
                if (Math.abs(currentX - lastX) > slop || Math.abs(currentY - lastY) > slop) {
                    dragMode = true;
                    getParent().requestDisallowInterceptTouchEvent(true);
                    lastX = currentX;
                    lastY = currentY;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                dragMode = false;
                downView = null;
                break;
        }
        return dragMode;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                resetTouchDownEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (activePointId < 0) {
                    break;
                }
                int index = event.findPointerIndex(activePointId);
                if (index < 0) {
                    break;
                }
                float currentX = event.getX(index);
                float currentY = event.getY(index);
                if (dragMode) {
                    float dx = currentX - lastX;
                    float dy = currentY - lastY;
                    moveView(dx, dy);
                    lastX = currentX;
                    lastY = currentY;
                } else if (canMove(currentX, lastX, currentY, lastY)) {
                    dragMode = true;
                    getParent().requestDisallowInterceptTouchEvent(true);
                    lastX = currentX;
                    lastY = currentY;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (dragMode) {
                    final VelocityTracker velocityTracker = mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                    int initialVelocity = horizontal ? (int) velocityTracker.getXVelocity(activePointId) : (int) velocityTracker.getYVelocity(activePointId);
                    int direction = 0;
                    if (Math.abs(initialVelocity) > mMinimumVelocity) {
                        Rect rect = new Rect();
                        Rect screenRect = new Rect(0, 0, getWidth(), getHeight());
                        if (downView != null) {
                            getChildViewRect(downView, rect);
                            direction = horizontal ? rect.centerX() - screenRect.centerX() : rect.centerY() - screenRect.centerY();
                        }
                    }
                    int[] offsets = new int[2];
                    View selectedView = null;
                    if (direction == 0) {
                        selectedView = findLargestViewInScreen(offsets);
                    } else if (direction < 0) {
                        selectedView = findNextView(this.downView, offsets);
                    } else if (direction > 0) {
                        selectedView = findPreviousView(this.downView, offsets);
                    }
                    sortChildViewAgain(selectedView);
                    scroller.startScroll(getScrollX(), getScrollY(), -offsets[0], -offsets[1]);
                    ViewCompat.postInvalidateOnAnimation(this);
                    dragMode = false;
                }
                downView = null;
                mVelocityTracker.clear();
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        if (!scroller.isFinished()) {
            if (scroller.computeScrollOffset()) {
                int currentX = scroller.getCurrX();
                int currentY = scroller.getCurrY();
                if (currentX != getScrollX() || currentY != getScrollY()) {
                    scrollTo(currentX, currentY);
                }
                // Keep on drawing until the animation has finished.
                ViewCompat.postInvalidateOnAnimation(this);
                updateStatus();
            }
        }
    }

    public void setAdapter(Adapter adp, int selectedPosition) {
        ViewGroup childView;
        LayoutParams layoutParams;
        int childCount = getChildCount();
        //clear old view;
        if (adapter != null) {
            updater.start();
            for (int i = 0; i < childCount; i++) {
                childView = (ViewGroup) getChildAt(i);
                layoutParams = (LayoutParams) childView.getLayoutParams();
                changeStatus(childView, layoutParams, LayoutParams.STATUS.INITIAL);
                childView.removeAllViews();
            }
            updater.end();
        }
        if (adp == null) {
            return;
        }
        if (selectedPosition < 0 || selectedPosition >= adp.getCount()) {
            throw new IllegalArgumentException("setAdapter selectedPosition out of bound!!!!");
        }
        adapter = adp;
        int startPosition = selectedPosition - 1;
        updater.start();
        for (int i = 0; i < childCount; i++) {
            childView = (ViewGroup) getChildAt(i);
            layoutParams = (LayoutParams) childView.getLayoutParams();
            layoutParams.position = checkPosition(startPosition + i);
            if (layoutParams.position == selectedPosition) {
                changeStatus(childView, layoutParams, LayoutParams.STATUS.ACTIVE);
            } else {
                changeStatus(childView, layoutParams, LayoutParams.STATUS.CREATED);
            }
        }
        updater.end();
    }

    private int checkPosition(int position) {
        if (adapter == null) {
            return position;
        }
        position %= adapter.getCount();
        if (position < 0) {
            position = position + adapter.getCount();
        }
        return position;
    }

    private void sortChildViewAgain(View selectedView) {
        int index = indexOfChild(selectedView);
        Log.e("dragon_sort", "sortChildViewAgain index " + index);
        int childCount = getChildCount();
        if (index < 0 || index >= childCount) {
            return;
        }
        if (index == 0) {
            View childView = getChildAt(childCount - 1);
            LayoutParams childLayoutParams = (LayoutParams) childView.getLayoutParams();
            removeViewAt(childCount - 1);
            updater.start();
            changeStatus(childView, childLayoutParams, LayoutParams.STATUS.INITIAL);
            updater.end();
            childLayoutParams.offset(horizontal ? -childCount * getMeasuredWidth() : 0, horizontal ? 0 : -childCount * getMeasuredHeight());
            childLayoutParams.backPosition(3, adapter.getCount());
            addView(childView, 0, childLayoutParams);
        } else if (index == 2) {
            View childView = getChildAt(0);
            LayoutParams childLayoutParams = (LayoutParams) childView.getLayoutParams();
            removeViewAt(0);
            updater.start();
            changeStatus(childView, childLayoutParams, LayoutParams.STATUS.INITIAL);
            updater.end();
            childLayoutParams.offset(horizontal ? childCount * getMeasuredWidth() : 0, horizontal ? 0 : childCount * getMeasuredHeight());
            childLayoutParams.forwardPosition(3, adapter.getCount());
            addView(childView, -1, childLayoutParams);
        }
        invalidate();
    }

    private void resetTouchDownEvent(MotionEvent event) {
        lastX = event.getX();
        lastY = event.getY();
        activePointId = event.getPointerId(0);
        dragMode = false;
        if (!scroller.isFinished()) {
            scroller.abortAnimation();
        }
    }

    private View findPreviousView(View view, int[] offsetScreen) {
        int index = indexOfChild(view);
        index--;
        if (index >= 0) {
            View childView = getChildAt(index);
            if (offsetScreen != null && offsetScreen.length > 1) {
                Rect rect = new Rect();
                Rect screenRect = new Rect(0, 0, getWidth(), getHeight());
                getChildViewRect(childView, rect);
                offsetScreen[0] = screenRect.centerX() - rect.centerX();
                offsetScreen[1] = screenRect.centerY() - rect.centerY();
            }
            return childView;
        }
        return view;
    }

    private View findNextView(View view, int[] offsetScreen) {
        int index = indexOfChild(view);
        index++;
        if (index < getChildCount()) {
            View childView = getChildAt(index);
            if (offsetScreen != null && offsetScreen.length > 1) {
                Rect rect = new Rect();
                Rect screenRect = new Rect(0, 0, getWidth(), getHeight());
                getChildViewRect(childView, rect);
                offsetScreen[0] = screenRect.centerX() - rect.centerX();
                offsetScreen[1] = screenRect.centerY() - rect.centerY();
            }
            return childView;
        }
        return view;
    }

    private View findLargestViewInScreen(int[] offsetScreen) {
        int count = getChildCount();
        View childView = null;
        Rect rect = new Rect();
        Rect screenRect = new Rect(0, 0, getWidth(), getHeight());
        for (int i = 0; i < count; i++) {
            childView = getChildAt(i);
            getChildViewRect(childView, rect);
            if (screenRect.contains(rect.centerX(), rect.centerY())) {
                if (offsetScreen != null && offsetScreen.length > 1) {
                    offsetScreen[0] = screenRect.centerX() - rect.centerX();
                    offsetScreen[1] = screenRect.centerY() - rect.centerY();
                }
                break;
            }
        }
        return childView;
    }

    private void getChildViewRect(View view, Rect rect) {
        rect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        rect.offset(-getScrollX(), -getScrollY());
    }

    private boolean canMove(float currentX, float lastX, float currentY, float lastY) {
        if (horizontal) {
            return Math.abs(currentX - lastX) > slop;
        } else {
            return Math.abs(currentY - lastY) > slop;
        }
    }

    private void moveView(float dx, float dy) {
        if (horizontal) {
            if (!supportLoop) {
                if (dx > 0) {
                    if (downView != null) {
                        LayoutParams layoutParams = (LayoutParams) downView.getLayoutParams();
                        if (layoutParams.position == 0) {
                            return;
                        }
                    }
                } else {
                    if (downView != null) {
                        LayoutParams layoutParams = (LayoutParams) downView.getLayoutParams();
                        if (layoutParams.position == adapter.getCount() - 1) {
                            return;
                        }
                    }
                }
            }
            scrollBy((int) -dx, 0);
        } else {
            if (!supportLoop && downView != null) {
                if (dy > 0) {
                    if (downView != null) {
                        LayoutParams layoutParams = (LayoutParams) downView.getLayoutParams();
                        if (layoutParams.position == 0) {
                            return;
                        }
                    }
                } else {
                    if (downView != null) {
                        LayoutParams layoutParams = (LayoutParams) downView.getLayoutParams();
                        if (layoutParams.position == adapter.getCount() - 1) {
                            return;
                        }
                    }
                }
            }
            scrollBy(0, (int) -dy);
        }
        updateStatus();
    }

    private void updateStatus() {
        int count = getChildCount();
        View childView;
        LayoutParams layoutParams;
        Rect rect = new Rect();

        updater.start();
        for (int i = 0; i < count; i++) {
            childView = getChildAt(i);
            layoutParams = (LayoutParams) childView.getLayoutParams();
            getChildViewRect(childView, rect);
            if (selfRect.contains(rect)) {
                changeStatus(childView, layoutParams, LayoutParams.STATUS.ACTIVE);
            } else if (rect.intersect(selfRect)) {
                changeStatus(childView, layoutParams, LayoutParams.STATUS.SHOWN);
            } else {
                changeStatus(childView, layoutParams, LayoutParams.STATUS.CREATED);
            }
        }
        updater.end();
    }

    private void changeStatus(View childView, LayoutParams layoutParams, LayoutParams.STATUS status) {
        if (layoutParams == null) {
            layoutParams = (LayoutParams) childView.getLayoutParams();
        }
        if (status.ordinal() > layoutParams.status.ordinal()) {
            switch (layoutParams.status) {
                case INITIAL:
                    if (status.ordinal() > LayoutParams.STATUS.INITIAL.ordinal()) {
                        updater.run();
                        adapter.onCreate((ViewGroup) childView, layoutParams.position);
                    }
                case CREATED:
                    if (status.ordinal() > LayoutParams.STATUS.CREATED.ordinal()) {
                        updater.run();
                        adapter.onShow((ViewGroup) childView, layoutParams.position);
                    }
                case SHOWN:
                    if (status.ordinal() > LayoutParams.STATUS.SHOWN.ordinal()) {
                        updater.run();
                        adapter.onActive((ViewGroup) childView, layoutParams.position);
                    }
                case ACTIVE:
            }
            layoutParams.status = status;
        }
        if (status.ordinal() < layoutParams.status.ordinal()) {
            switch (layoutParams.status) {
                case ACTIVE:
                    if (status.ordinal() < LayoutParams.STATUS.ACTIVE.ordinal()) {
                        updater.run();
                        adapter.onInactive((ViewGroup) childView, layoutParams.position);
                    }
                case SHOWN:
                    if (status.ordinal() < LayoutParams.STATUS.SHOWN.ordinal()) {
                        updater.run();
                        adapter.onHide((ViewGroup) childView, layoutParams.position);
                    }
                case CREATED:
                    if (status.ordinal() < LayoutParams.STATUS.CREATED.ordinal()) {
                        updater.run();
                        adapter.onDestroy((ViewGroup) childView, layoutParams.position);
                    }
                case INITIAL:
            }
            layoutParams.status = status;
        }
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {
        public int left;
        public int top;
        public int right;
        public int bottom;
        public int position;
        public STATUS status = STATUS.INITIAL;

        public enum STATUS {
            INITIAL,
            CREATED,
            SHOWN,
            ACTIVE
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("left ").append(left).append("\n");
            stringBuilder.append("top ").append(top).append("\n");
            stringBuilder.append("right ").append(right).append("\n");
            stringBuilder.append("bottom ").append(bottom).append("\n");
            stringBuilder.append("position ").append(position).append("\n");
            return stringBuilder.toString();
        }

        private boolean changeStatus(STATUS status) {
            if (status == this.status) {
                return false;
            }
            this.status = status;
            return true;
        }

        protected LayoutParams copy() {
            LayoutParams layoutParams = new LayoutParams(width, height);
            layoutParams.left = left;
            layoutParams.top = top;
            layoutParams.right = right;
            layoutParams.bottom = bottom;
            return layoutParams;
        }

        public LayoutParams offset(int dx, int dy) {
            left += dx;
            right += dx;
            top += dy;
            bottom += dy;
            return this;
        }

        public LayoutParams forwardPosition(int step, int limit) {
            position += step;
            position %= limit;
            if (position < 0) {
                position = position + limit;
            }
            return this;
        }

        public LayoutParams backPosition(int step, int limit) {
            position -= step;
            position %= limit;
            if (position < 0) {
                position = position + limit;
            }
            return this;
        }
    }

    public interface Adapter {
        void startUpdate(ViewGroup paper);

        void finishUpdate(ViewGroup paper);

        void onCreate(ViewGroup itemContainer, int position);

        void onShow(ViewGroup itemContainer, int position);

        void onActive(ViewGroup itemContainer, int position);

        void onInactive(ViewGroup itemContainer, int position);

        void onHide(ViewGroup itemContainer, int position);

        void onDestroy(ViewGroup itemContainer, int position);

        int getCount();
    }

    private class Updater implements Runnable {
        private boolean oneShotFlag = false;
        private boolean needFinishUpdate = false;

        public void start() {
            oneShotFlag = false;
            needFinishUpdate = false;
        }

        public void end() {
            if (needFinishUpdate) {
                if (adapter != null) {
                    adapter.finishUpdate(HVLPaper.this);
                }
            }
        }

        @Override
        public void run() {
            if (oneShotFlag) {
                return;
            }
            oneShotFlag = true;
            if (adapter != null) {
                adapter.startUpdate(HVLPaper.this);
                needFinishUpdate = true;
            }
        }
    }
}
