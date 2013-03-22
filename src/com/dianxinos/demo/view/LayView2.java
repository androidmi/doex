
package com.dianxinos.demo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

public class LayView2 extends LinearLayout {

    private static final String TAG = "LayView2";

    public LayView2(Context context) {
        super(context);
    }

    public LayView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.i(TAG, "LayView2 constrctor");
    }

    int i = 0;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        try {
            // throw new Exception("ex");
        } catch (Exception e) {
            e.printStackTrace();
        }
        i++;
        Log.i(TAG, "LayView 2:" + i);
    }

}
