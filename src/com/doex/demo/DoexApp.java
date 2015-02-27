package com.doex.demo;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.doex.demo.phone.PhoneCallStateListener;
import com.doex.demo.phone.PhoneStateReciver;

/**
 * Created by dufan on 14-10-9.
 */
public class DoexApp extends Application {
    private static final String TAG = "DoexApp";
    PhoneStateReciver reciver;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SERVICE_STATE"/*TelephonyIntents.ACTION_SERVICE_STATE_CHANGED*/);
        reciver = new PhoneStateReciver();
        registerReceiver(reciver, intentFilter);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        unregisterReceiver(reciver);
    }
}
