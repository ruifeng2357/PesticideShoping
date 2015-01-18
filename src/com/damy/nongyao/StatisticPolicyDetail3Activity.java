package com.damy.nongyao;

import java.util.ArrayList;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
/*import org.achartengine.chartdemo.demo.R;
import org.achartengine.chartdemo.demo.chart.PieChartBuilder;*/
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.damy.backend.LoadResponseThread;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.Utils.AutoSizeEditText;
import com.damy.Utils.ResolutionSet;
import com.damy.charts.SalesStackedBarChart;
import com.damy.common.Global;
import com.damy.datatypes.STAreaSaleCountInfo;
import com.damy.backend.*;
import com.damy.datatypes.STTypeSaleCountInfo;

public class StatisticPolicyDetail3Activity extends BaseActivity {
	
	public static String 		STATISTICPOLICY_CURYEAR = "STATISTICPOLICY_CURYEAR";
	private String				m_curYear;
	
	/** Colors to be used for the pie slices. */
	private static int[] COLORS = new int[] { Color.GREEN, Color.BLUE, Color.MAGENTA, Color.CYAN };
	/** The main series that will include all the data. */
	private CategorySeries mSeries = new CategorySeries("");
	/** The main renderer for the main dataset. */
	private DefaultRenderer mRenderer = new DefaultRenderer();
	/** The chart view that displays the data. */
	private GraphicalView mChartView;
	
	private ArrayList<STTypeSaleCountInfo> circleGraphList = new ArrayList<STTypeSaleCountInfo>();
	
	@Override
	protected void onRestoreInstanceState(Bundle savedState) {
	    super.onRestoreInstanceState(savedState);
	    mSeries = (CategorySeries) savedState.getSerializable("current_series");
	    mRenderer = (DefaultRenderer) savedState.getSerializable("current_renderer");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    outState.putSerializable("current_series", mSeries);
	    outState.putSerializable("current_renderer", mRenderer);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistic_policy_detail3);
		
		ResolutionSet._instance.iterateChild(findViewById(R.id.fl_statisticpolicy_detail3));
		
		m_curYear = getIntent().getStringExtra(STATISTICPOLICY_CURYEAR);
		
		initControls();
		new LoadResponseThread(StatisticPolicyDetail3Activity.this).start();
	}
	
	private void initControls()
	{
		FrameLayout fl_backbtn = (FrameLayout)findViewById(R.id.fl_statistic_policydetail_backbtn);
		FrameLayout fl_homebtn = (FrameLayout)findViewById(R.id.fl_statistic_policydetail_homebtn);
		
		fl_backbtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickBack();
        	}
        });
		/*
		fl_homebtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickHome();
        	}
        });
		*/
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	onClickBack();
        	return true;
        }
        return false;
    }
	
	private void onClickBack()
	{
		Intent policy_activity = new Intent(this, StatisticPolicyActivity.class);
		startActivity(policy_activity);
		finish();
	}
	
	private void onClickHome()
	{
		Intent login_activity = new Intent(this, LoginActivity.class);
		startActivity(login_activity);
		finish();
	}
	
	public void refreshUI() {
		super.refreshUI();
		
		for(int i=0; i<circleGraphList.size(); i++)
		{
			mSeries.add(circleGraphList.get(i).type, circleGraphList.get(i).sale_count);
	        SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
	        renderer.setColor(COLORS[(mSeries.getItemCount() - 1) % COLORS.length]);
	        mRenderer.addSeriesRenderer(renderer);
		}
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.circlechart);
	      mChartView = ChartFactory.getPieChartView(this, mSeries, mRenderer);
	      mRenderer.setClickEnabled(true);
	      mRenderer.setLabelsColor(Color.BLACK);
	      mRenderer.setLabelsTextSize(20);
	      mRenderer.setMargins(new int[]{100,100,100,100});
	      mRenderer.setScale((float)0.7);
	      mRenderer.setPanEnabled(false);
	      mChartView.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	          SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();
	          if (seriesSelection == null) {
	            /*Toast.makeText(StatisticPolicyDetail3Activity.this, "No chart element selected", Toast.LENGTH_SHORT)
	                .show();*/
	          } else {
	            for (int i = 0; i < mSeries.getItemCount(); i++) {
	              mRenderer.getSeriesRendererAt(i).setHighlighted(i == seriesSelection.getPointIndex());
	            }
	            mChartView.repaint();
	            Toast.makeText(
	            		StatisticPolicyDetail3Activity.this,
	                "[" + circleGraphList.get(seriesSelection.getPointIndex()).type + "]"+ ":"
	                    + String.valueOf((int)seriesSelection.getValue()) , Toast.LENGTH_LONG).show();
	          }
	        }
	      });
	      
	      layout.addView(mChartView);
	}
	
	public void getResponseJSON() {
		try {
			m_nResponse = ResponseRet.RET_SUCCESS;
			
			String strRequest = HttpConnUsingJSON.REQ_GETPIEGRAPH;
			strRequest += "?Year=" + m_curYear;

			JSONObject response = m_HttpConnUsingJSON.getGetJSONObject(strRequest);
			if (response == null) {
				m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
				return;
			}
			m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
            if (m_nResponse == ResponseRet.RET_SUCCESS) {
            	
            	JSONObject dataObject = response.getJSONObject(ResponseData.RESPONSE_DATA);
            	int count = dataObject.getInt("count");
            	JSONArray dataList = dataObject.getJSONArray("data");
				
				for (int i = 0; i < count; i++)
				{
					STTypeSaleCountInfo itemInfo = new STTypeSaleCountInfo();
					JSONObject tmpObj = dataList.getJSONObject(i);
					
					itemInfo.type = tmpObj.getString("nongyao_name");
	            	itemInfo.sale_count = tmpObj.getInt("sale_count");
	            						
					circleGraphList.add(itemInfo);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			m_nResponse = ResponseRet.RET_JSON_EXCEPTION;
		}
	}
	
	
	public JSONObject makeRequestJSON() throws JSONException {
		return null;
	}
	
}
