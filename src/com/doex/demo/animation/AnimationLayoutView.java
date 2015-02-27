
package com.doex.demo.animation;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.doex.demo.R;

public class AnimationLayoutView extends RelativeLayout {
    private static final String TAG = "AnimationCustView";

    private int mIcon;
    private String mWord;

    public AnimationLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context
                .obtainStyledAttributes(attrs, R.styleable.spam_icon);
        mIcon = a.getResourceId(R.styleable.spam_icon_res, -1);
        mWord = a.getString(R.styleable.spam_icon_word);
        a.recycle();
        inflate(context, R.layout.animation_layout_view, this);
    }

    private TextView mTextView;
    private ImageView mImageView;
    private View mMainView;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTextView = (TextView) findViewById(R.id.anim);
        mImageView = (ImageView) findViewById(R.id.image);
        mMainView = (View) findViewById(R.id.main);
        mImageView.setImageResource(mIcon);
        mTextView.setText(mWord);
    }

    public void setText(final String text) {
        float textX = mTextView.getRight();
        float mainX = mImageView.getLeft();
        float paddingLeft = mMainView.getRight() - mainX;
        Log.i(TAG, "textX:" + textX + " mainX:" + mainX);
        Log.i(TAG, "paddingLeft:" + paddingLeft);

        final TranslateAnimation anm = new TranslateAnimation(textX, 0, 0, 0);
        anm.setDuration(200);
        mTextView.startAnimation(anm);
        mTextView.setVisibility(View.VISIBLE);
        final TranslateAnimation anm2 = new TranslateAnimation(-mainX, 0, 0, 0);
        anm2.setDuration(1000);
        anm2.setInterpolator(new DecelerateInterpolator());
        anm.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                mTextView.setText(text);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mMainView.startAnimation(anm2);
            }
        });
    }
}
