
package com.doex.demo.sms;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

public class SmsMessage implements Parcelable {
    public static class FromCursorFactory {

        private static final String[] SMSMESSAGE_PROJECTION = {
                BaseColumns._ID,// 0
                "body",
                "address",
                "date",
                "read",
                "type"
        };

        private static final int ID = 0;
        private static final int BODY = 1;
        private static final int ADDRESS = 2;
        private static final int DATE = 3;
        private static final int READ = 4;
        private static final int TYPE = 5;

        public static String[] getProjection() {
            int len = SMSMESSAGE_PROJECTION.length;
            String[] res = new String[len];
            System.arraycopy(SMSMESSAGE_PROJECTION, 0, res, 0, len);
            return res;
        }

        public static SmsMessage fromCursor(Cursor cursor) {
            SmsMessage sms = new SmsMessage();
            sms.mId = cursor.getInt(ID);
            sms.mBody = cursor.getString(BODY);
            sms.mAddress = cursor.getString(ADDRESS);
            sms.mDate = cursor.getLong(DATE);
            sms.mRead = cursor.getInt(READ) == 1;
            sms.mType = cursor.getInt(TYPE);
            return sms;
        }
    }

    public static SmsMessage create(Cursor cursor) {
        return FromCursorFactory.fromCursor(cursor);
    }

    public int getId() {
        return mId;
    }

    public String getBody() {
        return mBody;
    }

    public String getAddress() {
        return mAddress;
    }

    public long getDate() {
        return mDate;
    }

    public boolean isRead() {
        return mRead;
    }

    private int mId;
    private String mBody;
    private String mAddress;
    private long mDate;
    private boolean mRead;
    private int mType;

    private boolean mIsChecked;

    public boolean isChecked() {
        return mIsChecked;
    }

    public void setCheck(boolean check) {
        mIsChecked = check;
    }

    public int getType() {
        return mType;
    }

    private SmsMessage() {
    }

    private SmsMessage(Parcel in) {
        mId = in.readInt();
        mBody = in.readString();
        mAddress = in.readString();
        mDate = in.readLong();
        mRead = in.readInt() == 1;
    }

    public static final Parcelable.Creator<SmsMessage> CREATOR = new Parcelable.Creator<SmsMessage>() {
        public SmsMessage createFromParcel(Parcel in) {
            return new SmsMessage(in);
        }

        @Override
        public SmsMessage[] newArray(int size) {
            return new SmsMessage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mBody);
        dest.writeString(mAddress);
        dest.writeLong(mDate);
        dest.writeInt(mRead ? 1 : 0);
    }

    @Override
    public String toString() {
        return "id:" + mId + " body:" + mBody + " address:" + mAddress + " date:" + mDate
                + " type:" + mType;
    }
}
