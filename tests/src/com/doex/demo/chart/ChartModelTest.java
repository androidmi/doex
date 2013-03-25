
package com.doex.demo.chart;

import android.database.MatrixCursor;
import android.test.AndroidTestCase;

import com.doex.demo.chart.model.ChartModel;

public class ChartModelTest extends AndroidTestCase {
    public static final String[] CHART_MODEL_PROJECTION = ChartModel.FromCursorFactory
            .getProjection();
    public static final MatrixCursor DATA = new MatrixCursor(CHART_MODEL_PROJECTION);

    static double[] XDATA = {
            1, 2, 3, 4, 5, 6, 7, 8
    };

    static double[] YDATA = {
            3320, 3325, 3312, 3345, 3356, 3378, 3321, 3301
    };

    static final int SERISE = 1;

    static {
        DATA.addRow(new Object[] {
                1, XDATA[0], YDATA[0], SERISE
        });
        DATA.addRow(new Object[] {
                2, XDATA[1], YDATA[1], SERISE
        });
        DATA.addRow(new Object[] {
                3, XDATA[2], YDATA[2], SERISE
        });
        DATA.addRow(new Object[] {
                4, XDATA[3], YDATA[3], SERISE
        });
        DATA.addRow(new Object[] {
                5, XDATA[4], YDATA[4], SERISE
        });
        DATA.addRow(new Object[] {
                6, XDATA[5], YDATA[5], SERISE
        });
        DATA.addRow(new Object[] {
                7, XDATA[6], YDATA[6], SERISE
        });
        DATA.addRow(new Object[] {
                8, XDATA[7], YDATA[7], SERISE
        });
    };

    public void testChartModel() {
        assertEquals(XDATA.length, YDATA.length);
        ChartModel model = ChartModel.create(DATA);
        for (int i = 0; i < XDATA.length; i++) {
            assertEquals(XDATA[i], model.getXData()[i]);
            assertEquals(YDATA[i], model.getYData()[i]);
        }
        assertEquals(SERISE, model.getSeries());
    }

}
