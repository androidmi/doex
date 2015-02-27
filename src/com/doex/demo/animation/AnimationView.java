
package com.doex.demo.animation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.drawable.StateListDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.doex.demo.R;

@SuppressLint("NewApi")
public class AnimationView extends View {
    private static final String TAG = "AnimationView";

    private Paint mPaint = new Paint();
    private String word = "99+";

    Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_antispam_call);

    private float mMoveX;
    private static final int REFRESH = 0;
    private static final int FINISH = 1;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case REFRESH:
                    if (!mIsFinish) {
                        invalidate();
                    }
                    break;
                case FINISH:
                    Log.i(TAG, "finish");
                    break;
            }
        };
    };

    public AnimationView(Context context) {
        super(context);
    }

    StateListDrawable stalistDrawable;

    public AnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundDrawable(getResources().getDrawable(
                R.drawable.btn_bkg));
    }

    /**
     * <------- true --------> false
     */

    protected void onDraw(Canvas canvas) {
        doDraw(canvas);
        if (true) {
            return;
        }
        mPaint.setTextSize(27);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);

        int height = getHeight();
        int width = getWidth();
        // Log.i(TAG, " width:" + width + " height:" + height);

        FontMetrics fontMetrics = mPaint.getFontMetrics();

        // 计算文字高度
        float fontHeight = fontMetrics.bottom - fontMetrics.top;
        // 计算文字baseline
        float textBaseY = height - (height - fontHeight) / 2 - fontMetrics.bottom;
        textBaseY = (height + fontHeight) / 2 - fontMetrics.bottom;

        float fontWidth = mPaint.measureText(word);
        canvas.drawColor(Color.WHITE);

        float textX = (width - fontWidth) / 2;
        float textY = textBaseY;
        /***** 画图 **/
        int iconWidth = icon.getWidth();
        int iconHeigth = icon.getHeight();
        Log.i(TAG, "iconWidth:" + iconWidth);
        Log.i(TAG, "iconHeigth:" + iconHeigth);
        float iconLeft = textX - iconWidth;
        float iconTop = textY;
        float leftMain = 24;
        canvas.drawText(word, textX + leftMain, textY, mPaint);
        canvas.drawBitmap(icon, textX - leftMain, (height - iconWidth) / 2, mPaint);

    };

    private static final float ACCELERATION = 0.2f;
    private static final float INCREMENT = 0.1f;
    private float mVelocity;
    private float mInitialVelocity = 0;
    private boolean mMoveLeft = true;
    private float mWordLeft;
    private boolean mIncrement = true;
    private boolean mIsFinish = false;

    /**
     * @param canvas
     */
    protected void doDraw(Canvas canvas) {
        initView();
        mVelocity = mInitialVelocity + mMoveX * ACCELERATION;
        // acceleration formula v*s*s*(1/2)
        float move = 0.5f * mVelocity * mMoveX * mMoveX;
        if (move >= (mWidth - mIconWidth)) {
            mMoveLeft = false;
            mIncrement = false;
        }

        if (mIncrement) {
            mMoveX += INCREMENT;
        } else {
            mMoveX -= INCREMENT;
        }

        mWordLeft = mWidth - move;

        if (mMoveLeft) {
            canvas.drawText(word, mWordLeft, mTextY, mPaint);
            if (mWordLeft >= mHalfWidth) {
                canvas.drawBitmap(icon, mTextX, mTop,
                        mPaint);
            } else {
                canvas.drawBitmap(icon, mWordLeft - mIconWidth, mTop, mPaint);
            }
        } else {
            if (mWordLeft >= mTextX) {
                mWordLeft = mTextX;
                mIsFinish = true;
            }
            canvas.drawText(word, mWordLeft + mLeftMain, mTextY, mPaint);
            canvas.drawBitmap(icon, mWordLeft - mLeftMain, mTop, mPaint);
        }
        // draw
        handler.sendEmptyMessage(REFRESH);
    };

    private boolean mIsViewInited = false;
    private int mWidth;
    private final static float mLeftMain = 24;
    float mTop;
    float mTextX;
    float mTextY;
    float mHalfWidth;
    int mIconWidth;

    private void initView() {
        if (mIsViewInited) {
            return;
        }
        DisplayMetrics dm = getResources().getDisplayMetrics();
        float value = dm.scaledDensity;
        mPaint.setTextSize(18 * value);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        int height = getHeight();
        mWidth = getWidth();
        mHalfWidth = mWidth / 2;

        FontMetrics fontMetrics = mPaint.getFontMetrics();

        // 计算文字高度
        float fontHeight = fontMetrics.bottom - fontMetrics.top;
        // 计算文字baseline
        float textBaseY = (height + fontHeight) / 2 - fontMetrics.bottom;

        // float fontWidth = mPaint.measureText(word);

        mIconWidth = icon.getWidth();
        mTextX = (mWidth - mIconWidth) / 2;
        mTextY = textBaseY;
        mTop = (height - mIconWidth) / 2;
        mIsViewInited = true;
    }

}
