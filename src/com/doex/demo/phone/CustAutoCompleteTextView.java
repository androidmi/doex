package com.doex.demo.phone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;
import android.widget.ListPopupWindow;

@SuppressLint("NewApi")
public class CustAutoCompleteTextView extends AutoCompleteTextView {
    ListPopupWindow mPopup;

    public CustAutoCompleteTextView(Context context) {
        super(context);
    }

    public CustAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDropDownAlwaysVisible() {
    }

}
