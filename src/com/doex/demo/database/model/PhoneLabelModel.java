
package com.doex.demo.database.model;

import android.database.Cursor;
import android.provider.BaseColumns;

import com.doex.demo.database.PhoneLabelDatabase;

public class PhoneLabelModel {

    private String mLabel;
    private String mNumber;
    private int mCount;

    public static class FromCursorFactory {
        private static final String[] PHONE_LABEL_PROJECTION = {
                BaseColumns._ID, // 0
                PhoneLabelDatabase.NUMBER, // 1
                PhoneLabelDatabase.LABEL, // 2
                PhoneLabelDatabase.COUNT, // 3
        };

        private static int COLUMN_NUMBER = 1;
        private static final int COLUMN_LABEL = 2;
        private static final int COLUMN_COUNT = 3;

        public static String[] getProjection() {
            int len = PHONE_LABEL_PROJECTION.length;
            String[] res = new String[len];
            System.arraycopy(PHONE_LABEL_PROJECTION, 0, res, 0, len);
            return res;
        }

        public static PhoneLabelModel create(Cursor cursor) {
            if (cursor == null) {
                return null;
            }
            PhoneLabelModel model = new PhoneLabelModel();
            model.mNumber = cursor.getString(COLUMN_NUMBER);
            model.mLabel = cursor.getString(COLUMN_LABEL);
            model.mCount = cursor.getInt(COLUMN_COUNT);
            return model;
        }
    }

    public static PhoneLabelModel create(Cursor cursor) {
        return FromCursorFactory.create(cursor);
    }

    private PhoneLabelModel() {
    }

    public PhoneLabelModel(String number, String label, int count) {
        this.mNumber = number;
        this.mLabel = label;
        this.mCount = count;
    }

    public String getLabel() {
        return mLabel;
    }

    public String getNumber() {
        return mNumber;
    }

    public int getCount() {
        return mCount;
    }

}
