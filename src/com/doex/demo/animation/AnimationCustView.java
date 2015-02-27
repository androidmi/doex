
package com.doex.demo.animation;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.doex.demo.R;

@SuppressLint("NewApi")
public class AnimationCustView extends RelativeLayout {
    private static final String TAG = "AnimationCustView";

    private int mIcon;
    private String mWord;

    public AnimationCustView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context
                .obtainStyledAttributes(attrs, R.styleable.spam_icon);
        mIcon = a.getResourceId(R.styleable.spam_icon_res, -1);
        mWord = a.getString(R.styleable.spam_icon_word);
        a.recycle();
        inflate(context, R.layout.animation_cust_view, this);
    }

    private TextView mTextView;
    private ImageView mImageView;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTextView = (TextView) findViewById(R.id.anim);
        mImageView = (ImageView) findViewById(R.id.image);
        mImageView.setImageResource(mIcon);
        mTextView.setText(mWord);
    }

    public void setText() {

        int w = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int h = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        Log.i(TAG, "width:" + width + " height:" + height);
        float textX = mTextView.getX();
        float textY = mTextView.getY();
        int textLeft = mTextView.getLeft();
        int textRight = mTextView.getRight();
        float translationX = mTextView.getTranslationX();
        float translationY = mTextView.getTranslationY();

        Log.i(TAG, "textX:" + textX + " textY:" + textY);
        Log.i(TAG, "textLeft:" + textLeft + " textRight:" + textRight);
        Log.i(TAG, "translationX:" + translationX + " translationY:" + translationY);

        ObjectAnimator anim1 = ObjectAnimator.ofFloat(mTextView, "translationX", 100, 0);
        anim1.setDuration(500);
        ObjectAnimator anim2_textView = ObjectAnimator
                .ofFloat(mTextView, "translationX", 0f, -100f);
        anim2_textView.setDuration(200);
        ObjectAnimator anim2_imageView = ObjectAnimator.ofFloat(mImageView, "translationX", 0f,
                -100f);
        anim2_imageView.setDuration(200);

        ObjectAnimator anim3_textView = ObjectAnimator
                .ofFloat(mTextView, "translationX", -200f, 100f);
        anim3_textView.setDuration(200);

        ObjectAnimator anim3_imageView = ObjectAnimator.ofFloat(mImageView, "translationX", -200,
                100f);
        anim3_imageView.setDuration(200);

        AnimatorSet set = new AnimatorSet();
        AnimatorSet set3 = new AnimatorSet();
        set3.play(anim3_textView).with(anim3_imageView);

        AnimatorSet set2 = new AnimatorSet();
        set2.play(anim2_textView).with(anim2_imageView).before(set3);
        set.play(anim1).before(set2);
        set.start();

        textX = mTextView.getX();
        textY = mTextView.getY();

        textLeft = mTextView.getLeft();
        textRight = mTextView.getRight();
        translationX = mTextView.getTranslationX();
        translationY = mTextView.getTranslationY();

        Log.i(TAG, "after textX:" + textX + " textY:" + textY);
        Log.i(TAG, "after textLeft:" + textLeft + " textRight:" + textRight);
        Log.i(TAG, "after translationX:" + translationX + " translationY:" + translationY);
    }
}
