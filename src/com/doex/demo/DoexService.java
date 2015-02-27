
package com.doex.demo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.*;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.doex.demo.phone.PhoneCallStateListener;
import com.doex.demo.sms.SmsReceiver;

public class DoexService extends Service {
    private static final String TAG = "DoexService";
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    public static final String EXTRA_SEND = "send";
    public static final String EXTRA_STATE = "state";

    private class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Intent intent = (Intent) msg.obj;
            int serviceId = msg.arg1;
            String action = intent.getAction();
            Log.i(TAG, "action:" + action+" resultCode:"+serviceId);
            if (action.equals(EXTRA_SEND)) {
                Log.i(TAG, "handleMessage");
                if (isAirplaneModeOn()) {
                    Log.i(TAG, "isAirplaneModeOn");
                    unregisterPhoneState();
                    registerPhoneState();
                }
            } else if (action.equals(EXTRA_STATE)) {
                Log.i(TAG, "ok to unregister");
                unregisterPhoneState();
            }
            SmsReceiver.finishStartingService(DoexService.this, serviceId);
        }
    }

    private boolean isAirplaneModeOn() {
        return Settings.System.getInt(getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON, 0) != 0;
    }

    private void registerPhoneState() {
        Context context = getApplicationContext();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SmsReceiver.EXTRA_START);
        context.registerReceiver(SmsReceiver.getInstance(), intentFilter);
        context.sendBroadcast(new Intent(SmsReceiver.EXTRA_START));
    }

    private void unregisterPhoneState() {
        Context context = getApplicationContext();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SmsReceiver.EXTRA_STOP);
        context.registerReceiver(SmsReceiver.getInstance(), intentFilter);
        context.sendBroadcast(new Intent(SmsReceiver.EXTRA_STOP));
        context.unregisterReceiver(SmsReceiver.getInstance());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
    }

    int mResultCode;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        HandlerThread thread = new HandlerThread(TAG, android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
        mResultCode = intent != null ? intent.getIntExtra("result", 0) : 0;
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = mResultCode;
        msg.obj = intent;
        mServiceHandler.sendMessage(msg);
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        mServiceLooper.quit();
    }
}
