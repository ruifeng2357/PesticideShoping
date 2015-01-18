package com.damy.nongyao;

import java.util.ArrayList;

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

import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;

public class StoreUsingAddActivity extends BaseActivity {
	private enum REQ_TYPE{REQ_GETSTORELIST, REQ_GETCATALOGLISTFROMSTORE, REQ_GETREMAINCATALOGSTANDARDLIST, REQ_GETLARGENUMBERLIST};
	
	private AutoSizeTextView			txt_storename;
	private AutoSizeTextView 			txt_catalogname;
	private AutoSizeTextView 			txt_standard;
	private AutoSizeTextView 			txt_largenumber;
	private AutoSizeEditText 			edit_quantity;
	private AutoSizeEditText 			edit_reason;
	
	private PopupWindow 				dialog_store;
	private PopupWindow 				dialog_catalog;
	private PopupWindow 				dialog_standard;
	private PopupWindow 				dialog_largenumber;
    
	private LinearLayout 				m_MaskLayer;
	
	private int 						m_CurStorePos = -1;
	private int 						m_CurCatalogPos = -1;
    private int							m_StandardPos = -1;
    private int							m_LargenumberPos = -1;
	
	private ArrayList<STStoreInfo>	 	m_StoreList = new ArrayList<STStoreInfo>();
    private ArrayList<STCatalogInfo> 	m_CatalogList = new ArrayList<STCatalogInfo>();
    private ArrayList<STStandardInfo>	m_StandardList = new ArrayList<STStandardInfo>();
    private ArrayList<String>			m_LargenumberList = new ArrayList<String>();
    
    private REQ_TYPE 					m_reqType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_store_using_add);
		
		ResolutionSet._instance.iterateChild(findViewById(R.id.fl_storeusing_add));
		
		initControls();
		readContents();
	}
	
	private void initControls()
	{
		txt_storename = (AutoSizeTextView)findViewById(R.id.txt_storeusing_add_storename);
		txt_catalogname = (AutoSizeTextView)findViewById(R.id.txt_storeusing_add_catalogname);
		txt_standard = (AutoSizeTextView)findViewById(R.id.txt_storeusing_add_standard);
		txt_largenumber = (AutoSizeTextView)findViewById(R.id.txt_storeusing_add_largenumber);
		edit_quantity = (AutoSizeEditText)findViewById(R.id.edit_storeusing_add_amount);
		edit_reason = (AutoSizeEditText)findViewById(R.id.edit_storeusing_add_reason);
		
		/*
		m_IsEdit = getIntent().getIntExtra(STOREUSING_ADD_ISEDIT, 0);
		
		if ( m_IsEdit == 1 )
		{
			txt_storename.setText(Global.StoreUsing_SelectItem.store_name);
			txt_catalogname.setText(Global.StoreUsing_SelectItem.catalog_name);
			edit_quantity.setText(String.valueOf(Global.StoreUsing_SelectItem.quantity));
			edit_reason.setText(Global.StoreUsing_SelectItem.reason);
		}
		*/

		FrameLayout fl_backbtn = (FrameLayout)findViewById(R.id.fl_storeusing_add_backbtn);
		FrameLayout fl_homebtn = (FrameLayout)findViewById(R.id.fl_storeusing_add_homebtn);
		FrameLayout fl_savebtn = (FrameLayout)findViewById(R.id.fl_storeusing_add_savebtn);
		FrameLayout fl_closebtn = (FrameLayout)findViewById(R.id.fl_storeusing_add_closebtn);

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
		
		LinearLayout ll_storeselect = (LinearLayout)findViewById(R.id.ll_storeusing_add_storeselect);
		ll_storeselect.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickStoreSelect();
        	}
        });
		
		LinearLayout ll_catalogselect = (LinearLayout)findViewById(R.id.ll_storeusing_add_catalogselect);
		ll_catalogselect.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickCatalogSelect();
        	}
        });
		
		LinearLayout ll_standardselect = (LinearLayout)findViewById(R.id.ll_storeusing_add_standardselect);
		ll_standardselect.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickStandardSelect();
        	}
        });
		
		LinearLayout ll_largenumberselect = (LinearLayout)findViewById(R.id.ll_storeusing_add_largenumber);
		ll_largenumberselect.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickLargenumberSelect();
        	}
        });
		
		m_MaskLayer = (LinearLayout)findViewById(R.id.ll_storeusing_add_masklayer);
		m_MaskLayer.setVisibility(View.INVISIBLE);
	}
	
	private void readContents()
	{
		m_reqType = REQ_TYPE.REQ_GETSTORELIST;
		new LoadResponseThread(StoreUsingAddActivity.this).start();
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
		
		if ( edit_reason.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_required_reason));
			return;
		}
		
		Global.StoreUsing_SelectItem.catalog_id = m_CatalogList.get(m_CurCatalogPos).catalog_id;
		Global.StoreUsing_SelectItem.catalog_name = m_CatalogList.get(m_CurCatalogPos).catalog_name;
		Global.StoreUsing_SelectItem.catalog_num = m_CatalogList.get(m_CurCatalogPos).catalog_num;
		Global.StoreUsing_SelectItem.standard_id = m_StandardList.get(m_StandardPos).standard_id;
		Global.StoreUsing_SelectItem.largenumber = m_LargenumberList.get(m_LargenumberPos);
		Global.StoreUsing_SelectItem.store_id = m_StoreList.get(m_CurStorePos).id;
		Global.StoreUsing_SelectItem.store_name = m_StoreList.get(m_CurStorePos).name;
		Global.StoreUsing_SelectItem.quantity = Integer.valueOf(edit_quantity.getText().toString());
		Global.StoreUsing_SelectItem.reason = edit_reason.getText().toString();
		
		Global.SotreUsing_isSelected = true;
		
		finish();
	}
	
	private void onClickClose()
	{
		finish();
	}
	
	private void onClickStoreSelect()
	{
		if ( m_StoreList.size() > 0 )
		{
			m_MaskLayer.setVisibility(View.VISIBLE);
			dialog_store.showAtLocation(findViewById(R.id.ll_storeusing_add_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		}
	}
	
	private void onClickCatalogSelect()
	{
		if ( m_CatalogList.size() > 0 )
		{
			m_MaskLayer.setVisibility(View.VISIBLE);
			dialog_catalog.showAtLocation(findViewById(R.id.ll_storeusing_add_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		}
	}
	
	private void onClickStandardSelect()
	{
		if ( m_StandardList.size() > 0 )
		{
			m_MaskLayer.setVisibility(View.VISIBLE);
			dialog_standard.showAtLocation(findViewById(R.id.ll_storeusing_add_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		}
	}
	
	private void onClickLargenumberSelect()
	{
		if ( m_LargenumberList.size() > 0 )
		{
			m_MaskLayer.setVisibility(View.VISIBLE);
			dialog_largenumber.showAtLocation(findViewById(R.id.ll_storeusing_add_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		}
	}
	
	private void setDialogStoreAdapter()
	{
		View popupview = View.inflate(this, R.layout.dialog_select, null);
		ResolutionSet._instance.iterateChild(popupview);
		
		AutoSizeTextView txt_title = (AutoSizeTextView)popupview.findViewById(R.id.txt_dialog_title);
		txt_title.setText(getResources().getString(R.string.dialog_select_store));

        ImageView img_cancel = (ImageView) popupview.findViewById(R.id.img_dialog_cancel);
        img_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog_store != null && dialog_store.isShowing() )
                {
                    m_MaskLayer.setVisibility(View.INVISIBLE);
                    dialog_store.dismiss();
                }
            }
        });
		
		ArrayList<String> arGeneral = new ArrayList<String>();
		
		int cnt = m_StoreList.size();
		for ( int i = 0; i < cnt; i++ )
			arGeneral.add(m_StoreList.get(i).name);
		
		DialogSelectAdapter Adpater = new DialogSelectAdapter(this, arGeneral);
		
		ListView list = (ListView)popupview.findViewById(R.id.lv_dialog_listview);
		list.setAdapter(Adpater);
		list.setDrawSelectorOnTop(true);
		list.setDivider(new ColorDrawable(getResources().getColor(R.color.dialog_line)));
		list.setCacheColorHint(Color.TRANSPARENT);
        list.setDividerHeight(1);
		
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onClickStoreItem(position);
        	}
		});
		
		dialog_store = new PopupWindow(popupview, R.dimen.common_popup_dialog_width, R.dimen.common_popup_dialog_height,true);
		dialog_store.setAnimationStyle(-1);
		
		if ( m_StoreList.size() > 0 )
		{
			m_CurStorePos = 0;
			txt_storename.setText(m_StoreList.get(m_CurStorePos).name);
		}
		else
		{
			txt_storename.setText("");
			txt_catalogname.setText("");
			txt_standard.setText("");
			txt_largenumber.setText("");
		}
	}

	private void setDialogCatalogAdapter()
	{
		View popupview = View.inflate(this, R.layout.dialog_select, null);
		ResolutionSet._instance.iterateChild(popupview);
		
		AutoSizeTextView txt_title = (AutoSizeTextView)popupview.findViewById(R.id.txt_dialog_title);
		txt_title.setText(getResources().getString(R.string.dialog_select_catalog));

        ImageView img_cancel = (ImageView) popupview.findViewById(R.id.img_dialog_cancel);
        img_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog_catalog != null && dialog_catalog.isShowing() )
                {
                    m_MaskLayer.setVisibility(View.INVISIBLE);
                    dialog_catalog.dismiss();
                }
            }
        });
		
		ArrayList<String> arGeneral = new ArrayList<String>();
		
		int cnt = m_CatalogList.size();
		for ( int i = 0; i < cnt; i++ )
			arGeneral.add(m_CatalogList.get(i).catalog_name);
		
		DialogSelectAdapter Adapter = new DialogSelectAdapter(this, arGeneral);
		
		ListView list = (ListView)popupview.findViewById(R.id.lv_dialog_listview);
		list.setAdapter(Adapter);
		list.setDrawSelectorOnTop(true);
		list.setDivider(new ColorDrawable(getResources().getColor(R.color.dialog_line)));
		list.setCacheColorHint(Color.TRANSPARENT);
        list.setDividerHeight(1);
		
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onClickCatalogItem(position);
        	}
		});
		
		dialog_catalog = new PopupWindow(popupview, R.dimen.common_popup_dialog_width, R.dimen.common_popup_dialog_height,true);
		dialog_catalog.setAnimationStyle(-1);
		if ( cnt > 0 )
		{
			m_CurCatalogPos = 0;
			txt_catalogname.setText(m_CatalogList.get(m_CurCatalogPos).catalog_name);
		}
		else
			txt_catalogname.setText("");
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
	
	private void onClickStoreItem(int pos)
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		dialog_store.dismiss();

		txt_storename.setText(m_StoreList.get(pos).name);
		
		m_reqType = REQ_TYPE.REQ_GETCATALOGLISTFROMSTORE;
		new LoadResponseThread(StoreUsingAddActivity.this).start();
		
		m_CurStorePos = pos;
	}
	
	private void onClickCatalogItem(int pos)
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		dialog_catalog.dismiss();
		
		txt_catalogname.setText(m_CatalogList.get(pos).catalog_name);
		m_CurCatalogPos = pos;
		
		m_reqType = REQ_TYPE.REQ_GETREMAINCATALOGSTANDARDLIST;
		new LoadResponseThread(StoreUsingAddActivity.this).start();
	}
	
	private void onClickStandardItem(int pos)
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		dialog_standard.dismiss();
		
		txt_standard.setText(m_StandardList.get(pos).standard);
		m_StandardPos = pos;
		
		m_reqType = REQ_TYPE.REQ_GETLARGENUMBERLIST;
		new LoadResponseThread(StoreUsingAddActivity.this).start();
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
		
		if ( m_reqType == REQ_TYPE.REQ_GETSTORELIST )
		{
			setDialogStoreAdapter();
			m_CurStorePos = 0;
			
			if ( m_StoreList.size() > 0 )
			{
				m_reqType = REQ_TYPE.REQ_GETCATALOGLISTFROMSTORE;
				new LoadResponseThread(StoreUsingAddActivity.this).start();
			}
		}
		else if ( m_reqType == REQ_TYPE.REQ_GETCATALOGLISTFROMSTORE )
		{
			setDialogCatalogAdapter();
			
			if ( m_CatalogList.size() > 0 )
			{
				m_reqType = REQ_TYPE.REQ_GETREMAINCATALOGSTANDARDLIST;
				new LoadResponseThread(StoreUsingAddActivity.this).start();
			}
		}
		else if ( m_reqType == REQ_TYPE.REQ_GETREMAINCATALOGSTANDARDLIST )
		{
			setDialogStandardAdapter();
			
			if ( m_StandardList.size() > 0 )
			{
				m_reqType = REQ_TYPE.REQ_GETLARGENUMBERLIST;
				new LoadResponseThread(StoreUsingAddActivity.this).start();
			}
		}
		else if ( m_reqType == REQ_TYPE.REQ_GETLARGENUMBERLIST )
		{
			setDialogLargenumberAdapter();
		}
	}
	
	public void getResponseJSON() {
		try {
			if ( m_reqType == REQ_TYPE.REQ_GETSTORELIST )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETSTORELIST;
				strRequest += "?shop_id=" + String.valueOf(Global.Cur_ShopId);
				
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
						STStoreInfo itemInfo = new STStoreInfo();
						JSONObject tmpObj = dataList.getJSONObject(i);
						
						itemInfo.id = tmpObj.getInt("store_id");
						itemInfo.name = tmpObj.getString("name");
						
						m_StoreList.add(itemInfo);
					}
				}
			}
			else if ( m_reqType == REQ_TYPE.REQ_GETCATALOGLISTFROMSTORE )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETCATALOGLISTFROMSTORE;
				strRequest += "?shop_id=" + String.valueOf(Global.Cur_ShopId);
				strRequest += "&store_id=" + String.valueOf(m_StoreList.get(m_CurStorePos).id);
				strRequest += "&search_name=";
				
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
					
					m_CatalogList.clear();
					
					for (int i = 0; i < count; i++)
					{
						STCatalogInfo itemInfo = new STCatalogInfo();
						JSONObject tmpObj = dataList.getJSONObject(i);
						
						itemInfo.catalog_id = tmpObj.getInt("catalog_id");
						itemInfo.catalog_num = tmpObj.getString("catalog_num");
						itemInfo.catalog_name = tmpObj.getString("catalog_name");
						
						m_CatalogList.add(itemInfo);
					}
				}
			}
			else if ( m_reqType == REQ_TYPE.REQ_GETREMAINCATALOGSTANDARDLIST )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETREMAINCATALOGSTANDARDLIST;
				strRequest += "?shop_id=" + String.valueOf(Global.Cur_ShopId);
				strRequest += "&store_id=" + String.valueOf(m_StoreList.get(m_CurStorePos).id);
				strRequest += "&catalog_id=" + String.valueOf(m_CatalogList.get(m_CurCatalogPos).catalog_id);
				
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
				strRequest += "&store_id=" + String.valueOf(m_StoreList.get(m_CurStorePos).id);
				strRequest += "&catalog_id=" + String.valueOf(m_CatalogList.get(m_CurCatalogPos).catalog_id);
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
