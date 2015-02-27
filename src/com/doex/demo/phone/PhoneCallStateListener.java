
package com.doex.demo.phone;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.doex.demo.DoexService;
import com.doex.demo.utils.DbUtils;

public class PhoneCallStateListener extends PhoneStateListener {
    private static final String TAG = "PhoneCallStateListener";

    enum PhoneState {
        NONE, PICK_UP, HANG_UP
    }

    private Context mContext;
    private int mPreCallState = TelephonyManager.CALL_STATE_IDLE;

    private static PhoneStateListener sInstance;

    private PhoneCallStateListener(Context context) {
        mContext = context;
    }

    public static PhoneStateListener getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PhoneCallStateListener(context);
        }
        return sInstance;
    }

    private boolean mIncoming = false;
    private long mStartRingTime;
    private static final long RINGOCNE_TIME = 1000 * 4;
    private String mIncomeNumber;

    private String mSinglStrenger = "";
    private Handler hander = new Handler() {
        public void handleMessage(android.os.Message msg) {
            // Toast.makeText(mContext, mSinglStrenger,
            // Toast.LENGTH_LONG).show();
        };
    };

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        Toast.makeText(mContext, "listener " + incomingNumber, Toast.LENGTH_SHORT).show();
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                Log.i(TAG, "CALL_STATE_IDLE");
                if (mIncoming) {
                    long ringDuration = System.currentTimeMillis() - mStartRingTime;
                    if (ringDuration < RINGOCNE_TIME) {
                        Log.i(TAG, "ringonce:" + ringDuration);
                        // TODO deal ringonce call
                        Toast.makeText(mContext, "is ringonce call", Toast.LENGTH_LONG).show();
                    }
                    mStartRingTime = 0;
                }
                mIncoming = false;
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                Log.i(TAG, "CALL_STATE_RINGING");
                Log.i("test", "send broadcast");
                mContext.sendBroadcast(new Intent("com.test"));
                mIncoming = true;
                mStartRingTime = System.currentTimeMillis();
                mIncomeNumber = incomingNumber;
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                Log.i(TAG, "CALL_STATE_OFFHOOK");
                break;
        }

        PhoneState phoneState = getPhoneState(state);
        vibrate(phoneState);
        handlePhoneState(phoneState);
    }

    @Override
    public void onServiceStateChanged(ServiceState serviceState) {
        Log.i(TAG, "onServiceStateChanged");
        if (serviceState.getState() == ServiceState.STATE_IN_SERVICE) {
            Intent ser = new Intent(mContext, DoexService.class);
            ser.setAction(DoexService.EXTRA_STATE);
            mContext.startService(ser);
            Log.i(TAG, "state ok");
        }
    }

    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        mSinglStrenger = "";
        boolean isGsm = signalStrength.isGsm();
        int cdmaDbm = signalStrength.getCdmaDbm();
        int gsmSignalStrength = signalStrength.getGsmSignalStrength();
        int cdmaEcio = signalStrength.getCdmaEcio();
        mSinglStrenger += "isGsm:" + isGsm;
        mSinglStrenger += "\n";
        mSinglStrenger += "cdmaDbm:" + cdmaDbm;
        mSinglStrenger += "\n";
        mSinglStrenger += "gsmSignalStrength:" + gsmSignalStrength;
        mSinglStrenger += "\n";
        mSinglStrenger += "cdmaEcio:" + cdmaEcio;
        hander.sendEmptyMessage(0);
        int level = RadioMonitorAnalysis.getRadioLevel(signalStrength);
        Log.i(TAG, mSinglStrenger);
        Log.i(TAG, "level:" + level);
    }

    private void handlePhoneState(PhoneState phoneState) {
        switch (phoneState) {
            case HANG_UP:
                Log.i(TAG, "hang up");
                boolean isMissCall = DbUtils.isMissedCall(mContext, mIncomeNumber);
                Toast.makeText(mContext, "isMisscall:" + isMissCall, Toast.LENGTH_LONG).show();
                Log.i(TAG, "isMissCall:" + isMissCall);
                break;
            case PICK_UP:
                // call in condition
                break;
        }
    }

    PhoneState getPhoneState(int state) {
        PhoneState res = PhoneState.NONE;
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                if (mPreCallState != TelephonyManager.CALL_STATE_IDLE) {
                    res = PhoneState.HANG_UP;
                }
                mPreCallState = state;
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                if (mPreCallState == TelephonyManager.CALL_STATE_RINGING) {
                    res = PhoneState.PICK_UP;
                } else if (mPreCallState == TelephonyManager.CALL_STATE_IDLE) {

                }
                mPreCallState = state;
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                mPreCallState = state;
                break;
            default:
                // ignore other state
        }
        return res;
    }

    private void vibrate(PhoneState type) {
    }
}
