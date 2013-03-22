
package com.dianxinos.demo.test;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.dianxinos.demo.R;

public class BadgeItemView extends FrameLayout {

    private ImageView mBadge;
    private TextView mBadgeName;

    public BadgeItemView(Context context) {
        this(context, null);
    }

    public BadgeItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BadgeItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        View.inflate(context, R.layout.badge_item_view, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mBadgeName = (TextView) findViewById(R.id.badge_name);
        mBadge = (ImageView) findViewById(R.id.badge);
    }

    public void setBadgeIcon(String badgeIcon) {
    }

    public void setBadgeName(String badgeName) {
        mBadgeName.setText(badgeName);
    }

}
