
package com.dianxinos.demo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

public class LayView extends LinearLayout {

    private static final String TAG = "LayView1";

    public LayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Log.i(TAG, "LayView constrctor");
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
        Log.i(TAG, "LayView 1:" + i);
    }

}
