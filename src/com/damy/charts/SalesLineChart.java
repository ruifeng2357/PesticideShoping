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
import java.util.Date;
import java.util.List;

import com.damy.Utils.ResolutionSet;
import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.GraphicalView;

import com.damy.datatypes.STMonthSaleCountInfo;
import com.damy.nongyao.R;

import android.content.Context;
import android.graphics.Color;

/**
 * Project status demo chart.
 */
public class SalesLineChart extends AbstractDemoChart {
  /**
   * Returns the chart name.
   * 
   * @return the chart name
   */
	private List<STMonthSaleCountInfo> Values;
	
	public boolean setValues(List<STMonthSaleCountInfo> setValues) {
		  if(setValues == null || setValues.size() == 0)
			  return false;
		  Values = setValues;
		  return true;
	  }
  public String getName() {
    return "Project tickets status";
  }

  /**
   * Returns the chart description.
   * 
   * @return the chart description
   */
  public String getDesc() {
    return "The opened tickets and the fixed tickets (time chart)";
  }

  /**
   * Executes the chart demo.
   * 
   * @param context the context
   * @return the built intent
   */
  
public GraphicalView getView(Context context,String curYear, int min_value, int max_value) {
    String[] titles = new String[] {curYear};
    List<double[]> values = new ArrayList<double[]>();
    int length = titles.length;
    double[] sale_count = new double[Values.size()];
    for(int i=0; i<Values.size(); i++)
    {
    	sale_count[i] = Values.get(i).sale_count;
    }
    values.add(sale_count);
    
    length = values.get(0).length;
    int[] colors = new int[] { Color.BLUE};
    PointStyle[] styles = new PointStyle[] { PointStyle.POINT };
    
    XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
    
    setChartSettings(renderer, "", "", context.getResources().getString(R.string.common_salestatistic_header1), 1,
        12, min_value, max_value+50, Color.BLACK, Color.BLACK);
    renderer.setXLabels(12);
    renderer.setYLabels(10);
    renderer.setLabelsTextSize(14 * ResolutionSet.fPro);
    renderer.setAxisTitleTextSize(18 * ResolutionSet.fPro);
    
    //renderer.addYTextLabel(100, "test");
    renderer.setBackgroundColor(Color.WHITE);
    renderer.setMarginsColor(Color.WHITE);
    renderer.setPanEnabled(false, true);
    
    length = renderer.getSeriesRendererCount();
    for (int i = 0; i < length; i++) {
      SimpleSeriesRenderer seriesRenderer = renderer.getSeriesRendererAt(i);
        seriesRenderer.setDisplayChartValues(true);
        seriesRenderer.setShowLegendItem(false);
        seriesRenderer.setChartValuesTextSize(14 * ResolutionSet.fPro);
    }
    renderer.setXRoundedLabels(false);
    
    return ChartFactory.getLineChartView(context, buildBarDataset(titles, values),
        renderer);
  }
}
