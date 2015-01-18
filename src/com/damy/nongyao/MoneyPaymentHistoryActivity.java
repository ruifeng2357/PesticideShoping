package com.damy.nongyao;

import java.util.ArrayList;
import java.util.Date;

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
import com.damy.adapters.MoneyPaymentHistoryAdapter;
import com.damy.adapters.MoneyReportAdapter;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;
import com.damy.datatypes.STMoneyPaymentHistoryInfo;
import com.damy.datatypes.STMoneyPaymentInfo;
import com.damy.datatypes.STCustomerInfo;
import com.damy.datatypes.STMoneyReportInfo;
import com.damy.datatypes.STRegionInfo;
import com.damy.datatypes.STShopInfo;


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

public class MoneyPaymentHistoryActivity extends BaseActivity {
	
	private enum REQ_TYPE{REQ_REALPAYMENTLIST};
	
	private PullToRefreshListView					m_lvMoneyPaymentHistoryListView;
	private ArrayList<STMoneyPaymentHistoryInfo> 	m_MoneyPaymentHistoryList;
	private MoneyPaymentHistoryAdapter				m_MoneyPaymentHistoryAdapter = null;
	private ListView								mRealListView;

    private int									m_nCurPageNumber = 1;
	private PopupWindow 						dialog_type;
	private PopupWindow 				        dialog_datepicker;
    private LinearLayout						m_MaskLayer;
    
    private REQ_TYPE							m_reqType;
    private int									m_CurType = 2;

    private AutoSizeEditText					txt_CustomerName;
    private AutoSizeTextView					txt_Type;
    
    private AutoSizeTextView                    txt_moneypayment_startdate;
    private AutoSizeTextView                    txt_moneypayment_enddate;
    
    private int								    m_curSelDateType;
	private DatePicker						    m_DatePicker;
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_money_payment_history);
		
		ResolutionSet._instance.iterateChild(findViewById(R.id.fl_moneypayment_history));
		
		initControls();
		setMoneyPaymentHistoryAdapter();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
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
		FrameLayout fl_backbtn = (FrameLayout)findViewById(R.id.fl_moneypayment_history_backbtn);
		FrameLayout fl_homebtn = (FrameLayout)findViewById(R.id.fl_moneypayment_history_homebtn);
		FrameLayout fl_searchbtn = (FrameLayout)findViewById(R.id.fl_moneypayment_history_searchbtn);
		FrameLayout fl_closebtn = (FrameLayout)findViewById(R.id.fl_moneypayment_history_closebtn);
		
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
		
		fl_searchbtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSearch();
        	}
        });
		
		fl_closebtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickClose();
        	}
        });
		
		
		m_MaskLayer = (LinearLayout)findViewById(R.id.ll_moneypayment_history_masklayer);
		m_MaskLayer.setVisibility(View.INVISIBLE);
		
		m_lvMoneyPaymentHistoryListView = (PullToRefreshListView)findViewById(R.id.anMoneyPaymentHistoryContentView);
		
		LinearLayout txt_typesel = (LinearLayout)findViewById(R.id.ll_moneypayment_history_typeselect);
		
		txt_typesel.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSelectType();
        	}
        });
		
		txt_CustomerName = (AutoSizeEditText)findViewById(R.id.edit_moneypayment_history_customer);
		txt_Type = (AutoSizeTextView)findViewById(R.id.txt_moneypayment_history_type);
		
		txt_Type.setText(getResources().getString(R.string.common_realtake));
		
		setDialogTypeAdapter();
		
		txt_moneypayment_startdate = (AutoSizeTextView)findViewById(R.id.txt_moneypayment_history_startdate);
		txt_moneypayment_enddate = (AutoSizeTextView)findViewById(R.id.txt_moneypayment_history_enddate);
		
		Date curDate = new Date();
		String strDate = String.valueOf(curDate.getYear() + 1900) + "-" + String.valueOf(curDate.getMonth() + 1) + "-" + String.valueOf(curDate.getDate());
		txt_moneypayment_enddate.setText(strDate);
		strDate = String.valueOf(curDate.getYear() + 1900) + "-" + String.valueOf(curDate.getMonth() + 1) + "-1";
		txt_moneypayment_startdate.setText(strDate);
		
		LinearLayout ll_startdate = (LinearLayout)findViewById(R.id.ll_moneypayment_history_startdate);
		LinearLayout ll_enddate = (LinearLayout)findViewById(R.id.ll_moneypayment_history_enddate);
		
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
		
	}
	
	private void onClickStartDate()
	{
		m_curSelDateType = 1;
		String strDate = txt_moneypayment_startdate.getText().toString();
		String[] dateStrArr = new String[3];
		dateStrArr = strDate.split("-");
		m_DatePicker.updateDate(Integer.parseInt(dateStrArr[0]), Integer.parseInt(dateStrArr[1]) - 1, Integer.parseInt(dateStrArr[2]));
		dialog_datepicker.showAtLocation(findViewById(R.id.ll_moneypayment_history_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		m_MaskLayer.setVisibility(View.VISIBLE);
	}
	
	private void onClickEndDate()
	{
		m_curSelDateType = 2;
		String strDate = txt_moneypayment_enddate.getText().toString();
		String[] dateStrArr = new String[3];
		dateStrArr = strDate.split("-");
		m_DatePicker.updateDate(Integer.parseInt(dateStrArr[0]), Integer.parseInt(dateStrArr[1]) - 1, Integer.parseInt(dateStrArr[2]));
		dialog_datepicker.showAtLocation(findViewById(R.id.ll_moneypayment_history_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
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
	
	private void onClickBack()
	{
		onClickHome();
	}
	
	private void onClickHome()
	{
		Intent main_activity = new Intent(this, MainActivity.class);
		startActivity(main_activity);	
		finish();
	}
	
	private void onClickSearch()
	{
		m_nCurPageNumber = 1;
		
		m_MoneyPaymentHistoryList.clear();
		
        m_reqType = REQ_TYPE.REQ_REALPAYMENTLIST;
        new LoadResponseThread(MoneyPaymentHistoryActivity.this).start();
	}
	
	private void onClickClose()
	{
		Intent moneypayment_activity = new Intent(this, MoneyPaymentActivity.class);
		startActivity(moneypayment_activity);	
		finish();
	}
	
	
	public STMoneyPaymentHistoryInfo getItem(int position)
	{
		if (position < 0 || position >= m_MoneyPaymentHistoryList.size())
			return null;
		
		return m_MoneyPaymentHistoryList.get(position);
	}
	
	private void setMoneyPaymentHistoryAdapter() {
		m_MoneyPaymentHistoryList = new ArrayList<STMoneyPaymentHistoryInfo>();
		m_lvMoneyPaymentHistoryListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);

        // Set a listener to be invoked when the list should be refreshed.
		m_lvMoneyPaymentHistoryListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                m_nCurPageNumber = m_nCurPageNumber + 1;
                m_reqType = REQ_TYPE.REQ_REALPAYMENTLIST;
                new LoadResponseThread(MoneyPaymentHistoryActivity.this).start();
            }
        });

        mRealListView = m_lvMoneyPaymentHistoryListView.getRefreshableView();
        registerForContextMenu(mRealListView);

        mRealListView.setCacheColorHint(Color.TRANSPARENT);
        mRealListView.setDividerHeight(0);
        //mRealListView.setDrawSelectorOnTop(true);

        m_MoneyPaymentHistoryAdapter = new MoneyPaymentHistoryAdapter(MoneyPaymentHistoryActivity.this, m_MoneyPaymentHistoryList);
        mRealListView.setAdapter(m_MoneyPaymentHistoryAdapter);
	}
	
	private void onClickSelectType()
	{
		m_MaskLayer.setVisibility(View.VISIBLE);
		dialog_type.showAtLocation(findViewById(R.id.ll_moneypayment_history_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
	}
	
	private void setDialogTypeAdapter()
	{
		View popupview = View.inflate(this, R.layout.dialog_select, null);
		ResolutionSet._instance.iterateChild(popupview);
		
		AutoSizeTextView txt_title = (AutoSizeTextView)popupview.findViewById(R.id.txt_dialog_title);
		txt_title.setText(getResources().getString(R.string.dialog_select_realgivetakemoney));

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
		
		arGeneral.add(getResources().getString(R.string.common_realtake));
		arGeneral.add(getResources().getString(R.string.common_realgive));
		
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
			strType = getResources().getString(R.string.common_realtake);
			m_CurType = 1;
		}
		else
		{
			strType = getResources().getString(R.string.common_realgive);
			m_CurType = 0;
		}
		
		txt_Type.setText(strType);		
		
	}
	
	
	public void refreshUI() {
		super.refreshUI();
		
		if ( m_reqType == REQ_TYPE.REQ_REALPAYMENTLIST )
		{
			m_MoneyPaymentHistoryAdapter.notifyDataSetChanged();
			m_lvMoneyPaymentHistoryListView.onRefreshComplete();
		}
	}
	
	public void getResponseJSON() {
		try {
			if ( m_reqType == REQ_TYPE.REQ_REALPAYMENTLIST )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strUrl = HttpConnUsingJSON.REQ_REALPAYMENTLIST;
				strUrl += "?shop_id=" + String.valueOf(Global.Cur_ShopId);
				strUrl += "&customer_name=" + EncodeToUTF8(txt_CustomerName.getText().toString());
				strUrl += "&type=" + String.valueOf(m_CurType);
				strUrl += "&start_date=" + String.valueOf(txt_moneypayment_startdate.getText().toString());
				strUrl += "&end_date=" + String.valueOf(txt_moneypayment_enddate.getText().toString());
				strUrl += "&pagenum=" + String.valueOf(m_nCurPageNumber);
				
				JSONObject response = m_HttpConnUsingJSON.getGetJSONObject(strUrl);
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
						STMoneyPaymentHistoryInfo itemInfo = new STMoneyPaymentHistoryInfo();
						JSONObject tmpObj = dataList.getJSONObject(i);
						
						itemInfo.date = tmpObj.getString("date");
						itemInfo.customer_name = tmpObj.getString("customer_name");
						itemInfo.type = tmpObj.getInt("type");
						itemInfo.price = tmpObj.getDouble("price");
						itemInfo.change = tmpObj.getDouble("change");
						itemInfo.remark = tmpObj.getString("etc");
						
						m_MoneyPaymentHistoryList.add(itemInfo);
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
