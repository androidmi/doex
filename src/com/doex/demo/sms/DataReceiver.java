package com.doex.demo.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.ServiceState;
import android.util.Log;

import java.util.Iterator;
import java.util.Set;

public class DataReceiver extends BroadcastReceiver {
    private static final String TAG = "DataReceiver";
    private static DataReceiver sInstance;
    private DataReceiver() {
    }

    public static DataReceiver getInstance() {
        if (sInstance == null) {
            sInstance = new DataReceiver();
        }
        return sInstance;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive");
        Bundle data = intent.getExtras();
        Iterator<String> iterator = data.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object o = data.get(key);
            if (o instanceof  Integer) {
                int value = data.getInt(key);
                Log.i(TAG, key + ":" + value);
            } else if (o instanceof Boolean) {
                boolean value = data.getBoolean(key);
                Log.i(TAG, key + ":" + value);
            } else if (o instanceof Long) {
                long value = data.getLong(key);
                Log.i(TAG, key + ":" + value);
            } else if (o instanceof String) {
                String value = data.getString(key);
                Log.i(TAG, key + ":" + value);
            } else {
                Log.i(TAG, "data:"+o);
            }
        }
        if (data != null) {
            // from ServiceState.newFromBundle
            int state = ServiceState.STATE_OUT_OF_SERVICE;
            if (data.containsKey("state")) {
                state = data.getInt("state");
                Log.i(TAG, "state--------");
            } else if (data.containsKey("voiceRegState")) {
                state = data.getInt("voiceRegState");
                Log.i(TAG, "voiceRegState--------");
            }
            Log.i(TAG, "state:" + state);
            if (state == ServiceState.STATE_IN_SERVICE) {
                Log.i(TAG, "ok");
            }
        }
    }

    public static void register(Context context) {
        IntentFilter filter = new IntentFilter("android.intent.action.SERVICE_STATE");
        context.registerReceiver(DataReceiver.getInstance(), filter);
    }

    public static void unRegister(Context context) {
        context.unregisterReceiver(DataReceiver.getInstance());
    }
}
