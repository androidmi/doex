
package com.doex.demo.database;

import android.database.Cursor;
import android.provider.BaseColumns;
import android.util.Config;

import org.json.JSONArray;
import org.json.JSONException;

public class PhoneLabelMappingModel {

    private String mNumber;
    private int mCount;
    private int mLabelNum;

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

        public static PhoneLabelMappingModel create(Cursor cursor) {
            if (cursor == null) {
                return null;
            }
            PhoneLabelMappingModel model = new PhoneLabelMappingModel();
            model.mNumber = cursor.getString(COLUMN_NUMBER);
            model.mLabelNum = cursor.getInt(COLUMN_LABEL);
            model.mCount = cursor.getInt(COLUMN_COUNT);
            return model;
        }
    }

    public static PhoneLabelMappingModel create(Cursor cursor) {
        return FromCursorFactory.create(cursor);
    }

    public static PhoneLabelMappingModel fromJson(JSONArray array) {
        if (array == null) {
            return null;
        }
        try {
            PhoneLabelMappingModel model = new PhoneLabelMappingModel();
            model.mNumber = array.getString(0);
            model.mLabelNum = array.getInt(1);
            model.mCount = array.getInt(2);
            return model;
        } catch (JSONException e) {
            if (Config.DEBUG) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private PhoneLabelMappingModel() {
    }

    public int getLabel() {
        return mLabelNum;
    }

    public String getNumber() {
        return mNumber;
    }

    public int getCount() {
        return mCount;
    }

}
