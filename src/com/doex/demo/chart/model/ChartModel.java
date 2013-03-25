
package com.doex.demo.chart.model;

import android.database.Cursor;
import android.provider.BaseColumns;

import com.doex.demo.chart.provider.ChartDatabase;

public class ChartModel {

    private double[] mXData;
    private double[] mYData;

    private int mSeries;

    public static class FromCursorFactory {
        private static final String[] CHART_PROJECTION = {
                BaseColumns._ID, // 0
                ChartDatabase.XDATA,
                ChartDatabase.YDATA,
                ChartDatabase.SERIES
        };

        private static final int COLUMN_XDATA = 1;
        private static final int COLUMN_YDATA = 2;
        private static final int COLUMN_SERIES = 3;

        public static String[] getProjection() {
            int len = CHART_PROJECTION.length;
            String[] res = new String[len];
            System.arraycopy(CHART_PROJECTION, 0, res, 0, len);
            return res;
        }

        public static ChartModel create(Cursor cursor) {
            if (cursor == null || !cursor.moveToFirst()) {
                return null;
            }
            ChartModel model = new ChartModel();
            int i = 0;
            int dataLength = cursor.getCount();
            model.mXData = new double[dataLength];
            model.mYData = new double[dataLength];
            do {
                model.mXData[i] = cursor.getDouble(COLUMN_XDATA);
                model.mYData[i] = cursor.getDouble(COLUMN_YDATA);
                // the same for every loop
                model.mSeries = cursor.getInt(COLUMN_SERIES);
                i++;
            } while (cursor.moveToNext());
            return model;
        }
    }

    private ChartModel() {
    }

    public static ChartModel create(Cursor cursor) {
        return FromCursorFactory.create(cursor);
    }

    public double[] getXData() {
        return mXData;
    }

    public double[] getYData() {
        return mYData;
    }

    public int getSeries() {
        return mSeries;
    }

}
