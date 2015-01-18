package com.damy.nongyao;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import android.widget.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.damy.Utils.AutoSizeEditText;
import com.damy.Utils.AutoSizeTextView;
import com.damy.Utils.PullToRefreshBase;
import com.damy.Utils.PullToRefreshListView;
import com.damy.Utils.ResolutionSet;
import com.damy.adapters.DialogSelectAdapter;
import com.damy.adapters.MoneyPaymentAdapter;
import com.damy.adapters.MoneyPaymentDetailAdapter;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;
import com.damy.datatypes.STMoneyPaymentInfo;
import com.damy.datatypes.STCustomerInfo;
import com.damy.datatypes.STMoneyReportInfo;
import com.damy.datatypes.STRegionInfo;
import com.damy.datatypes.STShopInfo;
import com.damy.datatypes.STMoneyPaymentDetailInfo;

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

public class MoneyPaymentActivity extends BaseActivity {
	
	private enum REQ_TYPE{REQ_GETPAYMENTLOG, REQ_GETPAYMENTLOGDETAIL};
	
	private PullToRefreshListView				m_lvMoneyPaymentListView;
	private ArrayList<STMoneyPaymentInfo> 		m_MoneyPaymentList;
	private ArrayList<STMoneyPaymentDetailInfo>  m_MoneyPaymentDetailList = new ArrayList<STMoneyPaymentDetailInfo>();
	
	private MoneyPaymentAdapter					m_MoneyPaymentAdapter = null;
	private MoneyPaymentDetailAdapter		    m_MoneyPaymentDetailAdapter = null;
	
	private ListView							mRealListView;
	private ListView			                m_lvMoneyPaymentDetailListView;

    private int 								m_CurClickedItem = -1;
    private int									m_nCurPageNumber = 1;
    private PopupWindow 						dialog_customer;
	private PopupWindow 						dialog_type;
	private PopupWindow 				        dialog_datepicker;
    private LinearLayout						m_MaskLayer;
    
    private REQ_TYPE							m_reqType;
    
    private int									m_CurType = 2;
    
    
    private AutoSizeEditText					txt_CustomerName;
    private AutoSizeTextView					txt_Type;
    
    private LinearLayout                        ll_moneypayment_main;
    private LinearLayout                        ll_moneypayment_detail;   
  
    
    private AutoSizeTextView                    txt_moneypayment_startdate;
    private AutoSizeTextView                    txt_moneypayment_enddate;
    
    private int								    m_curSelDateType;
	private DatePicker						    m_DatePicker;
	private STMoneyPaymentInfo                  PaymentInfo = new STMoneyPaymentInfo();
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_money_payment);
		
		ResolutionSet._instance.iterateChild(findViewById(R.id.fl_moneypayment));
		
		initControls();
		setMoneyPaymentAdapter();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		readContents();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	onClickBack();
        	return true;
        }
        return false;
    }
	
	private void initControls()
	{
		FrameLayout fl_backbtn = (FrameLayout)findViewById(R.id.fl_money_backbtn);
		FrameLayout fl_homebtn = (FrameLayout)findViewById(R.id.fl_money_homebtn);
		FrameLayout fl_reportbtn = (FrameLayout)findViewById(R.id.fl_moneyreport_btn);
		FrameLayout fl_paymentbtn = (FrameLayout)findViewById(R.id.fl_moneypayment_logbtn);
		FrameLayout fl_otherbtn = (FrameLayout)findViewById(R.id.fl_moneyother_paymentbtn);
		FrameLayout fl_cashbtn = (FrameLayout)findViewById(R.id.fl_moneycash_btn);
		FrameLayout fl_manreportbtn = (FrameLayout)findViewById(R.id.fl_moneymanrepot_btn);
		FrameLayout fl_addbtn = (FrameLayout)findViewById(R.id.fl_moneypayment_addbtn);
		FrameLayout fl_historybtn = (FrameLayout)findViewById(R.id.fl_moneypayment_historybtn);
		FrameLayout fl_searchbtn = (FrameLayout)findViewById(R.id.fl_moneypayment_searchbtn);
		
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
		
		fl_addbtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickAdd();
        	}
        });
		
		fl_searchbtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSearch();
        	}
        });
		
		m_MaskLayer = (LinearLayout)findViewById(R.id.ll_moneypayment_masklayer);
		m_MaskLayer.setVisibility(View.INVISIBLE);
		
		m_lvMoneyPaymentListView = (PullToRefreshListView)findViewById(R.id.anMoneyPaymentContentView);
		m_lvMoneyPaymentDetailListView = (ListView)findViewById(R.id.anMoneyPaymentDetailContentView);
		
		LinearLayout txt_customersel = (LinearLayout)findViewById(R.id.ll_moneypayment_customerselect);
		LinearLayout txt_typesel = (LinearLayout)findViewById(R.id.ll_moneypayment_typeselect);
		
		txt_customersel.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSelectCustomer();
        	}
        });
		
		txt_typesel.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSelectType();
        	}
        });
		
		txt_CustomerName = (AutoSizeEditText)findViewById(R.id.edit_moneypayment_customer);
		txt_Type = (AutoSizeTextView)findViewById(R.id.txt_moneypayment_type);
		
		//txt_CustomerName.setText(getResources().getString(R.string.common_all));
		txt_Type.setText(getResources().getString(R.string.common_all));
		
		setDialogTypeAdapter();
		
		ll_moneypayment_main = (LinearLayout)findViewById(R.id.ll_moneypayment_main);
		ll_moneypayment_detail = (LinearLayout)findViewById(R.id.ll_moneypayment_detail);
		
		ll_moneypayment_detail.setVisibility(View.INVISIBLE);
		
		txt_moneypayment_startdate = (AutoSizeTextView)findViewById(R.id.txt_moneypayment_startdate);
		txt_moneypayment_enddate = (AutoSizeTextView)findViewById(R.id.txt_moneypayment_enddate);
		
		Date curDate = new Date();
		String strDate = String.valueOf(curDate.getYear() + 1900) + "-" + String.valueOf(curDate.getMonth() + 1) + "-" + String.valueOf(curDate.getDate());
		txt_moneypayment_enddate.setText(strDate);
		strDate = String.valueOf(curDate.getYear() + 1900) + "-" + String.valueOf(curDate.getMonth() + 1) + "-1";
		txt_moneypayment_startdate.setText(strDate);
		
		LinearLayout ll_startdate = (LinearLayout)findViewById(R.id.ll_moneypayment_startdate);
		LinearLayout ll_enddate = (LinearLayout)findViewById(R.id.ll_moneypayment_enddate);
		
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
		
		
		View popupdateview = View.inflate(this, R.layout.dialog_datepicker, null);
		ResolutionSet._instance.iterateChild(popupdateview);
		dialog_datepicker = new PopupWindow(popupdateview, R.dimen.common_datepicker_dialog_width, R.dimen.common_datepicker_dialog_height, true);
		dialog_datepicker.setAnimationStyle(-1);
		
		FrameLayout fl_popupok = (FrameLayout)popupdateview.findViewById(R.id.fl_dialog_datepicker_okbtn);
		FrameLayout fl_popupcancel = (FrameLayout)popupdateview.findViewById(R.id.fl_dialog_datepicker_cancelbtn);
		m_DatePicker = (DatePicker)popupdateview.findViewById(R.id.dp_dialog_datepicker_date);
		
		fl_popupok.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickDatePopupOk();
        	}
        });
		
		fl_popupcancel.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickDatePopupCancel();
        	}
        });

		fl_historybtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickHistoryBtn();
        	}
        });
		
		
		FrameLayout fl_moneypayment_detail_closebtn = (FrameLayout)findViewById(R.id.fl_moneypayment_detail_close);
		fl_moneypayment_detail_closebtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickDetailClose();
        	}
        });
		
	}
	
	private void onClickStartDate()
	{
		m_curSelDateType = 1;
		String strDate = txt_moneypayment_startdate.getText().toString();
		String[] dateStrArr = new String[3];
		dateStrArr = strDate.split("-");
		m_DatePicker.updateDate(Integer.parseInt(dateStrArr[0]), Integer.parseInt(dateStrArr[1]) - 1, Integer.parseInt(dateStrArr[2]));
		dialog_datepicker.showAtLocation(findViewById(R.id.ll_moneypayment_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		m_MaskLayer.setVisibility(View.VISIBLE);
	}
	
	private void onClickEndDate()
	{
		m_curSelDateType = 2;
		String strDate = txt_moneypayment_enddate.getText().toString();
		String[] dateStrArr = new String[3];
		dateStrArr = strDate.split("-");
		m_DatePicker.updateDate(Integer.parseInt(dateStrArr[0]), Integer.parseInt(dateStrArr[1]) - 1, Integer.parseInt(dateStrArr[2]));
		dialog_datepicker.showAtLocation(findViewById(R.id.ll_moneypayment_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		m_MaskLayer.setVisibility(View.VISIBLE);
	}
	
	private void onClickDatePopupOk()
	{
		dialog_datepicker.dismiss();
		String strDate = String.valueOf(m_DatePicker.getYear()) + "-" + String.valueOf(m_DatePicker.getMonth() + 1) + "-" + String.valueOf(m_DatePicker.getDayOfMonth());
		if ( m_curSelDateType == 1 )
		{
			txt_moneypayment_startdate.setText(strDate);
		}
		else
		{
			if ( strDate.compareTo(txt_moneypayment_startdate.getText().toString()) < 0 )
				showToastMessage(getResources().getString(R.string.error_date_startend));
			else
				txt_moneypayment_enddate.setText(strDate);
		}
		
		m_MaskLayer.setVisibility(View.INVISIBLE);
	}
	
	private void onClickDatePopupCancel()
	{
		dialog_datepicker.dismiss();
		m_MaskLayer.setVisibility(View.INVISIBLE);
	}
	
	private void readContents()
	{
		m_MoneyPaymentList.clear();
		m_reqType = REQ_TYPE.REQ_GETPAYMENTLOG;
		new LoadResponseThread(MoneyPaymentActivity.this).start();
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
		/*
		Intent payment_activity = new Intent(this, MoneyPaymentActivity.class);
		startActivity(payment_activity);
		finish();
		*/
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
	
	private void onClickAdd()
	{
		Intent add_activity = new Intent(this, MoneyPaymentAddActivity.class);
		startActivity(add_activity);
	}
	
	private void onClickHistoryBtn()
	{
		Intent history_activity = new Intent(this, MoneyPaymentHistoryActivity.class);
		startActivity(history_activity);
	}
	
	private void onClickSearch()
	{
		m_nCurPageNumber = 1;
		readContents();
	}
	
	public void onClickDetail(int pos, View v)
	{
		
		PaymentInfo = m_MoneyPaymentList.get(pos);		
		m_MoneyPaymentDetailList.clear();		
		
		m_reqType = REQ_TYPE.REQ_GETPAYMENTLOGDETAIL;
		new LoadResponseThread(MoneyPaymentActivity.this).start();		
	}
	
	public void onClickDetailClose()
	{
		ll_moneypayment_detail.setVisibility(View.INVISIBLE);
	}
	
	
	public STMoneyPaymentInfo getItem(int position)
	{
		if (position < 0 || position >= m_MoneyPaymentList.size())
			return null;
		
		return m_MoneyPaymentList.get(position);
	}
	
	private void setMoneyPaymentAdapter() {
		m_MoneyPaymentList = new ArrayList<STMoneyPaymentInfo>();
		m_lvMoneyPaymentListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);

        // Set a listener to be invoked when the list should be refreshed.
		m_lvMoneyPaymentListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                m_nCurPageNumber = m_nCurPageNumber + 1;
                m_reqType = REQ_TYPE.REQ_GETPAYMENTLOG;
                new LoadResponseThread(MoneyPaymentActivity.this).start();
            }
        });

        mRealListView = m_lvMoneyPaymentListView.getRefreshableView();
        registerForContextMenu(mRealListView);

        //mRealListView.setDivider(new ColorDrawable(Color.TRANSPARENT));
        mRealListView.setCacheColorHint(Color.TRANSPARENT);
        mRealListView.setDividerHeight(0);
        //mRealListView.setDrawSelectorOnTop(true);

        m_MoneyPaymentAdapter = new MoneyPaymentAdapter(MoneyPaymentActivity.this, m_MoneyPaymentList);
        mRealListView.setAdapter(m_MoneyPaymentAdapter);
	}
	
	private void setMoneyPaymentDetailAdapter() {
		m_lvMoneyPaymentDetailListView.setCacheColorHint(Color.TRANSPARENT);
		m_lvMoneyPaymentDetailListView.setDividerHeight(0);
		
		m_MoneyPaymentDetailAdapter = new MoneyPaymentDetailAdapter(MoneyPaymentActivity.this, m_MoneyPaymentDetailList);
		m_lvMoneyPaymentDetailListView.setAdapter(m_MoneyPaymentDetailAdapter);
	}
	
	private void onClickSelectCustomer()
	{
		m_MaskLayer.setVisibility(View.VISIBLE);
		dialog_customer.showAtLocation(findViewById(R.id.ll_moneypayment_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
	}
	
	private void onClickSelectType()
	{
		m_MaskLayer.setVisibility(View.VISIBLE);
		dialog_type.showAtLocation(findViewById(R.id.ll_moneypayment_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
	}
	
	private void setDialogTypeAdapter()
	{
		View popupview = View.inflate(this, R.layout.dialog_select, null);
		ResolutionSet._instance.iterateChild(popupview);
		
		AutoSizeTextView txt_title = (AutoSizeTextView)popupview.findViewById(R.id.txt_dialog_title);
		txt_title.setText(getResources().getString(R.string.dialog_select_givetakemoney));

        ImageView img_cancel = (ImageView) popupview.findViewById(R.id.img_dialog_cancel);
        img_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog_type != null && dialog_type.isShowing() )
                {
                    m_MaskLayer.setVisibility(View.INVISIBLE);
                    dialog_type.dismiss();
                }
            }
        });
		
		ArrayList<String> arGeneral = new ArrayList<String>();
		
		arGeneral.add(getResources().getString(R.string.common_all));
		arGeneral.add(getResources().getString(R.string.common_givemoney));
		arGeneral.add(getResources().getString(R.string.common_takemoney));
		
		DialogSelectAdapter Adapter = new DialogSelectAdapter(this, arGeneral);
		
		ListView list = (ListView)popupview.findViewById(R.id.lv_dialog_listview);
		list.setAdapter(Adapter);
		list.setDrawSelectorOnTop(true);
		list.setDivider(new ColorDrawable(getResources().getColor(R.color.dialog_line)));
		list.setCacheColorHint(Color.TRANSPARENT);
        list.setDividerHeight(1);
		
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onClickTypeItem(position);
        	}
		});
		
		dialog_type = new PopupWindow(popupview, R.dimen.common_popup_dialog_width, R.dimen.common_popup_dialog_height,true);
		dialog_type.setAnimationStyle(-1);
	}
	
	private void onClickTypeItem(int pos)
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		dialog_type.dismiss();
		
		String strType;
		
		if ( pos == 0 )
		{
			strType = getResources().getString(R.string.common_all);
			m_CurType = 2;
		}
		else if ( pos == 1 )
		{
			strType = getResources().getString(R.string.common_givemoney);
			m_CurType = 0;
		}
		else
		{
			strType = getResources().getString(R.string.common_takemoney);
			m_CurType = 1;
		}
		
		txt_Type.setText(strType);		
		
	}
	
	
	public void refreshUI() {
		super.refreshUI();
		
		if ( m_reqType == REQ_TYPE.REQ_GETPAYMENTLOG )
		{
			m_MoneyPaymentAdapter.notifyDataSetChanged();
			m_lvMoneyPaymentListView.onRefreshComplete();
		}
		else if ( m_reqType == REQ_TYPE.REQ_GETPAYMENTLOGDETAIL )
		{
			setMoneyPaymentDetailAdapter();
			ll_moneypayment_detail.setVisibility(View.VISIBLE);			
		}
		
	}
	
	public void getResponseJSON() {
		try {
			if ( m_reqType == REQ_TYPE.REQ_GETPAYMENTLOGDETAIL)
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETPAYMENTDETAILLOG;
				strRequest += "?shop_id=" + String.valueOf(Global.Cur_ShopId);
				strRequest += "&payment_id=" + Long.toString(PaymentInfo.payment_id);
				
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
						STMoneyPaymentDetailInfo itemInfo = new STMoneyPaymentDetailInfo();
						JSONObject tmpObj = dataList.getJSONObject(i);
						
						itemInfo.date = tmpObj.getString("date");
						itemInfo.ticketnum = tmpObj.getString("ticket_num");
						itemInfo.content = tmpObj.getString("content");
						itemInfo.type = tmpObj.getInt("type");
						itemInfo.price = (float)tmpObj.getDouble("price");		
						itemInfo.reason = tmpObj.getString("reason");
						
						m_MoneyPaymentDetailList.add(itemInfo);
					}
				}
			}
			else if ( m_reqType == REQ_TYPE.REQ_GETPAYMENTLOG )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETPAYMENTLOG;
				strRequest += "?shop_id=" + String.valueOf(Global.Cur_ShopId);
				strRequest += "&customer_search=" + txt_CustomerName.getText().toString();				
				strRequest += "&type=" + Integer.toString(m_CurType);
				strRequest += "&start_date=" + txt_moneypayment_startdate.getText().toString();
				strRequest += "&end_date=" + txt_moneypayment_enddate.getText().toString();
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
			            STMoneyPaymentInfo itemInfo = new STMoneyPaymentInfo();
	
						itemInfo.payment_id = tmpObj.getInt("payment_id");
						itemInfo.date = tmpObj.getString("date");
						itemInfo.price = (float)tmpObj.getDouble("price");
						itemInfo.type = tmpObj.getInt("type");
						itemInfo.customer_id = tmpObj.getInt("customer_id");
						itemInfo.customer_name = tmpObj.getString("customer_name");
						itemInfo.customer_phone = tmpObj.getString("customer_phone");
						
						m_MoneyPaymentList.add(itemInfo);
		            }
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			m_nResponse = ResponseRet.RET_JSON_EXCEPTION;
		}
	}

	public JSONObject makeRequestJSON() throws JSONException {
		JSONObject requestObj = new JSONObject();
		
		STMoneyPaymentInfo curItem = getItem(m_CurClickedItem - 1);

		requestObj.put("shop_id", String.valueOf(Global.Cur_ShopId));
		requestObj.put("uid", String.valueOf(Global.Cur_UserId));
		requestObj.put("payment_id", String.valueOf(curItem.payment_id));

		return requestObj;
	}

}
