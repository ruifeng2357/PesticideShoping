package com.damy.nongyao;

import java.util.ArrayList;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;
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
import com.damy.datatypes.STCatalogInfo;
import com.damy.datatypes.STStandardInfo;
import com.damy.datatypes.STStoreInfo;
import com.google.zxing.client.android.CaptureActivity;

import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;

public class SaleRejectAddActivity extends BaseActivity {
	private enum REQ_TYPE{REQ_GETREMAINCATALOGSTANDARDLIST, REQ_GETLARGENUMBERLIST, REQ_GETCATALOGINFOFROMBARCODE};
	
	private final static int			REQUEST_BARCODE = 0;
	
	private AutoSizeEditText			txt_barcode;
	//private AutoSizeTextView			txt_barcode;
	private AutoSizeTextView 			txt_catalogname;
	private AutoSizeTextView 			txt_standard;
	private AutoSizeTextView 			txt_largenumber;
	private AutoSizeEditText 			edit_oneprice;
	private AutoSizeEditText 			edit_quantity;
	private AutoSizeTextView 			txt_totalprice;
	
	private PopupWindow 				dialog_standard;
	private PopupWindow 				dialog_largenumber;
    
	private LinearLayout 				m_MaskLayer;
	
	private long						m_CatalogId = -1;
	private String						m_CatalogName = "";
	private int							m_StandardPos = -1;
    private int							m_LargenumberPos = -1;
	
	private ArrayList<STStandardInfo>	m_StandardList = new ArrayList<STStandardInfo>();
    private ArrayList<String>			m_LargenumberList = new ArrayList<String>();
    
    private REQ_TYPE 					m_reqType;

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override
        public void afterTextChanged(Editable s) {
            onKeyDownMoney();
        }
    };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sale_reject_add);
		
		ResolutionSet._instance.iterateChild(findViewById(R.id.fl_salereject_add));
		
		initControls();
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
            case REQUEST_BARCODE:
            	if ( data != null )
            	{
					String strReturn = data.getStringExtra(CaptureActivity.RESULTSTR);
					txt_barcode.setText(strReturn);
					
					m_reqType = REQ_TYPE.REQ_GETCATALOGINFOFROMBARCODE;
					new LoadResponseThread(SaleRejectAddActivity.this).start();
            	}
                return;
        }
    }
	
	private void initControls()
	{
		txt_barcode = (AutoSizeEditText)findViewById(R.id.txt_salereject_add_barcode);
		//txt_barcode = (AutoSizeTextView)findViewById(R.id.txt_salereject_add_barcode);
		txt_catalogname = (AutoSizeTextView)findViewById(R.id.txt_salereject_add_catalogname);
		txt_standard = (AutoSizeTextView)findViewById(R.id.txt_salereject_add_standard);
		txt_largenumber = (AutoSizeTextView)findViewById(R.id.txt_salereject_add_largenumber);
		edit_oneprice = (AutoSizeEditText)findViewById(R.id.edit_salereject_add_oneprice);
		edit_quantity = (AutoSizeEditText)findViewById(R.id.edit_salereject_add_amount);
		txt_totalprice = (AutoSizeTextView)findViewById(R.id.txt_salereject_add_totalprice);
		
//		edit_oneprice.setOnKeyListener( new OnKeyListener(){
//			@Override
//			public boolean onKey(View v, int keyCode, KeyEvent event) {
//				onKeyDownMoney();
//				return false;
//			}
//		});
        edit_oneprice.addTextChangedListener(watcher);

//		edit_quantity.setOnKeyListener( new OnKeyListener(){
//			@Override
//			public boolean onKey(View v, int keyCode, KeyEvent event) {
//				onKeyDownMoney();
//				return false;
//			}
//		});
        edit_quantity.addTextChangedListener(watcher);
		
		FrameLayout fl_backbtn = (FrameLayout)findViewById(R.id.fl_salereject_add_backbtn);
		FrameLayout fl_savebtn = (FrameLayout)findViewById(R.id.fl_salereject_add_savebtn);
		FrameLayout fl_closebtn = (FrameLayout)findViewById(R.id.fl_salereject_add_closebtn);
	
		fl_backbtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickBack();
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
		
		LinearLayout ll_standardselect = (LinearLayout)findViewById(R.id.ll_salereject_add_standardselect);
		ll_standardselect.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickStandardSelect();
        	}
        });
		
		LinearLayout ll_largenumberselect = (LinearLayout)findViewById(R.id.ll_salereject_add_largenumber);
		ll_largenumberselect.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickLargenumberSelect();
        	}
        });
		
		m_MaskLayer = (LinearLayout)findViewById(R.id.ll_salereject_add_masklayer);
		m_MaskLayer.setVisibility(View.INVISIBLE);
		
		FrameLayout fl_barcodebtn = (FrameLayout)findViewById(R.id.fl_salereject_add_barcodebtn);
		fl_barcodebtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickBarCode();
        	}
        });
	}
	
	private void onClickBack()
	{
		finish();
	}
	
	private void onClickSave()
	{
		if ( txt_catalogname.getText().toString().length() <= 0 )
		{
			showToastMessage(getResources().getString(R.string.error_required_catalog1));
			return;
		}
		
		if ( txt_standard.getText().toString().length() <= 0 )
		{
			showToastMessage(getResources().getString(R.string.error_required_standard));
			return;
		}
		
		if ( txt_largenumber.getText().toString().length() <= 0 )
		{
			showToastMessage(getResources().getString(R.string.error_required_largenumber));
			return;
		}
		
		if ( edit_quantity.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_required_quantity));
			return;
		}
		
		if ( Integer.valueOf(edit_quantity.getText().toString()) == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_zeroquantity));
			return;
		}
		
		if ( edit_oneprice.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_required_oneprice));
			return;
		}
		
		Global.SaleReject_isSelected = true;
		Global.SaleReject_SelectItem.catalog_id = m_CatalogId;
		Global.SaleReject_SelectItem.catalog_name = m_CatalogName;
		Global.SaleReject_SelectItem.standard_id = m_StandardList.get(m_StandardPos).standard_id;
		Global.SaleReject_SelectItem.standard_name = m_StandardList.get(m_StandardPos).standard;
		Global.SaleReject_SelectItem.largenumber = m_LargenumberList.get(m_LargenumberPos);
		Global.SaleReject_SelectItem.oneprice = Double.valueOf(edit_oneprice.getText().toString());
		Global.SaleReject_SelectItem.quantity = Integer.valueOf(edit_quantity.getText().toString());
		Global.SaleReject_SelectItem.totalprice = Double.valueOf(txt_totalprice.getText().toString());
		
		finish();
	}
	
	private void onClickClose()
	{
		finish();
	}
	
	private void onClickBarCode()
	{
		
		Intent intent = new Intent(SaleRejectAddActivity.this, CaptureActivity.class);
		startActivityForResult(intent, REQUEST_BARCODE);

        /*
		m_reqType = REQ_TYPE.REQ_GETCATALOGINFOFROMBARCODE;
		new LoadResponseThread(SaleRejectAddActivity.this).start();
		*/
	}
	
	private void onKeyDownMoney()
	{
		double price = 0;
		int count = 0;	
		String p = edit_oneprice.getText().toString();
		String c = edit_quantity.getText().toString();
		
		if(!p.equals("") && !c.equals("")){
			try{
				price = Double.valueOf(p);
				count = Integer.parseInt(c);
				
				double total = price * count;
				String a = String.format("%.2f", total);
				
				txt_totalprice.setText(a);	
			}catch (Exception e) {
		      	e.printStackTrace();        	
		    }
		}
	}
	
	private void onClickStandardSelect()
	{
		if ( m_StandardList.size() > 0 )
		{
			m_MaskLayer.setVisibility(View.VISIBLE);
			dialog_standard.showAtLocation(findViewById(R.id.ll_salereject_add_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		}
	}
	
	private void onClickLargenumberSelect()
	{
		if ( m_LargenumberList.size() > 0 )
		{
			m_MaskLayer.setVisibility(View.VISIBLE);
			dialog_largenumber.showAtLocation(findViewById(R.id.ll_salereject_add_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		}
	}
	
	private void setDialogStandardAdapter()
	{
		View popupview = View.inflate(this, R.layout.dialog_select, null);
		ResolutionSet._instance.iterateChild(popupview);
		
		AutoSizeTextView txt_title = (AutoSizeTextView)popupview.findViewById(R.id.txt_dialog_title);
		txt_title.setText(getResources().getString(R.string.dialog_select_standard));

        ImageView img_cancel = (ImageView) popupview.findViewById(R.id.img_dialog_cancel);
        img_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog_standard != null && dialog_standard.isShowing() )
                {
                    m_MaskLayer.setVisibility(View.INVISIBLE);
                    dialog_standard.dismiss();
                }
            }
        });
		
		ArrayList<String> arGeneral = new ArrayList<String>();
		
		int cnt = m_StandardList.size();
		for ( int i = 0; i < cnt; i++ )
			arGeneral.add(m_StandardList.get(i).standard);
		
		DialogSelectAdapter Adpater = new DialogSelectAdapter(this, arGeneral);
		
		ListView list = (ListView)popupview.findViewById(R.id.lv_dialog_listview);
		list.setAdapter(Adpater);
		list.setDrawSelectorOnTop(true);
		list.setDivider(new ColorDrawable(getResources().getColor(R.color.dialog_line)));
		list.setCacheColorHint(Color.TRANSPARENT);
        list.setDividerHeight(1);
		
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onClickStandardItem(position);
        	}
		});
		
		dialog_standard = new PopupWindow(popupview, R.dimen.common_popup_dialog_width, R.dimen.common_popup_dialog_height,true);
		dialog_standard.setAnimationStyle(-1);
		if ( cnt > 0 )
		{
			m_StandardPos = 0;
			txt_standard.setText(m_StandardList.get(m_StandardPos).standard);
		}
		else
			txt_standard.setText("");
	}
	
	private void setDialogLargenumberAdapter()
	{
		View popupview = View.inflate(this, R.layout.dialog_select, null);
		ResolutionSet._instance.iterateChild(popupview);
		
		AutoSizeTextView txt_title = (AutoSizeTextView)popupview.findViewById(R.id.txt_dialog_title);
		txt_title.setText(getResources().getString(R.string.dialog_select_largenumber));

        ImageView img_cancel = (ImageView) popupview.findViewById(R.id.img_dialog_cancel);
        img_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog_largenumber != null && dialog_largenumber.isShowing() )
                {
                    m_MaskLayer.setVisibility(View.INVISIBLE);
                    dialog_largenumber.dismiss();
                }
            }
        });
		
		ArrayList<String> arGeneral = new ArrayList<String>();
		
		int cnt = m_LargenumberList.size();
		for ( int i = 0; i < cnt; i++ )
			arGeneral.add(m_LargenumberList.get(i));
		
		DialogSelectAdapter Adpater = new DialogSelectAdapter(this, arGeneral);
		
		ListView list = (ListView)popupview.findViewById(R.id.lv_dialog_listview);
		list.setAdapter(Adpater);
		list.setDrawSelectorOnTop(true);
		list.setDivider(new ColorDrawable(getResources().getColor(R.color.dialog_line)));
		list.setCacheColorHint(Color.TRANSPARENT);
        list.setDividerHeight(1);
		
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onClickLargenumberItem(position);
        	}
		});
		
		dialog_largenumber = new PopupWindow(popupview, R.dimen.common_popup_dialog_width, R.dimen.common_popup_dialog_height,true);
		dialog_largenumber.setAnimationStyle(-1);
		if ( cnt > 0 )
		{
			m_LargenumberPos = 0;
			txt_largenumber.setText(m_LargenumberList.get(m_LargenumberPos));
		}
		else
			txt_largenumber.setText("");
	}
	
	private void onClickStandardItem(int pos)
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		dialog_standard.dismiss();
		
		txt_standard.setText(m_StandardList.get(pos).standard);
		m_StandardPos = pos;
		
		m_reqType = REQ_TYPE.REQ_GETLARGENUMBERLIST;
		new LoadResponseThread(SaleRejectAddActivity.this).start();
	}
	
	private void onClickLargenumberItem(int pos)
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		dialog_largenumber.dismiss();
		
		txt_largenumber.setText(m_LargenumberList.get(pos));
		m_LargenumberPos = pos;
	}
	
	public void refreshUI() {
		super.refreshUI();
		
		if ( m_reqType == REQ_TYPE.REQ_GETCATALOGINFOFROMBARCODE )
		{
			if ( m_CatalogId >= 0 )
			{
				m_reqType = REQ_TYPE.REQ_GETREMAINCATALOGSTANDARDLIST;
				new LoadResponseThread(SaleRejectAddActivity.this).start();
			}
			else
			{
				m_StandardList.clear();
				m_LargenumberList.clear();
			}
			
			setDialogStandardAdapter();
			setDialogLargenumberAdapter();
			txt_catalogname.setText(m_CatalogName);
		}
		else if ( m_reqType == REQ_TYPE.REQ_GETREMAINCATALOGSTANDARDLIST )
		{
			setDialogStandardAdapter();
			
			if ( m_StandardList.size() > 0 )
			{
				m_reqType = REQ_TYPE.REQ_GETLARGENUMBERLIST;
				new LoadResponseThread(SaleRejectAddActivity.this).start();
			}
		}
		else if ( m_reqType == REQ_TYPE.REQ_GETLARGENUMBERLIST )
		{
			setDialogLargenumberAdapter();
		}
	}
	
	public void getResponseJSON() {
		try {
			if ( m_reqType == REQ_TYPE.REQ_GETCATALOGINFOFROMBARCODE )
			{
				String strRequest = HttpConnUsingJSON.REQ_GETCATALOGINFOFROMBARCODE;
				strRequest += "?shop_id=" + String.valueOf(Global.Cur_ShopId);
				strRequest += "&barcode=" + txt_barcode.getText().toString();
				
				JSONObject response = m_HttpConnUsingJSON.getGetJSONObject(strRequest);
				
				if (response == null) {
					m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
					return;					
				}
				m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
				
				m_CatalogName = "";
				m_CatalogId = -1;
				
	            if (m_nResponse == ResponseRet.RET_SUCCESS) {
	            	JSONObject dataObject = response.getJSONObject(ResponseData.RESPONSE_DATA);
					
	            	m_CatalogName = dataObject.getString("catalog_name");
					m_CatalogId = dataObject.getInt("catalog_id");
				}
			}
			else if ( m_reqType == REQ_TYPE.REQ_GETREMAINCATALOGSTANDARDLIST )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETREMAINCATALOGSTANDARDLIST;
				strRequest += "?shop_id=" + String.valueOf(Global.Cur_ShopId);
				strRequest += "&store_id=0";
				strRequest += "&catalog_id=" + String.valueOf(m_CatalogId);
				
				JSONObject response = m_HttpConnUsingJSON.getGetJSONObject(strRequest);
				if (response == null) {
					m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
					return;
				}
				
				m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
				if (m_nResponse == ResponseRet.RET_SUCCESS) {
					
					JSONObject dataObject = response.getJSONObject(ResponseData.RESPONSE_DATA);
					
					int count = dataObject.getInt("standard_count");
					JSONArray dataList = dataObject.getJSONArray("standard_data");
					
					m_StandardList.clear();
					
					for (int i = 0; i < count; i++)
					{
						STStandardInfo itemInfo = new STStandardInfo();
						JSONObject tmpObj = dataList.getJSONObject(i);
						
						itemInfo.standard_id = tmpObj.getInt("standard_id");
						itemInfo.standard = tmpObj.getString("standard");
						
						m_StandardList.add(itemInfo);
					}
				}
			}
			else if ( m_reqType == REQ_TYPE.REQ_GETLARGENUMBERLIST )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETLARGENUMBERLIST;
				strRequest += "?shop_id=" + String.valueOf(Global.Cur_ShopId);
				strRequest += "&store_id=0";
				strRequest += "&catalog_id=" + String.valueOf(m_CatalogId);
				strRequest += "&standard_id=" + String.valueOf(m_StandardList.get(m_StandardPos).standard_id);
				
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
					
					m_LargenumberList.clear();
					
					for (int i = 0; i < count; i++)
					{
						String itemInfo = dataList.getString(i);
						
						m_LargenumberList.add(itemInfo);
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
