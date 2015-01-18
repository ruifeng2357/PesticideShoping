package com.damy.nongyao;

import java.util.ArrayList;

import org.achartengine.GraphicalView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.damy.backend.LoadResponseThread;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.ResponseData;
import com.damy.Utils.AutoSizeEditText;
import com.damy.Utils.ResolutionSet;
import com.damy.charts.SalesLineChart;
import com.damy.common.Global;
import com.damy.datatypes.STAreaSaleCountInfo;
import com.damy.datatypes.STMonthSaleCountInfo;
import com.damy.backend.*;

public class StatisticPolicyDetail2Activity extends BaseActivity {
	
	public static String 		STATISTICPOLICY_CURYEAR = "STATISTICPOLICY_CURYEAR";
	
	private String				m_curYear;
	private GraphicalView 		mChartView;
	
	private ArrayList<STMonthSaleCountInfo> lineGraphList = new ArrayList<STMonthSaleCountInfo>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistic_policy_detail2);
		
		ResolutionSet._instance.iterateChild(findViewById(R.id.fl_statisticpolicy_detail2));
		
		m_curYear = getIntent().getStringExtra(STATISTICPOLICY_CURYEAR);
		
		initControls();
		new LoadResponseThread(StatisticPolicyDetail2Activity.this).start();
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
		SalesLineChart barChart = new SalesLineChart();
		barChart.setValues(lineGraphList);
		LinearLayout layout = (LinearLayout) findViewById(R.id.linechart);
		int min_value = GetMinValue(lineGraphList);
		int max_value = GetMaxValue(lineGraphList);
		mChartView = barChart.getView(this, m_curYear, min_value, max_value);
		
		layout.addView(mChartView);
	}
	public int GetMinValue(ArrayList<STMonthSaleCountInfo> lineGraphList)
	{
		int retValue = 0;
		int length = lineGraphList.size();
		for(int i=0; i<length; i++)
		{
			if(retValue > lineGraphList.get(i).sale_count)
			{
				retValue = lineGraphList.get(i).sale_count;
			}
		}
		if(retValue > 0)
		{
			retValue = 0;
		}
		if(retValue <0)
		{
			retValue -= 50;
		}
		return retValue;
	}
	public int GetMaxValue(ArrayList<STMonthSaleCountInfo> lineGraphList)
	{
		int retValue = 0;
		int length = lineGraphList.size();
		for(int i=0; i<length; i++)
		{
			if(retValue < lineGraphList.get(i).sale_count)
			{
				retValue = lineGraphList.get(i).sale_count;
			}
		}
		return retValue;
	}
	public void getResponseJSON() {
		try {
			m_nResponse = ResponseRet.RET_SUCCESS;
			
			String strRequest = HttpConnUsingJSON.REQ_GETLINEGRAPH;
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
					STMonthSaleCountInfo itemInfo = new STMonthSaleCountInfo();
					JSONObject tmpObj = dataList.getJSONObject(i);
					
					itemInfo.month = tmpObj.getInt("month");
	            	itemInfo.sale_count = tmpObj.getInt("sale_count");
	            						
					lineGraphList.add(itemInfo);
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
