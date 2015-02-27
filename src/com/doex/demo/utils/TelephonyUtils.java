
package com.doex.demo.utils;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

public class TelephonyUtils {
    private static final boolean DEBUG = true;
    private static final String TAG = "TelephonyUtils";

    private static final String PACKAGE_SETTINGS = "com.android.settings";
    private static final String ACTIVITY_APPSCHECK = "com.android.settings.applications.AppsCheck";
    private static final String ACTIVITY_APPSCHECK2 = "com.android.settings.applications.AppsCheckReadPermission";

    public static final int OPER_CHINAMOBILE = 0;
    public static final int OPER_CHINAUNICOM = 1;
    public static final int OPER_CHINATELECOM = 2;
    public static final int OPER_OTHER = 3;
    public static final int OPER_NONE = -1;

    private static boolean sIsInit = false;
    private static boolean sHasMobile = false;

    private static ITelephony getITelephony(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        Class<TelephonyManager> c = TelephonyManager.class;
        Method m = null;
        try {
            m = c.getDeclaredMethod("getITelephony", (Class[]) null);
            m.setAccessible(true);
            return (ITelephony) m.invoke(tm, (Object[]) null);
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static boolean endCall(Context context) {
        try {
            Log.i(TAG, "endCall");
            return getITelephony(context).endCall();
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
