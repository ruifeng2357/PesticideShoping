package com.damy.nongyao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.damy.backend.LoadResponseThread;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.Utils.AutoSizeEditText;
import com.damy.Utils.ResolutionSet;
import com.damy.common.Global;
import com.damy.backend.*;
import com.damy.charts.SalesStackedBarChart;
import com.damy.datatypes.STAreaSaleCountInfo;
import com.damy.datatypes.STStoreInfo;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
/*import org.achartengine.chartdemo.demo.R;*/
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;

public class StatisticPolicyDetail1Activity extends BaseActivity {
	
	public static String 		STATISTICPOLICY_CURYEAR = "STATISTICPOLICY_CURYEAR";
	
	private String				m_curYear;
	private GraphicalView 		mChartView;
	
	private ArrayList<STAreaSaleCountInfo> barGraphList = new ArrayList<STAreaSaleCountInfo>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistic_policy_detail1);
		
		ResolutionSet._instance.iterateChild(findViewById(R.id.fl_statisticpolicy_detail1));
		
		m_curYear = getIntent().getStringExtra(STATISTICPOLICY_CURYEAR);
		
		initControls();
		new LoadResponseThread(StatisticPolicyDetail1Activity.this).start();
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
		
		int max_value = 0;
		int min_value = 0;
		SalesStackedBarChart barChart = new SalesStackedBarChart();
		barChart.setValues(barGraphList);
		max_value = GetMaxValue(barGraphList);
		min_value = GetMinValue(barGraphList);
		LinearLayout layout = (LinearLayout) findViewById(R.id.barchart);
		mChartView = barChart.getView(this, min_value, max_value);
		
		layout.addView(mChartView);
	}
	public int GetMinValue(ArrayList<STAreaSaleCountInfo> barGraphList)
	{
		int retValue = 0;
		int length = barGraphList.size();
		for(int i=0; i<length; i++)
		{
			if(retValue > barGraphList.get(i).sale_count)
			{
				retValue = barGraphList.get(i).sale_count; 
				
			}
		}
		if(retValue > 0)
		{
			retValue = 0;
		}
		if(retValue <0)
		{
			retValue -= 200;
		}
		return retValue;
	}
	public int GetMaxValue(ArrayList<STAreaSaleCountInfo> barGraphList)
	{
		int retValue = 0;
		int length = barGraphList.size();
		for(int i=0; i<length; i++)
		{
			if(retValue < barGraphList.get(i).remain_count || retValue < (barGraphList.get(i).remain_count + barGraphList.get(i).sale_count))
			{
				if(barGraphList.get(i).sale_count > 0)
				{
					retValue = barGraphList.get(i).remain_count + barGraphList.get(i).sale_count; 
				}
				else
				{
					retValue = barGraphList.get(i).remain_count;
				}
			}
		}
		return retValue;
	}
	public void getResponseJSON() {
		try {
			m_nResponse = ResponseRet.RET_SUCCESS;
			
			String strRequest = HttpConnUsingJSON.REQ_GETBARGRAPH;
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
					STAreaSaleCountInfo itemInfo = new STAreaSaleCountInfo();
					JSONObject tmpObj = dataList.getJSONObject(i);
					
					itemInfo.area = tmpObj.getString("region_name");
	            	itemInfo.sale_count = tmpObj.getInt("sale_count");
	            	itemInfo.remain_count = tmpObj.getInt("remain_count");
					
					barGraphList.add(itemInfo);
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
