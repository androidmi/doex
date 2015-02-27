
package com.doex.demo.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Intents.Insert;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.List;

@SuppressWarnings("static-access")
public class IntentUtils {
    public static boolean isActivityAvailable(Context cxt, Intent intent) {
        List<ResolveInfo> list = cxt.getPackageManager().queryIntentActivities(intent, 0);
        return list != null && list.size() > 0;
    }

    public static boolean hasLauncherEntry(Context cxt, String pkgName) {
        return cxt.getPackageManager().getLaunchIntentForPackage(pkgName) != null;
    }

    public static List<ResolveInfo> getActivityInfo(Context cxt, Intent intent) {
        return cxt.getPackageManager().queryIntentActivities(intent, 0);
    }

    public static void sendMessage(Context context, String number) {
        if (!TextUtils.isEmpty(number)) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null));
                intent.putExtra("address", number);
                intent.setType("vnd.android-dir/mms-sms");
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void call(Context context, String number) {
        try {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.fromParts("tel", number, null));
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addNewContact(Context context, String number) {
        try {
            Intent intent = new Intent(Intent.ACTION_INSERT);
            intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Insert.PHONE, number);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addToExistContact(Context context, String number) {
        try {
            Intent intent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
            intent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
            intent.putExtra(Insert.PHONE, number);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
