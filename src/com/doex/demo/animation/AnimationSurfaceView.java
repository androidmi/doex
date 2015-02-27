
package com.doex.demo.animation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class AnimationSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    SurfaceHolder holder;

    public AnimationSurfaceView(Context context) {
        super(context);
        holder = this.getHolder();// 获取holder
        holder.addCallback(this);
    }

    public AnimationSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        holder = this.getHolder();// 获取holder
        holder.addCallback(this);
    }

    private static final String TAG = "AnimationSurfaceView";

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated");
        new Thread(new MyThread()).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(TAG, "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed");
    }

    // 内部类的内部类
    class MyThread implements Runnable {

        @Override
        public void run() {
            Canvas canvas = holder.lockCanvas(null);// 获取画布
            Paint mPaint = new Paint();
            mPaint.setColor(Color.BLUE);
            int x = 0;
            int y = 0;
            while (true) {
                x++;
                y++;
                canvas.drawRect(new RectF(x, y, x, y), mPaint);
                holder.unlockCanvasAndPost(canvas);// 解锁画布，提交画好的图像
            }

        }

    }

}
