
package com.doex.demo.phone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.doex.demo.phone.PhoneCallStateListener.PhoneState;

public class PhoneCallStateReciver extends BroadcastReceiver {
    private String mPhoneNumber;
    private boolean mIsIncomming;
    private static final String TAG = "PhoneCallStateReciver";

    enum PhoneState {
        NONE, PICK_UP, HANG_UP
    }

    private Context mContext;
    private static int mPreCallState = TelephonyManager.CALL_STATE_IDLE;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            mPhoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Log.i("hello", "onReceive number " + mPhoneNumber);
            Toast.makeText(context, "on Receive " + mPhoneNumber, Toast.LENGTH_SHORT).show();
            context.sendBroadcast(new Intent("com.test"));
        }

        TelephonyManager tm = (TelephonyManager) context.getApplicationContext().getSystemService(
                Context.TELEPHONY_SERVICE);
        int state = tm.getCallState();
        Log.i("hello", "call state-----:" + state);
        Log.i("hello", "call number-----:" + mPhoneNumber);
        PhoneState phoneState = getPhoneState(state);
        Log.i("hello", "call phoneState-----:" + phoneState);
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
                    Log.i("hello", "outgoing");
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

}
