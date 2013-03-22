
package com.doex.demo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

public class GuiderActivity extends FragmentActivity {

    private static final String TAG = "MainActivity";
    FragmentManager mFManager;

    Handler mHander = new Handler(new Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            mFManager.beginTransaction()
                    .replace(android.R.id.content, new MainFragment()).commit();
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        // WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final View view = new View(this);
        view.setBackgroundDrawable(getResources().getDrawable(R.drawable.guide));
        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getWindowManager().removeView(view);

            }
        });
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        params.flags = Window.FEATURE_NO_TITLE;
        getWindowManager().addView(view, params);

        setContentView(R.layout.page_main);

        mFManager = getSupportFragmentManager();
        mHander.sendEmptyMessageDelayed(1, 1000);

    }

}
