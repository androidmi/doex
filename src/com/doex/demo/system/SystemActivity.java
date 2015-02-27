package com.doex.demo.system;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.doex.demo.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

public class SystemActivity extends Activity {

    public static final int OP_WRITE_SMS = 15;
    private static final String LOG_TAG = "SystemActivity";
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system);

        String pkg = Telephony.Sms.getDefaultSmsPackage(getApplicationContext());
        Log.i(LOG_TAG, "default:"+pkg);
//        setDefaultApplication(getApplicationContext());
        setDefaultSms();
    }

    private void setDefaultSms() {
        try {
            Class<?> obj = ClassLoader.getSystemClassLoader().loadClass("com.android.internal.telephony.SmsApplication");
            Method md = obj.getMethod("setDefaultApplication", String.class, Context.class);
            md.invoke(obj, "com.skysoft.animation", getApplicationContext());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the specified package as the default SMS/MMS application. The caller of this method
     * needs to have permission to set AppOps and write to secure settings.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void setDefaultApplication(Context context) {

        // Get old package name
        String oldPackageName = Settings.Secure.getString(context.getContentResolver(),
                /*Settings.Secure.SMS_DEFAULT_APPLICATION*/"sms_default_application");

        // We only make the change if the new package is valid
        PackageManager packageManager = context.getPackageManager();
        oldPackageName = Telephony.Sms.getDefaultSmsPackage(context);
        Log.i(LOG_TAG, "oldPackageName:"+oldPackageName);
        if (oldPackageName != null) {
            // Ignore OP_WRITE_SMS for the previously configured default SMS app.
            AppOpsManager appOps = (AppOpsManager)context.getSystemService(Context.APP_OPS_SERVICE);
            if (oldPackageName != null) {
                try {
                    ApplicationInfo ai = packageManager.getApplicationInfo(oldPackageName, PackageManager.GET_ACTIVITIES);
                    Method setMode = AppOpsManager.class.getMethod("setMode", Integer.class, Integer.class, String.class, Integer.class);
                    setMode.invoke(appOps, Integer.valueOf(OP_WRITE_SMS), Integer.valueOf(ai.uid), oldPackageName, Integer.valueOf(AppOpsManager.MODE_IGNORED));
                } catch (PackageManager.NameNotFoundException e) {
                    Log.w(LOG_TAG, "Old SMS package not found: " + oldPackageName);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            String newPackageName = "com.skysoft.animation";
            // Update the secure setting.
            Settings.Secure.putString(context.getContentResolver(),
                    /*Settings.Secure.SMS_DEFAULT_APPLICATION*/"sms_default_application", newPackageName);
            // Allow OP_WRITE_SMS for the newly configured default SMS app.
            Method setMode = null;
            try {
                ApplicationInfo ai = packageManager.getApplicationInfo(newPackageName, PackageManager.GET_ACTIVITIES);
                setMode = AppOpsManager.class.getMethod("setMode", Integer.class, Integer.class, String.class, Integer.class);
                setMode.invoke(appOps, Integer.valueOf(OP_WRITE_SMS), Integer.valueOf(ai.uid), oldPackageName, Integer.valueOf(AppOpsManager.MODE_ALLOWED));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            Log.i(LOG_TAG, "ok");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.system, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
