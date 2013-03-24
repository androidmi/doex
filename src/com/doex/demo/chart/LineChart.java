/**
 * Copyright (C) 2009, 2010 SC 4ViewSoft SRL
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.doex.demo.chart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

/**
 * Multiple temperature demo chart.
 */
public class LineChart {
    

    /**
     * Adds a new value to the series.
     * 
     * @param x the value for the X axis
     * @param y the value for the Y axis
     */
    private HashMap<Double, Double> mDataSet;
    

  /**
   * Executes the chart demo.
   * 
   * @param context the context
   * @return the built intent
   */
  public Intent execute(Context context) {

    Intent intent = ChartFactory.getLineChartIntent(context, getDataset(), getRenderer(), "title");
    return intent;
  }
  
  private XYMultipleSeriesDataset getDataset() {
      XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
      final int nr = 10;
      Random r = new Random();
      for (int i = 0; i < 1; i++) {
        XYSeries series = new XYSeries("Demo series " + (i + 1));
        for (int k = 0; k < nr; k++) {
          series.add(k, 20 + r.nextInt() % 100);
        }
        dataset.addSeries(series);
      }
      return dataset;
    }
  
  private XYMultipleSeriesDataset getDatasetFromHashMap() {
      XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
      final int nr = 10;
      Random r = new Random();
      for (int i = 0; i < 1; i++) {
          XYSeries series = new XYSeries("Demo series " + (i + 1));
          for (int k = 0; k < nr; k++) {
              series.add(k, 20 + r.nextInt() % 100);
          }
          dataset.addSeries(series);
      }
      return dataset;
  }
  
  private XYMultipleSeriesRenderer getRenderer() {
      XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
      renderer.setYTitle("ms");
      renderer.setXTitle("times");
      renderer.setBackgroundColor(Color.WHITE);
      renderer.setAxisTitleTextSize(16);
      renderer.setChartTitleTextSize(20);
      renderer.setLabelsTextSize(15);
      renderer.setLegendTextSize(15);
      renderer.setPointSize(5f);
      renderer.setMargins(new int[] {20, 30, 0, 0});
      XYSeriesRenderer r = new XYSeriesRenderer();
      r.setColor(Color.RED);
      r.setPointStyle(PointStyle.SQUARE);
      renderer.addSeriesRenderer(r);
      renderer.setAxesColor(Color.DKGRAY);
      renderer.setLabelsColor(Color.LTGRAY);
      return renderer;
    }

}
