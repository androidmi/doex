package com.doex.demo.sms;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony.Sms;
import android.test.AndroidTestCase;

public class AddTest extends AndroidTestCase {
    public static final Uri SMS_CONTENT_URI = Uri.parse("content://sms");
    public static final Uri SMS_INBOX_CONTENT_URI = Uri.withAppendedPath(
            SMS_CONTENT_URI, "inbox");
    public static final int STATUS_NONE = -1;
    public static final int STATUS_COMPLETE = 0;
    public static final int STATUS_PENDING = 32;
    public static final int STATUS_FAILED = 64;

    /**
     * The protocol of the message
     * <P>
     * Type: INTEGER
     * </P>
     */
    public static final String PROTOCOL = "protocol";
    public static final int PROTOCOL_SMS = 0;
    public static final int PROTOCOL_MMS = 1;

    public void testAdd() {
        try {
            boolean read = false;
            for (int i = 1; i < 2; i++) {
                ContentValues values = new ContentValues();
                String number = "1510101819" + i;
                values.put(Sms.ADDRESS, number);
                values.put(Sms.DATE, System.currentTimeMillis());
                values.put(Sms.READ, read ? Integer.valueOf(1) : Integer.valueOf(0));
                values.put(Sms.BODY, "转账到以下账户:" + i);
                values.put(Sms.STATUS, STATUS_NONE);
                values.put(Sms.PROTOCOL, PROTOCOL_SMS);
                boolean success = false;
                Uri uri = getContext().getContentResolver().insert(SMS_INBOX_CONTENT_URI, values);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * for check the record inserted or not, in some buggy system, the calllog or sms
     * data insert will return valid Uri but not inserted, so we use this method to check
     * 
     * @param uri
     * @return
     */
    public boolean findDataByUri(Uri uri) {
        Cursor cur = null;
        try {
            cur = getContext().getContentResolver().query(uri, null,
                    null, null, null);
            if (cur.moveToNext()) {
                return true;
            }
        } catch (Exception e) {
            // ignore
        } finally {
        }
        return false;
    }

}
