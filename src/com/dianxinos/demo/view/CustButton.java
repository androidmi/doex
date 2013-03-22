
package com.dianxinos.demo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;

public class CustButton extends Button {

    public CustButton(Context context) {
        super(context);
        Log.i("CustmButton", "CustmButton1");
    }

    public CustButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Log.i("CustmButton", "CustmButton construtor");
    }

    public CustButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Log.i("CustmButton", "CustmButton co");
    }

    int i = 0;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        i++;
        Log.i("CustmButton", "CustmButton:" + i);
    }

}
