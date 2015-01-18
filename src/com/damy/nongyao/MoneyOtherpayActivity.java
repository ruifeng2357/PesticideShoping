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
import com.damy.adapters.MoneyOtherpayAdapter;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;
import com.damy.datatypes.STMoneyOtherpayInfo;

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

public class MoneyOtherpayActivity extends BaseActivity {
	
	private enum REQ_TYPE{REQ_GETOTHERPAYLIST, REQ_DELOTHERPAYLOG};
	
	private PullToRefreshListView				m_lvMoneyOtherpayListView;
	private ArrayList<STMoneyOtherpayInfo> 		m_MoneyOtherpayList;
	private MoneyOtherpayAdapter				m_MoneyOtherpayAdapter = null;
	private ListView							mRealListView;

    private int							m_nCurPageNumber = 1;
    private int 						m_CurClickedItem = -1;
    
    private PopupWindow 				popup_editdel;
    private PopupWindow 				popup_delconfirm;
    private PopupWindow 				dialog_type;
	private PopupWindow 				dialog_datepicker;
	
    private LinearLayout				m_MaskLayer;
    private AutoSizeTextView 			txt_type;
    
    private REQ_TYPE					m_reqType;
    
    private int							m_nCurType = 2;
    
    private AutoSizeTextView            txt_moneyotherpay_startdate;
    private AutoSizeTextView            txt_moneyotherpay_enddate;
    
    private int								m_curSelDateType;
	private DatePicker						m_DatePicker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_money_otherpay);
		
		ResolutionSet._instance.iterateChild(findViewById(R.id.fl_moneyotherpay));
		
		initControls();
		setMoneyOtherpayAdapter();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		m_nCurPageNumber = 1;
		m_MoneyOtherpayList.clear();
		m_reqType = REQ_TYPE.REQ_GETOTHERPAYLIST;
		new LoadResponseThread(MoneyOtherpayActivity.this).start();
	}
	
	private void initControls()
	{
		FrameLayout fl_backbtn = (FrameLayout)findViewById(R.id.fl_moneyotherpay_backbtn);
		FrameLayout fl_homebtn = (FrameLayout)findViewById(R.id.fl_moneyotherpay_homebtn);
		FrameLayout fl_addbtn = (FrameLayout)findViewById(R.id.fl_moneyotherpay_addbtn);
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
		
		fl_addbtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickAdd();
        	}
        });
		
		LinearLayout ll_startdate = (LinearLayout)findViewById(R.id.ll_moneyotherpay_startdate);
		LinearLayout ll_enddate = (LinearLayout)findViewById(R.id.ll_moneyotherpay_enddate);
		
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
		
		m_MaskLayer = (LinearLayout)findViewById(R.id.ll_moneyotherpay_masklayer);
		m_MaskLayer.setVisibility(View.INVISIBLE);
		
		m_lvMoneyOtherpayListView = (PullToRefreshListView)findViewById(R.id.anMoneyOtherpayContentView);
		
		LinearLayout ll_typeselect = (LinearLayout)findViewById(R.id.ll_moneyotherpay_typeselect);
		ll_typeselect.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSelectType();
        	}
        });
		
		txt_type = (AutoSizeTextView)findViewById(R.id.txt_moneyotherpay_type);
		txt_type.setText(getResources().getString(R.string.common_all));
		
		View popupview = View.inflate(this, R.layout.dialog_select, null);
		ResolutionSet._instance.iterateChild(popupview);
		
		AutoSizeTextView txt_title = (AutoSizeTextView)popupview.findViewById(R.id.txt_dialog_title);
		txt_title.setText(getResources().getString(R.string.dialog_select_incomeoutgo));

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
		arGeneral.add(getResources().getString(R.string.common_outgo));
		arGeneral.add(getResources().getString(R.string.common_income));
		
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
		
		txt_moneyotherpay_startdate = (AutoSizeTextView)findViewById(R.id.txt_moneyotherpay_startdate);
		txt_moneyotherpay_enddate = (AutoSizeTextView)findViewById(R.id.txt_moneyotherpay_enddate);
		
		Date curDate = new Date();
		String strDate = String.valueOf(curDate.getYear() + 1900) + "-" + String.valueOf(curDate.getMonth() + 1) + "-" + String.valueOf(curDate.getDate());
		txt_moneyotherpay_enddate.setText(strDate);
		strDate = String.valueOf(curDate.getYear() + 1900) + "-" + String.valueOf(curDate.getMonth() + 1) + "-1";
		txt_moneyotherpay_startdate.setText(strDate);
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
		/*
		Intent other_activity = new Intent(this, MoneyOtherpayActivity.class);
		startActivity(other_activity);	
		finish();
		*/
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
		Intent add_activity = new Intent(this, MoneyOtherpayAddActivity.class);
		add_activity.putExtra(MoneyOtherpayAddActivity.MONEYOTHERPAY_ADD_ID, 0);
		startActivity(add_activity);
	}
	
	private void onClickSelectType()
	{
		m_MaskLayer.setVisibility(View.VISIBLE);
		dialog_type.showAtLocation(findViewById(R.id.ll_moneyotherpay_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
	}
	
	private void onClickTypeItem(int pos)
	{
		int oldType = m_nCurType;
		if ( pos == 0 )
		{
			m_nCurType = 2;
			txt_type.setText(getResources().getString(R.string.common_all));
		}
		else if ( pos == 1 )
		{
			m_nCurType = 0;
			txt_type.setText(getResources().getString(R.string.common_outgo));
		}
		else if ( pos == 2 )
		{
			m_nCurType = 1;
			txt_type.setText(getResources().getString(R.string.common_income));
		}
		
		m_MaskLayer.setVisibility(View.INVISIBLE);
		dialog_type.dismiss();
		
		if ( oldType != m_nCurType )
		{
			m_nCurPageNumber = 1;
			m_MoneyOtherpayList.clear();
			m_reqType = REQ_TYPE.REQ_GETOTHERPAYLIST;
			new LoadResponseThread(MoneyOtherpayActivity.this).start();
		}
	}
	
	private void onClickPopupEdit()
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		
		STMoneyOtherpayInfo curItem = getItem(m_CurClickedItem - 1);
		
		Intent add_activity = new Intent(this, MoneyOtherpayAddActivity.class);
		add_activity.putExtra(MoneyOtherpayAddActivity.MONEYOTHERPAY_ADD_ID, curItem.otherpay_id);
		add_activity.putExtra(MoneyOtherpayAddActivity.MONEYOTHERPAY_ADD_TYPE, curItem.type);
		add_activity.putExtra(MoneyOtherpayAddActivity.MONEYOTHERPAY_ADD_MONEY, curItem.price);
		add_activity.putExtra(MoneyOtherpayAddActivity.MONEYOTHERPAY_ADD_REASON, curItem.reason);
		startActivity(add_activity);
		
		popup_editdel.dismiss();
	}
	
	private void onClickPopupDel()
	{
		popup_editdel.dismiss();
		
		View popupview = View.inflate(this, R.layout.dialog_delconfirm, null);
		ResolutionSet._instance.iterateChild(popupview);
		
		popup_delconfirm = new PopupWindow(popupview, R.dimen.common_delconfirm_dialog_width, R.dimen.common_delconfirm_dialog_height,true);
		popup_delconfirm.setAnimationStyle(-1);
		
		popup_delconfirm.showAtLocation(findViewById(R.id.ll_moneyotherpay_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		
		AutoSizeTextView txt_delconfirm_msg = (AutoSizeTextView)popupview.findViewById(R.id.txt_dialog_delconfirm_msg);
		txt_delconfirm_msg.setText(getResources().getString(R.string.confirm_del_moneyotherpay));
		
		FrameLayout fl_delconfirm_ok = (FrameLayout)popupview.findViewById(R.id.fl_dialog_delconfirm_okbtn);
		FrameLayout fl_delconfirm_cancel = (FrameLayout)popupview.findViewById(R.id.fl_dialog_delconfirm_cancelbtn);
		
		fl_delconfirm_ok.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickDelConfirmOk();
        	}
        });
		fl_delconfirm_cancel.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickDelConfirmCancel();
        	}
        });
	}
	
	private void onClickPopupCancel()
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		popup_editdel.dismiss();
	}
	
	private void onClickDelConfirmOk()
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		popup_delconfirm.dismiss();
		
		m_reqType = REQ_TYPE.REQ_DELOTHERPAYLOG;
		new LoadResponseThread(MoneyOtherpayActivity.this).start();
	}
	
	private void onClickDelConfirmCancel()
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		popup_delconfirm.dismiss();
	}
	
	public STMoneyOtherpayInfo getItem(int position)
	{
		if (position < 0 || position >= m_MoneyOtherpayList.size())
			return null;
		
		return m_MoneyOtherpayList.get(position);
	}
	
	private void setMoneyOtherpayAdapter() {
		m_MoneyOtherpayList = new ArrayList<STMoneyOtherpayInfo>();
		m_lvMoneyOtherpayListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);

        // Set a listener to be invoked when the list should be refreshed.
		m_lvMoneyOtherpayListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                m_nCurPageNumber = m_nCurPageNumber + 1;
                m_reqType = REQ_TYPE.REQ_GETOTHERPAYLIST;
                new LoadResponseThread(MoneyOtherpayActivity.this).start();
            }
        });

        mRealListView = m_lvMoneyOtherpayListView.getRefreshableView();
        registerForContextMenu(mRealListView);

        //mRealListView.setDivider(new ColorDrawable(Color.TRANSPARENT));
        mRealListView.setCacheColorHint(Color.TRANSPARENT);
        mRealListView.setDividerHeight(0);
        //mRealListView.setDrawSelectorOnTop(true);

        m_MoneyOtherpayAdapter = new MoneyOtherpayAdapter(MoneyOtherpayActivity.this, m_MoneyOtherpayList);
        mRealListView.setAdapter(m_MoneyOtherpayAdapter);
        
        /*mRealListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				onLongClickItem(parent, position);
				return true;
        	}
		});*/
	}
	/*
	private void onLongClickItem(View view, int position)
	{
		m_CurClickedItem = position;
		
		m_MaskLayer.setVisibility(View.VISIBLE);
		
		View popupview = View.inflate(this, R.layout.dialog_editdel, null);
		ResolutionSet._instance.iterateChild(popupview);
		
		popup_editdel = new PopupWindow(popupview, R.dimen.common_popup_dialog_width, R.dimen.common_popup_dialog_height,true);
		popup_editdel.setAnimationStyle(-1);
		
		popup_editdel.showAtLocation(view, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		
		FrameLayout fl_popup_edit = (FrameLayout)popupview.findViewById(R.id.fl_popup_editdel_editbtn);
		fl_popup_edit.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickPopupEdit();
        	}
        });
		FrameLayout fl_popup_del = (FrameLayout)popupview.findViewById(R.id.fl_popup_editdel_delbtn);
		fl_popup_del.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickPopupDel();
        	}
        });
		FrameLayout fl_popup_cancel = (FrameLayout)popupview.findViewById(R.id.fl_popup_editdel_cancelbtn);
		fl_popup_cancel.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickPopupCancel();
        	}
        });
	}
	*/
	private void onClickStartDate()
	{
		m_curSelDateType = 1;
		String strDate = txt_moneyotherpay_startdate.getText().toString();
		String[] dateStrArr = new String[3];
		dateStrArr = strDate.split("-");
		m_DatePicker.updateDate(Integer.parseInt(dateStrArr[0]), Integer.parseInt(dateStrArr[1]) - 1, Integer.parseInt(dateStrArr[2]));
		dialog_datepicker.showAtLocation(findViewById(R.id.ll_moneyotherpay_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		m_MaskLayer.setVisibility(View.VISIBLE);
	}
	
	private void onClickEndDate()
	{
		m_curSelDateType = 2;
		String strDate = txt_moneyotherpay_enddate.getText().toString();
		String[] dateStrArr = new String[3];
		dateStrArr = strDate.split("-");
		m_DatePicker.updateDate(Integer.parseInt(dateStrArr[0]), Integer.parseInt(dateStrArr[1]) - 1, Integer.parseInt(dateStrArr[2]));
		dialog_datepicker.showAtLocation(findViewById(R.id.ll_moneyotherpay_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		m_MaskLayer.setVisibility(View.VISIBLE);
	}
	
	private void onClickDatePopupOk()
	{
		dialog_datepicker.dismiss();
		String strDate = String.valueOf(m_DatePicker.getYear()) + "-" + String.valueOf(m_DatePicker.getMonth() + 1) + "-" + String.valueOf(m_DatePicker.getDayOfMonth());
		if ( m_curSelDateType == 1 )
		{
			if (!strDate.equals(txt_moneyotherpay_startdate.getText()))
			{
				txt_moneyotherpay_startdate.setText(strDate);
				m_nCurPageNumber = 1;
				m_MoneyOtherpayList.clear();
				m_reqType = REQ_TYPE.REQ_GETOTHERPAYLIST;
				new LoadResponseThread(MoneyOtherpayActivity.this).start();
			}
		}
		else
		{
			if (!strDate.equals(txt_moneyotherpay_enddate.getText()))
			{
				if ( strDate.compareTo(txt_moneyotherpay_startdate.getText().toString()) < 0 )
					showToastMessage(getResources().getString(R.string.error_date_startend));
				else
				{
					txt_moneyotherpay_enddate.setText(strDate);
					m_nCurPageNumber = 1;
					m_MoneyOtherpayList.clear();
					m_reqType = REQ_TYPE.REQ_GETOTHERPAYLIST;
					new LoadResponseThread(MoneyOtherpayActivity.this).start();
				}
			}
		}
		
		m_MaskLayer.setVisibility(View.INVISIBLE);		
		
	}
	
	private void onClickDatePopupCancel()
	{
		dialog_datepicker.dismiss();
		m_MaskLayer.setVisibility(View.INVISIBLE);
	}
	
	public void refreshUI() {
		super.refreshUI();
		
		if ( m_reqType == REQ_TYPE.REQ_GETOTHERPAYLIST )
		{
			m_MoneyOtherpayAdapter.notifyDataSetChanged();
			m_lvMoneyOtherpayListView.onRefreshComplete();
		}
		else if ( m_reqType == REQ_TYPE.REQ_DELOTHERPAYLOG )
		{
			m_MoneyOtherpayList.clear();
			m_reqType = REQ_TYPE.REQ_GETOTHERPAYLIST;
			m_nCurPageNumber = 1;
			new LoadResponseThread(MoneyOtherpayActivity.this).start();
		}
		
	}
	
	public void getResponseJSON() {
		try {
			if ( m_reqType == REQ_TYPE.REQ_GETOTHERPAYLIST )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETOTHERPAYLIST;
				strRequest += "?shop_id=" + String.valueOf(Global.Cur_ShopId);				
				strRequest += "&type=" + Integer.toString(m_nCurType);
				strRequest += "&start_date=" + txt_moneyotherpay_startdate.getText().toString();
				strRequest += "&end_date=" + txt_moneyotherpay_enddate.getText().toString();
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
			            STMoneyOtherpayInfo itemInfo = new STMoneyOtherpayInfo();
	
						itemInfo.otherpay_id = tmpObj.getInt("otherpay_id");
						itemInfo.date = tmpObj.getString("date");
						itemInfo.price = (float)tmpObj.getDouble("price");
						itemInfo.type = tmpObj.getInt("type");
						itemInfo.reason = tmpObj.getString("reason");
						
						m_MoneyOtherpayList.add(itemInfo);
		            }
				}
			}
			else if ( m_reqType == REQ_TYPE.REQ_DELOTHERPAYLOG )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_DELOTHERPAYLOG;
				
				JSONObject response = m_HttpConnUsingJSON.getPostJSONObject(strRequest);
				if (response == null) {
					m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
					return;
				}
	
				m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			m_nResponse = ResponseRet.RET_JSON_EXCEPTION;
		}
	}

	public JSONObject makeRequestJSON() throws JSONException {
		JSONObject requestObj = new JSONObject();
		
		STMoneyOtherpayInfo curItem = getItem(m_CurClickedItem - 1);

		requestObj.put("shop_id", String.valueOf(Global.Cur_ShopId));
		requestObj.put("uid", String.valueOf(Global.Cur_UserId));
		requestObj.put("otherpay_id", String.valueOf(curItem.otherpay_id));

		return requestObj;
	}
	

}
