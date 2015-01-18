package com.damy.nongyao;

import java.util.ArrayList;
import java.util.Date;

import android.widget.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.damy.Utils.AutoSizeTextView;
import com.damy.Utils.PullToRefreshBase;
import com.damy.Utils.PullToRefreshListView;
import com.damy.Utils.ResolutionSet;
import com.damy.adapters.DialogSelectAdapter;
import com.damy.adapters.MoneyBankAdapter;
import com.damy.adapters.MoneyBankDetailAdapter;
import com.damy.adapters.MoneyReportDetailAdapter;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;
import com.damy.datatypes.STMoneyBankDetailInfo;
import com.damy.datatypes.STMoneyBankInfo;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

public class MoneyBankActivity extends BaseActivity {
	
	private enum REQ_TYPE{REQ_GETBANKCASHLOGLIST, REQ_GETBANKDETAILLIST};
	
	private PopupWindow 				dialog_datepicker;
    private LinearLayout				m_MaskLayer;
    private LinearLayout                ll_moneybank_detail;
    private LinearLayout                ll_moneybankdetail_paytype;
    private LinearLayout                ll_moneybank_main;
    
    private PullToRefreshListView		m_lvMoneyBankListView;
    private ListView                    m_lvMoneyBankDetailListView;
    
	private ArrayList<STMoneyBankInfo> 	m_MoneyBankList;
	private ArrayList<STMoneyBankDetailInfo> m_MoneyBankDetailList = new ArrayList<STMoneyBankDetailInfo>();
	
	private MoneyBankAdapter			m_MoneyBankAdapter = null;
	private ListView					mRealListView;
	
	private MoneyBankDetailAdapter      m_MoneyBankDetailAdapter = null;
	private ListView                    mRealDetailListView;
    
    private int							m_curSelDateType;
    private DatePicker					m_DatePicker;
    
    private int							m_nCurPageNumber = 1;
    private int                         m_CurPayType = 4;
    
    private AutoSizeTextView			txt_startdate;
    private AutoSizeTextView			txt_enddate;
    private AutoSizeTextView			txt_detail_date;
    private AutoSizeTextView			txt_detail_money;
    private AutoSizeTextView			txt_detail_bank;
    private AutoSizeTextView			txt_detail_sum;
    private AutoSizeTextView            txt_detail_paytype;
    
    
    private REQ_TYPE					m_reqType;
    
    private PopupWindow                 dialog_paytype;
    private ArrayList<String>	        m_typeList = new ArrayList<String>();
    
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_money_bank);
		
		ResolutionSet._instance.iterateChild(findViewById(R.id.fl_moneybank));
		
		initContents();
		setMoneyBankAdapter();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		m_nCurPageNumber = 1;
		m_MoneyBankList.clear();
		m_reqType = REQ_TYPE.REQ_GETBANKCASHLOGLIST;
		new LoadResponseThread(MoneyBankActivity.this).start();
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
		
		LinearLayout ll_startdate = (LinearLayout)findViewById(R.id.ll_moneybank_startdate);
		LinearLayout ll_enddate = (LinearLayout)findViewById(R.id.ll_moneybank_enddate);
		
		ll_startdate.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickStartDate();
        	}
        });
		
		ll_enddate.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickEndDate();
        	}
        });
		
		
		m_MaskLayer = (LinearLayout)findViewById(R.id.ll_moneybank_masklayer);
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

		txt_startdate = (AutoSizeTextView)findViewById(R.id.txt_moneybank_startdate);
		txt_enddate = (AutoSizeTextView)findViewById(R.id.txt_moneybank_enddate);
		
		Date curDate = new Date();
		Date beforeDate = new Date();
		beforeDate.setMonth(curDate.getMonth() - 1);
		String strDate = String.valueOf(curDate.getYear() + 1900) + "-" + String.valueOf(curDate.getMonth() + 1) + "-" + String.valueOf(curDate.getDate());
		txt_enddate.setText(strDate);
		
		strDate = String.valueOf(beforeDate.getYear() + 1900) + "-" + String.valueOf(beforeDate.getMonth() + 1) + "-" + String.valueOf(beforeDate.getDate());
		txt_startdate.setText(strDate);
		
		m_lvMoneyBankListView = (PullToRefreshListView)findViewById(R.id.anMoneyBankContentView);
		m_lvMoneyBankDetailListView = (ListView)findViewById(R.id.anMoneyBankDetailContentView);
		
		txt_detail_date = (AutoSizeTextView)findViewById(R.id.txt_moneybankdetail_date);
		txt_detail_money = (AutoSizeTextView)findViewById(R.id.txt_moneybankdetail_money);
		txt_detail_bank = (AutoSizeTextView)findViewById(R.id.txt_moneybankdetail_bank);
		txt_detail_sum = (AutoSizeTextView)findViewById(R.id.txt_moneybankdetail_sum);
		
		ll_moneybank_detail = (LinearLayout)findViewById(R.id.ll_moneybank_detail);
		ll_moneybank_detail.setVisibility(View.INVISIBLE);
		
		txt_detail_paytype = (AutoSizeTextView)findViewById(R.id.txt_moneybankdetail_paytype);
		txt_detail_paytype.setText(getResources().getString(R.string.common_all));
		
		FrameLayout fl_moneybank_detail_close = (FrameLayout)findViewById(R.id.fl_moneybank_detail_close);
		fl_moneybank_detail_close.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickDetailClose();
        	}
        });
		
		ll_moneybankdetail_paytype = (LinearLayout)findViewById(R.id.ll_moneybankdetail_paytype);
		
		ll_moneybankdetail_paytype.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSelectPayType();
        	}
        });
		
		dialog_paytype = new PopupWindow(popupview, R.dimen.common_popup_dialog_width, R.dimen.common_popup_dialog_height,true);
		dialog_paytype.setAnimationStyle(-1);
		
		setDialogPayTypeAdapter();
		
		ll_moneybank_main = (LinearLayout)findViewById(R.id.ll_moneybank_main);
		
		
	}
	
	private void setMoneyReportDetailAdapter() {
		
		m_lvMoneyBankDetailListView.setCacheColorHint(Color.TRANSPARENT);
		m_lvMoneyBankDetailListView.setDividerHeight(0);
		
		m_MoneyBankDetailAdapter = new MoneyBankDetailAdapter(MoneyBankActivity.this, m_MoneyBankDetailList);
		m_lvMoneyBankDetailListView.setAdapter(m_MoneyBankDetailAdapter);
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
		Intent report_activity = new Intent(this, MoneyReportActivity.class);
		startActivity(report_activity);
		finish();
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
		/*
		Intent bank_activity = new Intent(this, MoneyBankActivity.class);
		startActivity(bank_activity);	
		finish();
		*/
	}
	
	private void onClickDetailClose()
	{
		ll_moneybank_main.setVisibility(View.VISIBLE);
		ll_moneybank_detail.setVisibility(View.INVISIBLE);
	}
	
	private void onClickManreport()
	{
		Intent manreport_activity = new Intent(this, MoneyManreportActivity.class);
		startActivity(manreport_activity);
		finish();
	}
	
	private void onClickStartDate()
	{
		m_MaskLayer.setVisibility(View.VISIBLE);
		
		m_curSelDateType = 1;
		String strDate = txt_startdate.getText().toString();
		String[] dateStrArr = new String[3];
		dateStrArr = strDate.split("-");
		m_DatePicker.updateDate(Integer.parseInt(dateStrArr[0]), Integer.parseInt(dateStrArr[1]) - 1, Integer.parseInt(dateStrArr[2]));
		dialog_datepicker.showAtLocation(findViewById(R.id.ll_moneybank_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
	}
	
	private void onClickEndDate()
	{
		m_MaskLayer.setVisibility(View.VISIBLE);
		
		m_curSelDateType = 2;
		String strDate = txt_enddate.getText().toString();
		String[] dateStrArr = new String[3];
		dateStrArr = strDate.split("-");
		m_DatePicker.updateDate(Integer.parseInt(dateStrArr[0]), Integer.parseInt(dateStrArr[1]) - 1, Integer.parseInt(dateStrArr[2]));
		dialog_datepicker.showAtLocation(findViewById(R.id.ll_moneybank_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
	}
	
	private void onClickPopupOk()
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		dialog_datepicker.dismiss();
		String strDate = String.valueOf(m_DatePicker.getYear()) + "-" + String.valueOf(m_DatePicker.getMonth() + 1) + "-" + String.valueOf(m_DatePicker.getDayOfMonth());
		if ( m_curSelDateType == 1 )
		{
			txt_startdate.setText(strDate);
		}
		else
		{
			if ( strDate.compareTo(txt_startdate.getText().toString()) < 0 )
				showToastMessage(getResources().getString(R.string.error_date_startend));
			else
				txt_enddate.setText(strDate);
		}
		
		m_nCurPageNumber = 1;
		m_MoneyBankList.clear();
		m_reqType = REQ_TYPE.REQ_GETBANKCASHLOGLIST;
		new LoadResponseThread(MoneyBankActivity.this).start();
	}
	
	private void onClickPopupCancel()
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		dialog_datepicker.dismiss();
	}
	
	private void onClickSelectPayType()
	{
		m_MaskLayer.setVisibility(View.VISIBLE);
		dialog_paytype.showAtLocation(findViewById(R.id.ll_moneybank_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
	}
	
	private void setDialogPayTypeAdapter()
	{
		View popupview = View.inflate(this, R.layout.dialog_select, null);
		ResolutionSet._instance.iterateChild(popupview);
		
		AutoSizeTextView txt_title = (AutoSizeTextView)popupview.findViewById(R.id.txt_dialog_title);
		txt_title.setText(getResources().getString(R.string.common_detailtype));

        ImageView img_cancel = (ImageView) popupview.findViewById(R.id.img_dialog_cancel);
        img_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog_paytype != null && dialog_paytype.isShowing() )
                {
                    m_MaskLayer.setVisibility(View.INVISIBLE);
                    dialog_paytype.dismiss();
                }
            }
        });

		m_typeList.add(getResources().getString(R.string.common_all));
		m_typeList.add(getResources().getString(R.string.common_actual));		
		m_typeList.add(getResources().getString(R.string.common_bankpayment));
		
		DialogSelectAdapter Adapter = new DialogSelectAdapter(this, m_typeList);
		
		ListView list = (ListView)popupview.findViewById(R.id.lv_dialog_listview);
		list.setAdapter(Adapter);
		list.setDrawSelectorOnTop(true);
		list.setDivider(new ColorDrawable(getResources().getColor(R.color.dialog_line)));
		list.setCacheColorHint(Color.TRANSPARENT);
        list.setDividerHeight(1);
		
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onClickPayTypeItem(position);
        	}
		});
		
		dialog_paytype = new PopupWindow(popupview, R.dimen.common_popup_dialog_width, R.dimen.common_popup_dialog_height,true);
		dialog_paytype.setAnimationStyle(-1);
	}
	
	private void onClickPayTypeItem(int pos)
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		dialog_paytype.dismiss();
		
		txt_detail_paytype.setText(m_typeList.get(pos));
		if (pos == 0) m_CurPayType = 4;
		else if (pos == 1) m_CurPayType = 0;
		else m_CurPayType = 2;
		
		m_MoneyBankDetailList.clear();		
		m_reqType = REQ_TYPE.REQ_GETBANKDETAILLIST;
		new LoadResponseThread(MoneyBankActivity.this).start();		
		
	}
	
	public STMoneyBankInfo getItem(int position)
	{
		if (position < 0 || position >= m_MoneyBankList.size())
			return null;
		
		return m_MoneyBankList.get(position);
	}
	
	private void setMoneyBankAdapter() {
		m_MoneyBankList = new ArrayList<STMoneyBankInfo>();
		m_lvMoneyBankListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);

        // Set a listener to be invoked when the list should be refreshed.
		m_lvMoneyBankListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                m_nCurPageNumber = m_nCurPageNumber + 1;
                m_reqType = REQ_TYPE.REQ_GETBANKCASHLOGLIST;
                new LoadResponseThread(MoneyBankActivity.this).start();
            }
        });

        mRealListView = m_lvMoneyBankListView.getRefreshableView();
        registerForContextMenu(mRealListView);

        //mRealListView.setDivider(new ColorDrawable(Color.TRANSPARENT));
        mRealListView.setCacheColorHint(Color.TRANSPARENT);
        mRealListView.setDividerHeight(0);
        //mRealListView.setDrawSelectorOnTop(true);

        m_MoneyBankAdapter = new MoneyBankAdapter(MoneyBankActivity.this, m_MoneyBankList);
        mRealListView.setAdapter(m_MoneyBankAdapter);
        
	}
	
	public void onClickDetail(int pos, View v)
	{
		STMoneyBankInfo itemInfo = new STMoneyBankInfo();
		itemInfo = m_MoneyBankList.get(pos);
		
		txt_detail_paytype.setText(getResources().getString(R.string.common_all));
		txt_detail_date.setText(itemInfo.date);
		txt_detail_money.setText(String.valueOf(itemInfo.money) + getResources().getString(R.string.common_yuan));
		txt_detail_bank.setText(String.valueOf(itemInfo.bank) + getResources().getString(R.string.common_yuan));
		txt_detail_sum.setText(String.valueOf(itemInfo.sum) + getResources().getString(R.string.common_yuan));
		
		m_CurPayType = 4;
		
		m_MoneyBankDetailList.clear();
		
		m_reqType = REQ_TYPE.REQ_GETBANKDETAILLIST;
		new LoadResponseThread(MoneyBankActivity.this).start();		
	}
	
	
	public void refreshUI() {
		super.refreshUI();
		
		if ( m_reqType == REQ_TYPE.REQ_GETBANKCASHLOGLIST )
		{
			m_MoneyBankAdapter.notifyDataSetChanged();
			m_lvMoneyBankListView.onRefreshComplete();
		}
		else if ( m_reqType == REQ_TYPE.REQ_GETBANKDETAILLIST) 
		{
			setMoneyReportDetailAdapter();
			ll_moneybank_detail.setVisibility(View.VISIBLE);
			ll_moneybank_main.setVisibility(View.INVISIBLE);
		}
	}
	
	public void getResponseJSON() {
		try {
			if ( m_reqType == REQ_TYPE.REQ_GETBANKCASHLOGLIST )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETBANKCASHLOGLIST;
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
			            STMoneyBankInfo itemInfo = new STMoneyBankInfo();
	
						itemInfo.date = tmpObj.getString("date");
						itemInfo.money = (float)tmpObj.getDouble("money");
						itemInfo.bank = (float)tmpObj.getDouble("bank");
						itemInfo.sum = tmpObj.getInt("sum");
	
						m_MoneyBankList.add(itemInfo);
		            }
				}
			}
			else if ( m_reqType == REQ_TYPE.REQ_GETBANKDETAILLIST )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETBANKDETAILLIST;
				strRequest += "?shop_id=" + String.valueOf(Global.Cur_ShopId);
				strRequest += "&paytype=" + String.valueOf(m_CurPayType);
				strRequest += "&date=" + txt_detail_date.getText();
								
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
			            STMoneyBankDetailInfo itemInfo = new STMoneyBankDetailInfo();
	
						itemInfo.ticket = tmpObj.getString("ticket_num");
						itemInfo.paytype = (tmpObj.getInt("paytype") == 0)?getResources().getString(R.string.common_actual):getResources().getString(R.string.common_bankpayment);
						itemInfo.type = (tmpObj.getInt("type") == 1)?getResources().getString(R.string.common_income):getResources().getString(R.string.common_outgo);
						itemInfo.money = (float)tmpObj.getDouble("price");
						itemInfo.remark = tmpObj.getString("reason");
	
						m_MoneyBankDetailList.add(itemInfo);
		            }
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
