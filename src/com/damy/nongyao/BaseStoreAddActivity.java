package com.damy.nongyao;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.damy.adapters.StoreSelectAdapter;
import com.damy.adapters.DialogSelectAdapter;
import com.damy.backend.LoadResponseThread;
import com.damy.Utils.AutoSizeEditText;
import com.damy.Utils.AutoSizeTextView;
import com.damy.Utils.ResolutionSet;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;
import com.damy.datatypes.STRegionInfo;
import com.damy.datatypes.STUserSimpleInfo;

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

public class BaseStoreAddActivity extends BaseActivity {
	
	public static String BASESTORE_ADD_ID = "BASESTORE_ADD_ID";
	public static String BASESTORE_ADD_STORENAME = "BASESTORE_ADD_STORENAME";
	public static String BASESTORE_ADD_MANAGERID = "BASESTORE_ADD_MANAGERID";
	public static String BASESTORE_ADD_MANAGERNAME = "BASESTORE_ADD_MANAGERNAME";
	
	private enum REQ_TYPE{REQ_GETUSERLIST, REQ_ADDEDITSTORE};
	
	private long 						m_itemId = 0;
	private String 						m_itemName = "";
	private String 						m_itemManId = "";
	private String 						m_itemManName = "";
	
	public String 						m_CurSelManId = "";
	
	private AutoSizeEditText 			edit_Name;
	private AutoSizeTextView 			txt_ManName;
	
	private PopupWindow 				dialog_manager;
    private LinearLayout				m_MaskLayer;
	
	private REQ_TYPE 					m_reqType;
	
	private ArrayList<STUserSimpleInfo> 	m_ManagerList = new ArrayList<STUserSimpleInfo>();
	public ArrayList<Boolean> 				m_CheckList = new ArrayList<Boolean>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_store_add);
		
		ResolutionSet._instance.iterateChild(findViewById(R.id.fl_basestore_add));
		
		initControls();
		readContent();
	}
	
	private void initControls()
	{
		edit_Name = (AutoSizeEditText)findViewById(R.id.edit_basestore_add_name);
		txt_ManName = (AutoSizeTextView)findViewById(R.id.txt_basestore_add_manager);

		m_itemId = getIntent().getLongExtra(BASESTORE_ADD_ID, 0);
		
		if ( m_itemId > 0 )
		{
			m_itemName = getIntent().getStringExtra(BASESTORE_ADD_STORENAME);
			m_itemManId = getIntent().getStringExtra(BASESTORE_ADD_MANAGERID);
			m_itemManName = getIntent().getStringExtra(BASESTORE_ADD_MANAGERNAME);
			
			edit_Name.setText(m_itemName);
			txt_ManName.setText(m_itemManName);
			
			m_CurSelManId = m_itemManId;
		}

		FrameLayout fl_backbtn = (FrameLayout)findViewById(R.id.fl_basestore_add_backbtn);
		FrameLayout fl_homebtn = (FrameLayout)findViewById(R.id.fl_basestore_add_homebtn);
		FrameLayout fl_savebtn = (FrameLayout)findViewById(R.id.fl_basestore_add_savebtn);
		FrameLayout fl_closebtn = (FrameLayout)findViewById(R.id.fl_basestore_add_closebtn);

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
		
		m_MaskLayer = (LinearLayout)findViewById(R.id.ll_basestore_add_masklayer);
		m_MaskLayer.setVisibility(View.INVISIBLE);
		
		
		LinearLayout ll_storemansel = (LinearLayout)findViewById(R.id.ll_basestore_storemansel);
		ll_storemansel.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSelectUser();
        	}
        });
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
		if ( edit_Name.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_required_userid));
			return;
		}
		
		if ( edit_Name.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_required_storemanager));
			return;
		}
		
		m_reqType = REQ_TYPE.REQ_ADDEDITSTORE;
		new LoadResponseThread(BaseStoreAddActivity.this).start();
	}
	
	private void onClickClose()
	{
		finish();
	}
	
	private void onClickSelectUser()
	{
		if ( m_ManagerList.size() <= 0 )
			return;
		
		setDialogManagerAdapter();
		
		m_MaskLayer.setVisibility(View.VISIBLE);
		dialog_manager.showAtLocation(findViewById(R.id.ll_basestore_add_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
	}
	
	private void readContent()
	{
		m_reqType = REQ_TYPE.REQ_GETUSERLIST;
		new LoadResponseThread(BaseStoreAddActivity.this).start();
	}
	
	private void setDialogManagerAdapter()
	{
		View popupview = View.inflate(this, R.layout.dialog_checkselect, null);
		ResolutionSet._instance.iterateChild(popupview);
		
		AutoSizeTextView txt_title = (AutoSizeTextView)popupview.findViewById(R.id.txt_dialog_checkselect_title);
		txt_title.setText(getResources().getString(R.string.dialog_select_storemanager));
		
		ArrayList<String> arGeneral = new ArrayList<String>();
		
		m_CheckList.clear();
		
		int cnt = m_ManagerList.size();
		for ( int i = 0; i < cnt; i++ )
		{
			arGeneral.add(m_ManagerList.get(i).name);
			
			if ( m_CurSelManId.contains(String.valueOf(m_ManagerList.get(i).id)) )
				m_CheckList.add(true);
			else
				m_CheckList.add(false);
		}
		
		if ( m_itemId == 0 && cnt > 0 )
		{
			txt_ManName.setText(arGeneral.get(0));
		}
		
		StoreSelectAdapter Adapter = new StoreSelectAdapter(this, arGeneral);
		
		ListView list = (ListView)popupview.findViewById(R.id.lv_dialog_checkselect_listview);
		list.setAdapter(Adapter);
		list.setDrawSelectorOnTop(true);
		list.setDivider(new ColorDrawable(getResources().getColor(R.color.dialog_line)));
		list.setCacheColorHint(Color.TRANSPARENT);
        list.setDividerHeight(1);
        
		/*
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onClickUserItem(position);
        	}
		});
		*/
        
        FrameLayout fl_delconfirm_ok = (FrameLayout)popupview.findViewById(R.id.fl_dialog_checkselect_okbtn);
		FrameLayout fl_delconfirm_cancel = (FrameLayout)popupview.findViewById(R.id.fl_dialog_checkselect_cancelbtn);
		
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
        
		dialog_manager = new PopupWindow(popupview, R.dimen.common_popup_checkdialog_width, R.dimen.common_popup_checkdialog_height,true);
		dialog_manager.setAnimationStyle(-1);
	}
	
	public void onChangeCheckbox(int pos, boolean isCheck)
	{
		m_CheckList.set(pos, isCheck);
	}
	
	private void onClickDelConfirmOk()
	{
		int cnt = m_ManagerList.size();
		m_CurSelManId = "";
		String SelManName = "";
		for ( int i = 0; i < cnt; i++ )
		{
			if ( m_CheckList.get(i) )
			{
				m_CurSelManId += String.valueOf(m_ManagerList.get(i).id) + ",";
				SelManName += m_ManagerList.get(i).name + ", ";
			}
		}
		
		if ( !m_CurSelManId.equals("") )
		{
			int len = m_CurSelManId.length();
			m_CurSelManId = m_CurSelManId.substring(0, len - 1);
			
			len = SelManName.length();
			SelManName = SelManName.substring(0, len - 2);
			
			m_MaskLayer.setVisibility(View.INVISIBLE);
			dialog_manager.dismiss();
			
			txt_ManName.setText(SelManName);
		}

	}
	
	private void onClickDelConfirmCancel()
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		dialog_manager.dismiss();
	}
	
	/*
	private void onClickUserItem(int pos)
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		dialog_manager.dismiss();
		txt_ManName.setText(m_ManagerList.get(pos).name);
		m_CurSelManId += String.valueOf(m_ManagerList.get(pos).id) + ",";
	}
	*/
	
	public void refreshUI() {
		super.refreshUI();
		
		if ( m_reqType == REQ_TYPE.REQ_GETUSERLIST )
		{
			if ( m_CurSelManId.equals("") && m_ManagerList.size() > 0)
				m_CurSelManId = String.valueOf(m_ManagerList.get(0).id);
			
			setDialogManagerAdapter();
		}
		else if ( m_reqType == REQ_TYPE.REQ_ADDEDITSTORE )
		{
			if ( m_nResponse == ResponseRet.RET_SUCCESS )
				finish();
		}
	}
	
	public void getResponseJSON() {
		try {
			if ( m_reqType == REQ_TYPE.REQ_GETUSERLIST )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strUrl = HttpConnUsingJSON.REQ_GETUSERLIST;
				strUrl += "?shop_id=" + String.valueOf(Global.Cur_ShopId);
				strUrl += "&role=store";
				
				
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
						STUserSimpleInfo itemInfo = new STUserSimpleInfo();
						JSONObject tmpObj = dataList.getJSONObject(i);
						
						itemInfo.id = tmpObj.getInt("uid");
						itemInfo.name = tmpObj.getString("name");
						
						m_ManagerList.add(itemInfo);
					}
				}
			}
			else if ( m_reqType == REQ_TYPE.REQ_ADDEDITSTORE )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strUrl = "";
				if ( m_itemId == 0 )
					strUrl = HttpConnUsingJSON.REQ_ADDSTORE;
				else
					strUrl = HttpConnUsingJSON.REQ_EDITSTORE;
				
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
		
		if ( m_itemId > 0 )
			requestObj.put("store_id", String.valueOf(m_itemId));
    	
		requestObj.put("shop_id", String.valueOf(Global.Cur_ShopId));
		requestObj.put("storename", edit_Name.getText().toString());
		requestObj.put("uid", m_CurSelManId);
		
		return requestObj;
	}

}
