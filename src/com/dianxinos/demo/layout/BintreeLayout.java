
package com.dianxinos.demo.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.dianxinos.demo.R;

public class BintreeLayout extends ViewGroup {
    private static final String TAG = "BintreeLayout";

    public BintreeLayout(Context context) {
        super(context);
    }

    public BintreeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BintreeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BintreeLayout);
        boolean align_left = a.getBoolean(R.styleable.BintreeLayout_Layout_align_left, false);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.i(TAG, "onLayout:" + l);
        Log.i(TAG, "onLayout:" + t);
        Log.i(TAG, "onLayout:" + r);
        Log.i(TAG, "onLayout:" + b);
        int childCount = getChildCount();
        Log.i(TAG, "child:" + childCount);
    }

}
