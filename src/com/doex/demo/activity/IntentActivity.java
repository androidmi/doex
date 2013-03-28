
package com.doex.demo.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.doex.demo.R;
import com.doex.demo.utils.Logger;

public class IntentActivity extends FragmentActivity {
    private static final String TAG = "IntentActivity";

    public static final String EXTRA_NAME = "extra_name";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intent_view);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main, new FristFragment()).commit();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Logger.i(TAG, "onRestart");
        testIntents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.i(TAG, "onResume");
        testIntents();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.i(TAG, "onDestroy");
        testIntents();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.i(TAG, "onPause");
        testIntents();
    }

    private void testIntents() {
        String extraName = getIntent().getStringExtra(EXTRA_NAME);
        Logger.i(TAG, "extraName:" + extraName);
    }
}
