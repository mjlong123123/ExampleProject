package com.sortlayout.dragon.sortlayout;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

/**
 * @author dragon
 */
public class SortLayout2 extends RelativeLayout {

    private static final String TAG = "SortLayout2";

    private boolean normalOrder = true;

    public SortLayout2(Context context) {
        super(context);
        init();
    }

    public SortLayout2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setChildrenDrawingOrderEnabled(true);
    }

    public void switchOrder() {
        normalOrder = !normalOrder;
        invalidate();
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        Log.e("dragon_log", "getChildDrawingOrder normalOrder " + normalOrder);
        if (normalOrder) {
            return super.getChildDrawingOrder(childCount, i);
        } else {
            return getChildCount() - 1 - i;
        }
    }
}
