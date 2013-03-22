
package com.doex.demo.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.doex.demo.R;
import com.doex.demo.activity.MenuAct;

public class NotificationUtil {

    public static void showNotification(Context context, String message) {
        NotificationManager manager = (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);

        Notification notification = new Notification();
        notification.icon = R.drawable.badge_1_1_small;
        notification.tickerText = message;
        Intent intent = new Intent(context, MenuAct.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setLatestEventInfo(context, "contentTitle", message,
                pendingIntent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        int notifyId = -1;
        manager.notify(notifyId, notification);
    }
}
