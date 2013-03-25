
package com.doex.demo.chart;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.doex.demo.R;
import com.doex.demo.chart.model.ChartModel;
import com.doex.demo.chart.provider.ChartDatabase;
import com.doex.demo.database.PhoneLabelDatabase;
import com.doex.demo.database.model.PhoneLabelDataModel;
import com.doex.demo.utils.FileUtils;
import com.doex.demo.utils.Logger;

import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.json.JSONObject;

public class ChartActivity extends Activity implements OnClickListener {

    private String TAG = "ChartActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_view);
        findViewById(R.id.line_chart).setOnClickListener(this);
        findViewById(R.id.multiple_chart).setOnClickListener(this);
        findViewById(R.id.cust_chart).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.line_chart:
                double[] xData = {
                        1, 2, 3, 4, 5, 6, 7, 8
                };
                double[] yData = {
                        1023, 1025, 1030, 1023, 1028, 1002, 1009, 1034
                };
                LineChart lineChart = new LineChart(getDataset(xData, yData, "数据库测试"));

                startActivity(lineChart.execute(this));
                break;
            case R.id.multiple_chart:
                ChartModel model1 = ChartDatabase.getInstance(this).getChartModelBySeries(1);
                ChartModel model2 = ChartDatabase.getInstance(this).getChartModelBySeries(2);

                startActivity(new MultipleTemperatureChart().execute(this, model1.getYData(),
                        model2.getYData()));
                break;
            case R.id.cust_chart:
                new dataLoader(this).execute();
                break;
        }
    }

    private XYMultipleSeriesDataset getDataset(double[] xData, double[] yData, String label) {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        if (xData.length != yData.length) {
            return null;
        }
        XYSeries series = new XYSeries(label);
        int length = xData.length;
        for (int i = 0; i < length; i++) {
            series.add(xData[i], yData[i]);
        }
        dataset.addSeries(series);
        return dataset;
    }

    class dataLoader extends AsyncTask<Void, Void, Boolean> {
        Context mContext;

        public dataLoader(Context context) {
            mContext = context;
            ChartDatabase.getInstance(mContext).dropAllData();
        }

        private int insertSerise = 1;
        private int loadSerise = 2;

        @Override
        protected Boolean doInBackground(Void... params) {
            Logger.i(TAG, "start do in background");
            try {
                for (int i = 0; i < 40; i++) {
                    insert(i);
                    load(i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        private void insert(int i) throws Exception {
            JSONObject obj = FileUtils.getAssetFileToJSONObject(mContext,
                    "json_90000.txt");
            PhoneLabelDataModel model = PhoneLabelDataModel.fromJson(obj);

            long insertStart = System.currentTimeMillis();
            PhoneLabelDatabase.getInstance(mContext).insert(model.getData());
            long insertDuration = System.currentTimeMillis() - insertStart;
            Logger.i(TAG, "insert:" + insertDuration);
            ContentValues insertValues = new ContentValues();
            insertValues.put(ChartDatabase.SERIES, insertSerise);
            insertValues.put(ChartDatabase.XDATA, i);
            insertValues.put(ChartDatabase.YDATA, insertDuration);
            ChartDatabase.getInstance(mContext).insert(insertValues);
        }

        private void load(int i) {
            // load data time
            long loadStart = System.currentTimeMillis();
            PhoneLabelDatabase.getInstance(mContext).getMarkedLableByNumber(
                    "1800000");
            long loadDuration = System.currentTimeMillis() - loadStart;
            Logger.i(TAG, "load:" + loadDuration);
            ContentValues loadValues = new ContentValues();
            loadValues.put(ChartDatabase.SERIES, loadSerise);
            loadValues.put(ChartDatabase.XDATA, i);
            loadValues.put(ChartDatabase.YDATA, loadDuration);
            ChartDatabase.getInstance(mContext).insert(loadValues);

        }

        @Override
        protected void onPostExecute(Boolean result) {

            ChartModel model = ChartDatabase.getInstance(mContext)
                    .getChartModelBySeries(insertSerise);
            double[] xData = model.getXData();
            double[] yData = model.getYData();

            LineChart lineChart = new LineChart(getDataset(xData, yData, "数据库插入测试"));

            startActivity(lineChart.execute(mContext));

        }
    }

}
