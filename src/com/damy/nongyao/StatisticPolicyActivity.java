package com.damy.nongyao;

import java.util.ArrayList;
import java.util.Date;

import android.widget.*;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.damy.adapters.DialogSelectAdapter;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.Utils.AutoSizeEditText;
import com.damy.Utils.AutoSizeTextView;
import com.damy.Utils.ResolutionSet;
import com.damy.common.Global;
import com.damy.backend.*;
import com.damy.charts.*;
import com.damy.datatypes.STShopSimpleInfo;

public class StatisticPolicyActivity extends BaseActivity {
	
	private LinearLayout					m_MaskLayer;
	
	private PopupWindow 					dialog_year;
	private AutoSizeTextView                m_txtYear;
	private AutoSizeTextView                m_txtRemain_Count;
	private AutoSizeTextView                m_txtSale_Count;
	private AutoSizeTextView                m_txtTotal_Count;
	
	private String                          Remain_Count = new String();
	private String                          Sale_Count = new String();
	private String                          Total_Count = new String();
	private ArrayList<String>               arGeneral = new ArrayList<String>();
	private int                             m_CurYear = 2014;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistic_policy);
		
		ResolutionSet._instance.iterateChild(findViewById(R.id.fl_statisticpolicy));
		
		initControls();
	}
	
	private void initControls()
	{
		FrameLayout fl_backbtn = (FrameLayout)findViewById(R.id.fl_statistic_backbtn);
		FrameLayout fl_homebtn = (FrameLayout)findViewById(R.id.fl_statistic_homebtn);
		FrameLayout fl_shopbtn = (FrameLayout)findViewById(R.id.fl_statistic_shopbtn);
		FrameLayout fl_nongyaobtn = (FrameLayout)findViewById(R.id.fl_statistic_nongyaobtn);
		FrameLayout fl_policybtn = (FrameLayout)findViewById(R.id.fl_statistic_policybtn);
		
		fl_backbtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickBack();
        	}
        });
		
		fl_homebtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickHome();
        	}
        });
		
		fl_shopbtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickShop();
        	}
        });
		
		fl_nongyaobtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickNongyao();
        	}
        });
		
		fl_policybtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickPolicy();
        	}
        });
		
		m_MaskLayer = (LinearLayout)findViewById(R.id.ll_statisticpolicy_masklayer);
		m_MaskLayer.setVisibility(View.INVISIBLE);
		
		FrameLayout fl_detail1 = (FrameLayout)findViewById(R.id.fl_statisticpolicy_detail1btn);
		FrameLayout fl_detail2 = (FrameLayout)findViewById(R.id.fl_statisticpolicy_detail2btn);
		FrameLayout fl_detail3 = (FrameLayout)findViewById(R.id.fl_statisticpolicy_detail3btn);
		
		fl_detail1.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickDetail1();
        	}
        });
		
		fl_detail2.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickDetail2();
        	}
        });
		
		fl_detail3.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickDetail3();
        	}
        });
		
		m_txtYear = (AutoSizeTextView)findViewById(R.id.txt_statistic_policy_year);
		m_txtRemain_Count = (AutoSizeTextView)findViewById(R.id.txt_statistic_policy_remain_count);
		m_txtSale_Count = (AutoSizeTextView)findViewById(R.id.txt_statistic_policy_sale_count);
		m_txtTotal_Count = (AutoSizeTextView)findViewById(R.id.txt_statistic_policy_total_count);
		
		LinearLayout ll_statistic_policy_year = (LinearLayout)findViewById(R.id.ll_statistic_policy_year);
		
		ll_statistic_policy_year.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickYearSelect();
        	}
        });
		
		
		setDialogYearAdapter();
		readPolicyContents();
		
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
		onClickHome();
	}
	
	private void onClickHome()
	{
		Intent login_activity = new Intent(this, LoginActivity.class);
		startActivity(login_activity);
		finish();
	}
	
	private void onClickShop()
	{
		Intent shop_activity = new Intent(this, StatisticShopActivity.class);
		startActivity(shop_activity);
		finish();
	}
	
	private void onClickNongyao()
	{
		Intent nongyao_activity = new Intent(this, StatisticNongyaoActivity.class);
		startActivity(nongyao_activity);
		finish();
	}
	
	private void onClickPolicy()
	{
	}
	
	private void onClickDetail1()
	{
		Intent detail1_activity = new Intent(this, StatisticPolicyDetail1Activity.class);
		detail1_activity.putExtra(StatisticPolicyDetail1Activity.STATISTICPOLICY_CURYEAR, m_txtYear.getText().toString());
		startActivity(detail1_activity);
		finish();
	}
	
	private void onClickDetail2()
	{
		Intent detail2_activity = new Intent(this, StatisticPolicyDetail2Activity.class);
		detail2_activity.putExtra(StatisticPolicyDetail2Activity.STATISTICPOLICY_CURYEAR, m_txtYear.getText().toString());
		startActivity(detail2_activity);
		finish();
	}
	
	private void onClickDetail3()
	{
		Intent detail3_activity = new Intent(this, StatisticPolicyDetail3Activity.class);
		detail3_activity.putExtra(StatisticPolicyDetail3Activity.STATISTICPOLICY_CURYEAR, m_txtYear.getText().toString());
		startActivity(detail3_activity);
		finish();
	}
	
	private void onClickYearSelect()
	{
		m_MaskLayer.setVisibility(View.VISIBLE);
		dialog_year.showAtLocation(findViewById(R.id.ll_statisticpolicy_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
	}
	
	private void setDialogYearAdapter()
	{
		View popupview = View.inflate(this, R.layout.dialog_select, null);
		ResolutionSet._instance.iterateChild(popupview);
		
		AutoSizeTextView txt_title = (AutoSizeTextView)popupview.findViewById(R.id.txt_dialog_title);
		txt_title.setText(getResources().getString(R.string.dialog_select_year));

        ImageView img_cancel = (ImageView) popupview.findViewById(R.id.img_dialog_cancel);
        img_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog_year != null && dialog_year.isShowing() )
                {
                    m_MaskLayer.setVisibility(View.INVISIBLE);
                    dialog_year.dismiss();
                }
            }
        });
		
		arGeneral.clear();
		
		Date CurDate = new Date();
		int CurYear = CurDate.getYear() + 1900;
		
		m_txtYear.setText(Integer.toString(CurYear));
		m_CurYear = CurYear;
		
		for ( int i = 2014; i <= CurYear; i++ )
			arGeneral.add(Integer.toString(i));
		
		DialogSelectAdapter Adapter = new DialogSelectAdapter(this, arGeneral);
		
		ListView list = (ListView)popupview.findViewById(R.id.lv_dialog_listview);
		list.setAdapter(Adapter);
		list.setDrawSelectorOnTop(true);
		list.setDivider(new ColorDrawable(getResources().getColor(R.color.dialog_line)));
		list.setCacheColorHint(Color.TRANSPARENT);
        list.setDividerHeight(1);
		
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onClickYearItem(position);
        	}
		});
		
		dialog_year = new PopupWindow(popupview, R.dimen.common_popup_dialog_width, R.dimen.common_popup_dialog_height,true);
		dialog_year.setAnimationStyle(-1);		
		
	}
	
	private void onClickYearItem(int position)
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		dialog_year.dismiss();		
		
		m_txtRemain_Count.setText("");
		m_txtSale_Count.setText("");
		m_txtTotal_Count.setText("");
		
		m_txtYear.setText(arGeneral.get(position));
		m_CurYear = Integer.valueOf(arGeneral.get(position));
		
		readPolicyContents();
	}
	
	private void readPolicyContents()
	{	
		new LoadResponseThread(StatisticPolicyActivity.this).start();
	}

	public void refreshUI() {
		super.refreshUI();
		if (m_nResponse == ResponseRet.RET_SUCCESS)
		{
		   m_txtRemain_Count.setText(Remain_Count);
		   m_txtSale_Count.setText(Sale_Count);
		   m_txtTotal_Count.setText(Total_Count);
		}
	}
	
	public void getResponseJSON() {
		try {
			m_nResponse = ResponseRet.RET_SUCCESS;
			
			String strRequest = HttpConnUsingJSON.REQ_STATISTIC_POLICY;
			strRequest += "?year=" +  Integer.toString(m_CurYear);

			JSONObject response = m_HttpConnUsingJSON.getGetJSONObject(strRequest);
			if (response == null) {
				m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
				return;
			}
			m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
            if (m_nResponse == ResponseRet.RET_SUCCESS) {
            	
            	JSONObject dataObject = response.getJSONObject(ResponseData.RESPONSE_DATA);

            	Remain_Count = String.format("%.1f", dataObject.getDouble("remain_count")) + "Kg";
            	Sale_Count = String.format("%.1f", dataObject.getDouble("sale_count")) + "Kg";
            	Total_Count = String.format("%.1f", dataObject.getDouble("total_count")) + "Kg";
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
