package com.damy.nongyao;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.damy.Utils.AutoSizeTextView;
import com.damy.Utils.PullToRefreshBase;
import com.damy.Utils.PullToRefreshListView;
import com.damy.Utils.ResolutionSet;
import com.damy.adapters.MoneyReportAdapter;
import com.damy.adapters.MoneyReportDetailAdapter;
import com.damy.adapters.SaleSearchDetailAdapter;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;
import com.damy.datatypes.STMoneyReportDetailInfo;
import com.damy.datatypes.STMoneyReportInfo;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

public class MoneyReportActivity extends BaseActivity {
	
	private enum REQ_TYPE{REQ_GETINOUTLOG, REQ_GETINOUTTOTALPROFIT, REQ_GETINOUTDETAIL};
	
	private PopupWindow 					dialog_datepicker;
    private LinearLayout					m_MaskLayer;
	
	private PullToRefreshListView			m_lvMoneyReportListView;
	private ListView			            m_lvMoneyReportDetailListView;
	
	private ArrayList<STMoneyReportDetailInfo> 	m_MoneyReportDetailList = new ArrayList<STMoneyReportDetailInfo>();
	private ArrayList<STMoneyReportInfo> 	m_MoneyReportList;
	
	private MoneyReportAdapter				m_MoneyReportAdapter = null;
	private MoneyReportDetailAdapter		m_MoneyReportDetailAdapter = null;
	private ListView						mRealListView;
	
	private int								m_nCurPageNumber = 1;
	private float							m_CurTotalProfit = 0;
	private AutoSizeTextView				txt_startdate;
	private AutoSizeTextView				txt_enddate;
	private AutoSizeTextView				txt_totalprofit;
	private AutoSizeTextView                txt_detail_date;
	
	private LinearLayout					ll_seltab1;
	private LinearLayout					ll_seltab2;
	private LinearLayout					ll_seltab3;
	private LinearLayout                    ll_moneyreport_detail;
	
	private int								m_curSelDateType;
    private DatePicker						m_DatePicker;
	
	private REQ_TYPE						m_reqType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_money_report);
		
		ResolutionSet._instance.iterateChild(findViewById(R.id.fl_moneyreport));
		
		initContents();
		setMoneyReportAdapter();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		m_reqType = REQ_TYPE.REQ_GETINOUTTOTALPROFIT;
		new LoadResponseThread(MoneyReportActivity.this).start();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	onClickBack();
        	return true;
        }
        return false;
    }
	
	private void initContents()
	{
		FrameLayout fl_backbtn = (FrameLayout)findViewById(R.id.fl_money_backbtn);
		FrameLayout fl_homebtn = (FrameLayout)findViewById(R.id.fl_money_homebtn);
		FrameLayout fl_reportbtn = (FrameLayout)findViewById(R.id.fl_moneyreport_btn);
		FrameLayout fl_paymentbtn = (FrameLayout)findViewById(R.id.fl_moneypayment_logbtn);
		FrameLayout fl_otherbtn = (FrameLayout)findViewById(R.id.fl_moneyother_paymentbtn);
		FrameLayout fl_cashbtn = (FrameLayout)findViewById(R.id.fl_moneycash_btn);
		FrameLayout fl_manreportbtn = (FrameLayout)findViewById(R.id.fl_moneymanrepot_btn);
		
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
		
		fl_reportbtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickReport();
        	}
        });
		
		fl_paymentbtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickPayment();
        	}
        });
		
		fl_otherbtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickOther();
        	}
        });
		
		fl_cashbtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickCash();
        	}
        });
		
		fl_manreportbtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickManreport();
        	}
        });
		
		FrameLayout ll_tab1 = (FrameLayout)findViewById(R.id.fl_moneyreport_tab1);
		FrameLayout ll_tab2 = (FrameLayout)findViewById(R.id.fl_moneyreport_tab2);
		FrameLayout ll_tab3 = (FrameLayout)findViewById(R.id.fl_moneyreport_tab3);
		
		ll_tab1.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickTab(1);
        	}
        });
		
		ll_tab2.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickTab(2);
        	}
        });
		
		ll_tab3.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickTab(3);
        	}
        });
		
		ll_seltab1 = (LinearLayout)findViewById(R.id.ll_moneyreport_seltab1);
		ll_seltab2 = (LinearLayout)findViewById(R.id.ll_moneyreport_seltab2);
		ll_seltab3 = (LinearLayout)findViewById(R.id.ll_moneyreport_seltab3);
		
		LinearLayout ll_startdate = (LinearLayout)findViewById(R.id.ll_moneyreport_startdate);
		LinearLayout ll_enddate = (LinearLayout)findViewById(R.id.ll_moneyreport_enddate);
		
		ll_startdate.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		//onClickStartDate();
        	}
        });
		
		ll_enddate.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		//onClickEndDate();
        	}
        });
		
		ll_moneyreport_detail = (LinearLayout)findViewById(R.id.ll_moneyreport_detail);		
		ll_moneyreport_detail.setVisibility(View.INVISIBLE);
		
		m_MaskLayer = (LinearLayout)findViewById(R.id.ll_moneyreport_masklayer);
		m_MaskLayer.setVisibility(View.INVISIBLE);
		
	
		
		View popupview = View.inflate(this, R.layout.dialog_datepicker, null);
		ResolutionSet._instance.iterateChild(popupview);
		dialog_datepicker = new PopupWindow(popupview, R.dimen.common_datepicker_dialog_width, R.dimen.common_datepicker_dialog_height, true);
		dialog_datepicker.setAnimationStyle(-1);
		
		FrameLayout fl_popupok = (FrameLayout)popupview.findViewById(R.id.fl_dialog_datepicker_okbtn);
		FrameLayout fl_popupcancel = (FrameLayout)popupview.findViewById(R.id.fl_dialog_datepicker_cancelbtn);
		m_DatePicker = (DatePicker)popupview.findViewById(R.id.dp_dialog_datepicker_date);
		
		fl_popupok.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickPopupOk();
        	}
        });
		
		fl_popupcancel.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickPopupCancel();
        	}
        });
		
		m_lvMoneyReportListView = (PullToRefreshListView)findViewById(R.id.anMoneyReportContentView);
		
		txt_startdate = (AutoSizeTextView)findViewById(R.id.txt_moneyreport_startdate);
		txt_enddate = (AutoSizeTextView)findViewById(R.id.txt_moneyreport_enddate);
		
		Date curDate = new Date();
		String strDate = String.valueOf(curDate.getYear() + 1900) + "-" + String.valueOf(curDate.getMonth() + 1) + "-" + String.valueOf(curDate.getDate());
		txt_enddate.setText(strDate);
		txt_startdate.setText(strDate);
		
		txt_totalprofit = (AutoSizeTextView)findViewById(R.id.txt_moneyreport_totalprofit);
		
		txt_detail_date = (AutoSizeTextView)findViewById(R.id.txt_moneyreport_detail_date);
		m_lvMoneyReportDetailListView = (ListView)findViewById(R.id.anMoneyReportDetailContentView);
		
		FrameLayout fl_moneyreport_detail_close = (FrameLayout)findViewById(R.id.fl_moneyreport_detail_close);
		fl_moneyreport_detail_close.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickDetailClose();
        	}
        });
		
	}

	private void onClickBack()
	{
		//finish();
		onClickHome();
	}
	
	private void onClickHome()
	{
		Intent main_activity = new Intent(this, MainActivity.class);
		startActivity(main_activity);	
		finish();
	}
	
	private void onClickReport()
	{
		/*
		Intent report_activity = new Intent(this, MoneyReportActivity.class);
		startActivity(report_activity);
		finish();	
		*/		
	}
	
	private void onClickPayment()
	{
		Intent payment_activity = new Intent(this, MoneyPaymentActivity.class);
		startActivity(payment_activity);
		finish();
	}
	
	private void onClickOther()
	{
		Intent other_activity = new Intent(this, MoneyOtherpayActivity.class);
		startActivity(other_activity);	
		finish();
	}
	
	private void onClickCash()
	{
		Intent bank_activity = new Intent(this, MoneyBankActivity.class);
		startActivity(bank_activity);	
		finish();
	}
	
	private void onClickManreport()
	{
		Intent manreport_activity = new Intent(this, MoneyManreportActivity.class);
		startActivity(manreport_activity);
		finish();
	}
	
	private void onClickTab(int ind)
	{
		ll_seltab1.setBackgroundColor(getResources().getColor(R.color.common_background));
		ll_seltab2.setBackgroundColor(getResources().getColor(R.color.common_background));
		ll_seltab3.setBackgroundColor(getResources().getColor(R.color.common_background));
		
		Date curDate = new Date();
		String strDate = String.valueOf(curDate.getYear() + 1900) + "-" + String.valueOf(curDate.getMonth() + 1) + "-" + String.valueOf(curDate.getDate());
		txt_enddate.setText(strDate);
		
		if ( ind == 1 )
		{
			ll_seltab1.setBackgroundColor(getResources().getColor(R.color.common_line));
			txt_startdate.setText(strDate);
		}
		else if ( ind == 2 )
		{
			ll_seltab2.setBackgroundColor(getResources().getColor(R.color.common_line));
			strDate = String.valueOf(curDate.getYear() + 1900) + "-" + String.valueOf(curDate.getMonth() + 1) + "-1";
			txt_startdate.setText(strDate);
		}
		else
		{
			ll_seltab3.setBackgroundColor(getResources().getColor(R.color.common_line));
			strDate = String.valueOf(curDate.getYear() + 1900) + "-1-1";
			txt_startdate.setText(strDate);
		}
		
		m_reqType = REQ_TYPE.REQ_GETINOUTTOTALPROFIT;
		new LoadResponseThread(MoneyReportActivity.this).start();
		
		ll_moneyreport_detail.setVisibility(View.INVISIBLE);
	}
	
	private void onClickStartDate()
	{
		m_curSelDateType = 1;
		String strDate = txt_startdate.getText().toString();
		String[] dateStrArr = new String[3];
		dateStrArr = strDate.split("-");
		m_DatePicker.updateDate(Integer.parseInt(dateStrArr[0]), Integer.parseInt(dateStrArr[1]) - 1, Integer.parseInt(dateStrArr[2]));
		dialog_datepicker.showAtLocation(findViewById(R.id.ll_moneyreport_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
	}
	
	private void onClickEndDate()
	{
		m_curSelDateType = 2;
		String strDate = txt_enddate.getText().toString();
		String[] dateStrArr = new String[3];
		dateStrArr = strDate.split("-");
		m_DatePicker.updateDate(Integer.parseInt(dateStrArr[0]), Integer.parseInt(dateStrArr[1]) - 1, Integer.parseInt(dateStrArr[2]));
		dialog_datepicker.showAtLocation(findViewById(R.id.ll_moneyreport_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
	}
	
	private void onClickPopupOk()
	{
		dialog_datepicker.dismiss();
		String strDate = String.valueOf(m_DatePicker.getYear()) + "-" + String.valueOf(m_DatePicker.getMonth() + 1) + "-" + String.valueOf(m_DatePicker.getDayOfMonth());
		if ( m_curSelDateType == 1 )
			txt_startdate.setText(strDate);
		else
			txt_enddate.setText(strDate);
		
		m_reqType = REQ_TYPE.REQ_GETINOUTTOTALPROFIT;
		new LoadResponseThread(MoneyReportActivity.this).start();
	}
	
	private void onClickPopupCancel()
	{
		dialog_datepicker.dismiss();
	}
	
	private void setMoneyReportAdapter() {
		m_MoneyReportList = new ArrayList<STMoneyReportInfo>();
		m_lvMoneyReportListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);

        // Set a listener to be invoked when the list should be refreshed.
		m_lvMoneyReportListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                m_nCurPageNumber = m_nCurPageNumber + 1;
                m_reqType = REQ_TYPE.REQ_GETINOUTLOG;
                new LoadResponseThread(MoneyReportActivity.this).start();
            }
        });

        mRealListView = m_lvMoneyReportListView.getRefreshableView();
        registerForContextMenu(mRealListView);

        //mRealListView.setDivider(new ColorDrawable(Color.TRANSPARENT));
        mRealListView.setCacheColorHint(Color.TRANSPARENT);
        mRealListView.setDividerHeight(0);

        m_MoneyReportAdapter = new MoneyReportAdapter(MoneyReportActivity.this, m_MoneyReportList);
        mRealListView.setAdapter(m_MoneyReportAdapter);
	}
	
	private void setMoneyReportDetailAdapter() {
		m_lvMoneyReportDetailListView.setCacheColorHint(Color.TRANSPARENT);
		m_lvMoneyReportDetailListView.setDividerHeight(0);
		
		m_MoneyReportDetailAdapter = new MoneyReportDetailAdapter(MoneyReportActivity.this, m_MoneyReportDetailList);
		m_lvMoneyReportDetailListView.setAdapter(m_MoneyReportDetailAdapter);
	}
	
	public void refreshUI() {
		super.refreshUI();
		
		if ( m_reqType == REQ_TYPE.REQ_GETINOUTTOTALPROFIT )
		{
			txt_totalprofit.setText(String.valueOf(m_CurTotalProfit) + getResources().getString(R.string.common_yuan));
			
			m_nCurPageNumber = 1;
			m_MoneyReportList.clear();
			m_reqType = REQ_TYPE.REQ_GETINOUTLOG;
			new LoadResponseThread(MoneyReportActivity.this).start();
		}
		else if ( m_reqType == REQ_TYPE.REQ_GETINOUTLOG )
		{
			m_MoneyReportAdapter.notifyDataSetChanged();
			m_lvMoneyReportListView.onRefreshComplete();
		}
		else if (m_reqType == REQ_TYPE.REQ_GETINOUTDETAIL)
		{
			setMoneyReportDetailAdapter();
			ll_moneyreport_detail.setVisibility(View.VISIBLE);
		}
	
	}
	
	public void getResponseJSON() {
		try {
			if ( m_reqType == REQ_TYPE.REQ_GETINOUTTOTALPROFIT )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETINOUTTOTALPROFIT;
				strRequest += "?shop_id=" + String.valueOf(Global.Cur_ShopId);
				strRequest += "&start_date=" + txt_startdate.getText().toString();
				strRequest += "&end_date=" + txt_enddate.getText().toString();
				
				JSONObject response = m_HttpConnUsingJSON.getGetJSONObject(strRequest);
				if (response == null) {
					m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
					return;
				}
	
				m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
				if (m_nResponse == ResponseRet.RET_SUCCESS) {
					JSONObject dataObject = response.getJSONObject(ResponseData.RESPONSE_DATA);
					
					m_CurTotalProfit = (float)dataObject.getDouble("totalprofit");
				}
			}
			else if ( m_reqType == REQ_TYPE.REQ_GETINOUTLOG )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETINOUTLOG;
				strRequest += "?shop_id=" + String.valueOf(Global.Cur_ShopId);
				strRequest += "&start_date=" + txt_startdate.getText().toString();
				strRequest += "&end_date=" + txt_enddate.getText().toString();
				strRequest += "&pagenum=" + Integer.toString(m_nCurPageNumber);
				
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
		            
		            for (int i = 0; i < count; i++) {
		            	JSONObject tmpObj = (JSONObject) dataList.get(i);
			            STMoneyReportInfo itemInfo = new STMoneyReportInfo();
	
						itemInfo.date = tmpObj.getString("date");
						itemInfo.incoming = (float)tmpObj.getDouble("saleincome");
						itemInfo.originprice = (float)tmpObj.getDouble("saleorigin");
						itemInfo.restmoney = (float)tmpObj.getDouble("change");
						itemInfo.earn = (float)tmpObj.getDouble("profit");
						
						m_MoneyReportList.add(itemInfo);
		            }
				}
			}
			else if ( m_reqType == REQ_TYPE.REQ_GETINOUTDETAIL)				
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETINOUTLOGDETAIL;
				strRequest += "?shop_id=" + String.valueOf(Global.Cur_ShopId);
				strRequest += "&date=" + txt_detail_date.getText().toString();
				
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
		            
		            for (int i = 0; i < count; i++) {
		            	JSONObject tmpObj = (JSONObject) dataList.get(i);
			            STMoneyReportDetailInfo itemInfo = new STMoneyReportDetailInfo();
	
						itemInfo.no = tmpObj.getInt("no");
						itemInfo.description = tmpObj.getString("description");
						itemInfo.money = (float)tmpObj.getDouble("money");
						itemInfo.username = tmpObj.getString("username");
						itemInfo.reason = tmpObj.getString("reason");
						
						m_MoneyReportDetailList.add(itemInfo);
		            }
				}
				
			}
		} catch (JSONException e) {
			e.printStackTrace();
			m_nResponse = ResponseRet.RET_JSON_EXCEPTION;
		}
	}
	
	public void onClickDetail(int pos, View v)
	{
		STMoneyReportInfo itemInfo = new STMoneyReportInfo();
		itemInfo = m_MoneyReportList.get(pos);
		txt_detail_date.setText(itemInfo.date);
		
		m_MoneyReportDetailList.clear();		
		
		m_reqType = REQ_TYPE.REQ_GETINOUTDETAIL;
		new LoadResponseThread(MoneyReportActivity.this).start();		
	}
	
	public void onClickDetailClose()
	{
		ll_moneyreport_detail.setVisibility(View.INVISIBLE);
	}

	public JSONObject makeRequestJSON() throws JSONException {
		return null;
	}

}
