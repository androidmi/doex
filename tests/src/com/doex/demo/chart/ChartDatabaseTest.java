
package com.doex.demo.chart;

import android.content.ContentValues;
import android.test.AndroidTestCase;

import com.doex.demo.chart.model.ChartModel;
import com.doex.demo.chart.provider.ChartDatabase;

public class ChartDatabaseTest extends AndroidTestCase {

    static double[] XDATA = {
            1, 2, 3, 4, 5, 6, 7, 8
    };

    static double[] YDATA = {
            3320, 3325, 3312, 3345, 3356, 3378, 3321, 3301
    };

    static final int SERISE_1 = 1;
    static final int SERISE_2 = 2;

    ChartDatabase mDbInstance;

    @Override
    protected void setUp() throws Exception {
        assertEquals(XDATA.length, YDATA.length);
        mDbInstance = ChartDatabase.getInstance(getContext());
    }

    @Override
    protected void tearDown() throws Exception {
        mDbInstance.dropAllData();
    }

    public void testGetChartModelBySeries() {
        mDbInstance.dropAllData();
        // add serise 1
        for (int i = 0; i < XDATA.length; i++) {
            ContentValues values = new ContentValues();
            values.put(ChartDatabase.SERIES, SERISE_1);
            values.put(ChartDatabase.XDATA, XDATA[i]);
            values.put(ChartDatabase.YDATA, YDATA[i]);
            mDbInstance.insert(values);
        }
        // add serise 2
        for (int i = 0; i < XDATA.length; i++) {
            ContentValues values = new ContentValues();
            values.put(ChartDatabase.SERIES, SERISE_2);
            values.put(ChartDatabase.XDATA, XDATA[i]);
            values.put(ChartDatabase.YDATA, YDATA[i]);
            mDbInstance.insert(values);
        }

        ChartModel model = mDbInstance.getChartModelBySeries(SERISE_1);
        assertEquals(XDATA.length, model.getXData().length);
        assertEquals(YDATA.length, model.getYData().length);
        assertEquals(SERISE_1, model.getSeries());

        model = mDbInstance.getChartModelBySeries(SERISE_2);
        assertEquals(XDATA.length, model.getXData().length);
        assertEquals(YDATA.length, model.getYData().length);
        assertEquals(SERISE_2, model.getSeries());

    }

}
