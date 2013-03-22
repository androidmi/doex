
package com.doex.demo.guide;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public class GuideView extends FrameLayout {

    public GuideView(Context context) {
        super(context);
    }

    public GuideView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GuideView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View view = new View(getContext());
        android.view.WindowManager.LayoutParams params = new android.view.WindowManager.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        view.setBackgroundColor(Color.RED);
        addView(view, params);
    }
}
