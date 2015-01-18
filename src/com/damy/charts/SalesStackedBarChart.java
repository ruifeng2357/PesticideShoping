/**
 * Copyright (C) 2009 - 2013 SC 4ViewSoft SRL
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
package com.damy.charts;

import java.util.ArrayList;
import java.util.List;

import com.damy.Utils.ResolutionSet;
import com.damy.nongyao.R;
import android.view.View;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.GraphicalView;
import com.damy.common.Global;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;

import com.damy.datatypes.STAreaSaleCountInfo;
/**
 * Sales demo bar chart.
 */
public class SalesStackedBarChart extends AbstractDemoChart {
  /**
   * Returns the chart name.
   * 
   * @return the chart name
   */
  private List<STAreaSaleCountInfo> Values;
  public String getName() {
    return "Sales stacked bar chart";
  }

  /**
   * Returns the chart description.
   * 
   * @return the chart description
   */
  public boolean setValues(List<STAreaSaleCountInfo> setValues) {
	  if(setValues == null || setValues.size() == 0)
		  return false;
	  Values = setValues;
	  return true;
  }
  public String getDesc() {
    return "The monthly sales for the last 2 years (stacked bar chart)";
  }

  /**
   * Executes the chart demo.
   * 
   * @param context the context
   * @return the built intent
   */
  public GraphicalView getView(Context context, int min_value, int max_value) {
      String[] titles = new String[] { context.getResources().getString(R.string.common_remainamount), context.getResources().getString(R.string.common_salestatistic_header1) };
      List<double[]> values = new ArrayList<double[]>();

      double[] remain_count = new double[Values.size()];
      double[] sale_count = new double[Values.size()];

      for(int i=0; i<Values.size(); i++)
      {
            remain_count[i] = Values.get(i).remain_count;
            sale_count[i] = Values.get(i).sale_count;
        }
        values.add(remain_count);
        values.add(sale_count);
        int[] colors = new int[] { Color.BLUE, Color.RED };

        XYMultipleSeriesRenderer renderer = buildBarRenderer(colors);
        setChartSettings(renderer, "", "",  context.getResources().getString(R.string.common_salestatistic_unit), 0.5,
            10.5, min_value, max_value+200, Color.BLACK, Color.CYAN);

        renderer.setXLabels(10);

        for(int i=0; i<Values.size(); i++)
        {
            renderer.addXTextLabel(i+1, Values.get(i).area);
        }
        renderer.setXLabelsAngle(30);
      renderer.setXLabels(0);
        renderer.setYLabels(10);
        renderer.setXLabelsAlign(Align.LEFT);
        renderer.setYLabelsAlign(Align.LEFT);
        renderer.setPanEnabled(true, true);
        renderer.setBackgroundColor(Color.WHITE);
        renderer.setMarginsColor(Color.WHITE);

        // renderer.setZoomEnabled(false);
        renderer.setZoomRate(1.1f);
        renderer.setBarSpacing(0.5f);
        renderer.getSeriesRendererAt(0).setDisplayChartValues(true);
        renderer.getSeriesRendererAt(1).setDisplayChartValues(true);
      renderer.setLabelsTextSize(14 * ResolutionSet.fPro);
      renderer.setAxisTitleTextSize(18 * ResolutionSet.fPro);
      renderer.setChartValuesTextSize(14 * ResolutionSet.fPro);
      renderer.setShowLegend(false);
      //renderer.setShowLabels(false);
    
    return ChartFactory.getBarChartView(context, buildBarDataset(titles, values), renderer,
        Type.STACKED);
  }

}
