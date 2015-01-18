package com.damy.nongyao;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.damy.adapters.DialogSelectAdapter;
import com.damy.backend.LoadResponseThread;
import com.damy.Utils.AutoSizeEditText;
import com.damy.Utils.AutoSizeTextView;
import com.damy.Utils.ResolutionSet;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;
import com.damy.datatypes.STCustomerInfo;
import com.damy.datatypes.STRegionInfo;

import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

public class MoneyPaymentAddActivity extends BaseActivity {
	
	private enum REQ_TYPE{REQ_GETCUSTOMERLIST, REQ_ADDPAYMENTLOG};
	
	private long						m_CurSelCustomerId = 0;
	private int 						m_CurSelType = 0;
	private int 						m_CurSelPaytype = 0;
	
	private AutoSizeTextView 			txt_CustomerName;
	private AutoSizeTextView 			txt_CustomerPhone;
	private AutoSizeTextView 			txt_Type;
	private AutoSizeTextView 			txt_Paytype;
	private AutoSizeEditText 			edit_Price;
	private AutoSizeEditText 			edit_Change;
	private AutoSizeEditText 			edit_Remark;
	private AutoSizeTextView 			txt_Operator;
	
	private PopupWindow 				dialog_customer;
	private PopupWindow 				dialog_type;
	private PopupWindow 				dialog_paytype;
    private LinearLayout				m_MaskLayer;
	
	private REQ_TYPE 					m_reqType;
	
	private ArrayList<STCustomerInfo> 	m_CustomerList = new ArrayList<STCustomerInfo>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_money_payment_add);
		
		ResolutionSet._instance.iterateChild(findViewById(R.id.fl_moneypayment_add));
		
		initControls();
		readContent();
	}
	
	private void initControls()
	{
		FrameLayout fl_backbtn = (FrameLayout)findViewById(R.id.fl_moneypayment_add_backbtn);
		FrameLayout fl_homebtn = (FrameLayout)findViewById(R.id.fl_moneypayment_add_homebtn);
		FrameLayout fl_savebtn = (FrameLayout)findViewById(R.id.fl_moneypayment_add_savebtn);
		FrameLayout fl_closebtn = (FrameLayout)findViewById(R.id.fl_moneypayment_add_closebtn);

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
		
		fl_savebtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSave();
        	}
        });
		
		fl_closebtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickClose();
        	}
        });
		
		m_MaskLayer = (LinearLayout)findViewById(R.id.ll_moneypayment_add_masklayer);
		m_MaskLayer.setVisibility(View.INVISIBLE);
		
		txt_CustomerName = (AutoSizeTextView)findViewById(R.id.txt_moneyotherpay_add_customername);
		txt_CustomerPhone = (AutoSizeTextView)findViewById(R.id.txt_moneypayment_add_connectmethod);
		txt_Type = (AutoSizeTextView)findViewById(R.id.txt_moneypayment_add_type);
		txt_Paytype = (AutoSizeTextView)findViewById(R.id.txt_moneypayment_add_paytype);
		edit_Price = (AutoSizeEditText)findViewById(R.id.edit_moneypayment_add_money);
		edit_Change = (AutoSizeEditText)findViewById(R.id.edit_moneypayment_add_restmoney);
		edit_Remark = (AutoSizeEditText)findViewById(R.id.edit_moneypayment_add_remark);
		txt_Operator = (AutoSizeTextView)findViewById(R.id.txt_moneypayment_add_operator);
		
		LinearLayout ll_customerselect = (LinearLayout)findViewById(R.id.ll_moneypayment_add_customerselect);
		LinearLayout ll_typeselect = (LinearLayout)findViewById(R.id.ll_moneypayment_add_typeselect);
		LinearLayout ll_paytypeselect = (LinearLayout)findViewById(R.id.ll_moneypayment_add_paytypeselect);
		
		ll_customerselect.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSelectCustomer();
        	}
        });
		
		ll_typeselect.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSelectType();
        	}
        });
		
		ll_paytypeselect.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSelectPaytype();
        	}
        });
		
		setDialogTypeAdapter();
		setDialogPaytypeAdapter();
		
		txt_Operator.setText(Global.Cur_UserName);
	}
	
	private void onClickBack()
	{
		finish();
	}
	
	private void onClickHome()
	{
		Intent main_activity = new Intent(this, MainActivity.class);
		startActivity(main_activity);	
		finish();
	}
	
	private void onClickSave()
	{
		if ( edit_Price.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_required_money));
			return;
		}
		
		if ( Float.valueOf(edit_Price.getText().toString()) == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_zeromoney));
			return;
		}
		
		if ( edit_Change.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_required_restmoney));
			return;
		}
		
		if ( edit_Remark.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_required_remark));
			return;
		}
		
		m_reqType = REQ_TYPE.REQ_ADDPAYMENTLOG;
		new LoadResponseThread(MoneyPaymentAddActivity.this).start();
	}
	
	private void onClickClose()
	{
		finish();
	}
	
	private void readContent()
	{
		m_reqType = REQ_TYPE.REQ_GETCUSTOMERLIST;
		new LoadResponseThread(MoneyPaymentAddActivity.this).start();
	}
	
	private void onClickSelectCustomer()
	{
		if ( m_CustomerList.size() <= 0 )
			return;
		
		m_MaskLayer.setVisibility(View.VISIBLE);
		dialog_customer.showAtLocation(findViewById(R.id.ll_moneypayment_add_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
	}
	
	private void onClickSelectType()
	{
		m_MaskLayer.setVisibility(View.VISIBLE);
		dialog_type.showAtLocation(findViewById(R.id.ll_moneypayment_add_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
	}
	
	private void onClickSelectPaytype()
	{
		m_MaskLayer.setVisibility(View.VISIBLE);
		dialog_paytype.showAtLocation(findViewById(R.id.ll_moneypayment_add_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
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
		
		txt_Type.setText(getResources().getString(R.string.common_realtake));
	}
	
	private void setDialogPaytypeAdapter()
	{
		View popupview = View.inflate(this, R.layout.dialog_select, null);
		ResolutionSet._instance.iterateChild(popupview);
		
		AutoSizeTextView txt_title = (AutoSizeTextView)popupview.findViewById(R.id.txt_dialog_title);
		txt_title.setText(getResources().getString(R.string.dialog_select_paymentpaytype));

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
		
		ArrayList<String> arGeneral = new ArrayList<String>();
		
		arGeneral.add(getResources().getString(R.string.common_actual));
		arGeneral.add(getResources().getString(R.string.common_bankmoney));
		
		DialogSelectAdapter Adapter = new DialogSelectAdapter(this, arGeneral);
		
		ListView list = (ListView)popupview.findViewById(R.id.lv_dialog_listview);
		list.setAdapter(Adapter);
		list.setDrawSelectorOnTop(true);
		list.setDivider(new ColorDrawable(getResources().getColor(R.color.dialog_line)));
		list.setCacheColorHint(Color.TRANSPARENT);
        list.setDividerHeight(1);
		
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onClickPaytypeItem(position);
        	}
		});
		
		dialog_paytype = new PopupWindow(popupview, R.dimen.common_popup_dialog_width, R.dimen.common_popup_dialog_height,true);
		dialog_paytype.setAnimationStyle(-1);
		
		txt_Paytype.setText(getResources().getString(R.string.common_actual));
	}
	
	private void setDialogCustomerAdapter()
	{
		View popupview = View.inflate(this, R.layout.dialog_select, null);
		ResolutionSet._instance.iterateChild(popupview);
		
		AutoSizeTextView txt_title = (AutoSizeTextView)popupview.findViewById(R.id.txt_dialog_title);
		txt_title.setText(getResources().getString(R.string.dialog_select_givetakemoney));

        ImageView img_cancel = (ImageView) popupview.findViewById(R.id.img_dialog_cancel);
        img_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog_customer != null && dialog_customer.isShowing() )
                {
                    m_MaskLayer.setVisibility(View.INVISIBLE);
                    dialog_customer.dismiss();
                }
            }
        });
		
		ArrayList<String> arGeneral = new ArrayList<String>();
		
		int cnt = m_CustomerList.size();
		for ( int i = 0; i < cnt; i++ )
			arGeneral.add(m_CustomerList.get(i).name);
		
		DialogSelectAdapter Adapter = new DialogSelectAdapter(this, arGeneral);
		
		ListView list = (ListView)popupview.findViewById(R.id.lv_dialog_listview);
		list.setAdapter(Adapter);
		list.setDrawSelectorOnTop(true);
		list.setDivider(new ColorDrawable(getResources().getColor(R.color.dialog_line)));
		list.setCacheColorHint(Color.TRANSPARENT);
        list.setDividerHeight(1);
		
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onClickCustomerItem(position);
        	}
		});
		
		dialog_customer = new PopupWindow(popupview, R.dimen.common_popup_dialog_width, R.dimen.common_popup_dialog_height,true);
		dialog_customer.setAnimationStyle(-1);
		
		if ( m_CustomerList.size() > 0 )
		{
			txt_CustomerName.setText(m_CustomerList.get(0).name);
			txt_CustomerPhone.setText(m_CustomerList.get(0).phone);
			
			m_CurSelCustomerId = m_CustomerList.get(0).customer_id;
		}
		else
		{
			txt_CustomerName.setText("");
			txt_CustomerPhone.setText("");
		}
	}
	
	private void onClickCustomerItem(int pos)
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		dialog_customer.dismiss();
		
		txt_CustomerName.setText(m_CustomerList.get(pos).name);
		txt_CustomerPhone.setText(m_CustomerList.get(pos).phone);
		
		m_CurSelCustomerId = m_CustomerList.get(pos).customer_id;
	}
	
	private void onClickTypeItem(int pos)
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		dialog_type.dismiss();
		
		txt_Type.setText(pos == 0 ? getResources().getString(R.string.common_realtake) : getResources().getString(R.string.common_realgive));
		
		if ( pos == 0 )
			m_CurSelType = 1;
		else
			m_CurSelType = 0;
	}
	
	private void onClickPaytypeItem(int pos)
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		dialog_paytype.dismiss();
		
		txt_Paytype.setText(pos == 0 ? getResources().getString(R.string.common_actual) : getResources().getString(R.string.common_bankmoney));
		
		if ( pos == 0 )
			m_CurSelPaytype = 0;
		else
			m_CurSelPaytype = 2;
	}
	
	public void refreshUI() {
		super.refreshUI();
		
		if ( m_reqType == REQ_TYPE.REQ_GETCUSTOMERLIST )
		{
			setDialogCustomerAdapter();
		}
		else if ( m_reqType == REQ_TYPE.REQ_ADDPAYMENTLOG )
		{
			if ( m_nResponse == ResponseRet.RET_SUCCESS )
				finish();
		}
	}
	
	public void getResponseJSON() {
		try {
			if ( m_reqType == REQ_TYPE.REQ_GETCUSTOMERLIST )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strUrl = HttpConnUsingJSON.REQ_GETCUSTOMERLIST;
				strUrl += "?shop_id=" + String.valueOf(Global.Cur_ShopId);
				
				
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
						STCustomerInfo itemInfo = new STCustomerInfo();
						JSONObject tmpObj = dataList.getJSONObject(i);
						
						itemInfo.customer_id = tmpObj.getInt("customer_id");
						itemInfo.name = tmpObj.getString("name");
						itemInfo.phone = tmpObj.getString("phone");
						
						m_CustomerList.add(itemInfo);
					}
				}
			}
			else if ( m_reqType == REQ_TYPE.REQ_ADDPAYMENTLOG )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strUrl = HttpConnUsingJSON.REQ_ADDPAYMENTLOG;
				
				JSONObject response = m_HttpConnUsingJSON.getPostJSONObject(strUrl);
				if (response == null) {
					m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
					return;
				}
				m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
	            if (m_nResponse == ResponseRet.RET_SUCCESS) {
	            	
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			m_nResponse = ResponseRet.RET_JSON_EXCEPTION;
		}
	}

	public JSONObject makeRequestJSON() throws JSONException {
		JSONObject requestObj = new JSONObject();
		
		requestObj.put("shop_id", String.valueOf(Global.Cur_ShopId));
		requestObj.put("uid", String.valueOf(Global.Cur_UserId));
		requestObj.put("customer_id", String.valueOf(m_CurSelCustomerId));
		requestObj.put("price", edit_Price.getText().toString());
		requestObj.put("type", Integer.toString(m_CurSelType));
		requestObj.put("paytype", Integer.toString(m_CurSelPaytype));
		requestObj.put("change", String.valueOf(edit_Change.getText().toString()));
		requestObj.put("etc", edit_Remark.getText().toString());
		
		return requestObj;
	}

}
