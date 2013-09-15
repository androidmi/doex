
package com.doex.demo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.doex.demo.phone.PhoneCallStateListener;

public class DoexService extends Service {
    private static final String TAG = "DoexService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        PhoneCallStateListener mPhoneStateListener = new PhoneCallStateListener(
                getApplicationContext());
        tm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE
                | PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }
}
