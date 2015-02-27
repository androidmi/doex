
package com.doex.demo.phone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Iterator;

/**
 * 手机sim等网络状态变化监听
 * android.intent.action.SERVICE_STATE
 *
 */
public class PhoneStateReciver extends BroadcastReceiver {
    private String mPhoneNumber;
    private boolean mIsIncomming;
    private static final String TAG = "PhoneStateReciver";
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();
        Iterator<String> keyIterator = data.keySet().iterator();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next();
            Object value = data.get(key);
            if (value instanceof Boolean) {

            } else if (value instanceof Intent) {

            } else if (value instanceof String) {

            } else {
            }
            Log.i(TAG, key + ":" + value);
        }

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = tm.getSimState();
        Log.i(TAG, "sim state:" + simState);
    }

}
